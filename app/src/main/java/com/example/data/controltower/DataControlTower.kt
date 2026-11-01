package com.example.data.controltower

import com.example.data.local.db.OmniVestDatabase
import com.example.data.local.entity.DataHealthEntity
import com.example.data.local.entity.QuoteEntity
import com.example.data.provider.failover.ProviderFailoverRouter
import com.example.data.provider.failover.RoutedQuoteResult
import com.example.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

class DataControlTower(
    private val database: OmniVestDatabase,
    val failoverRouter: ProviderFailoverRouter
) {

    // Concurrency limiter to protect mobile CPU, memory, and battery (Layer 43)
    private val networkSemaphore = Semaphore(3)

    /**
     * Retrieves normalized quote for a security, auditing freshness on the fly.
     * If cached quote is aging or stale, automatically routes through ProviderFailoverRouter.
     */
    suspend fun getVerifiedQuote(symbol: String, forceRefresh: Boolean = false): SecurityQuote? {
        val cached = database.quoteDao().getQuoteSnapshot(symbol)
        val now = System.currentTimeMillis()

        val isFresh = if (cached != null) {
            val state = FreshnessEngine.evaluateFreshness(
                dataClass = DataClass.INTRADAY_PRICE,
                publishedAt = cached.publishedAt,
                retrievedAt = cached.retrievedAt,
                currentTime = now
            )
            state == FreshnessState.LIVE || state == FreshnessState.FRESH
        } else false

        if (isFresh && !forceRefresh && cached != null) {
            return mapEntityToQuote(cached)
        }

        // Cache missing or stale -> Route through failover provider
        return syncQuoteWithFailover(symbol)
    }

    /**
     * Executes failover routing and persists verified quote into local Room database.
     */
    suspend fun syncQuoteWithFailover(symbol: String): SecurityQuote? = withContext(Dispatchers.IO) {
        networkSemaphore.withPermit {
            val routedResult: RoutedQuoteResult = failoverRouter.getRoutedQuote(symbol)

            if (routedResult.isAvailable && routedResult.quote != null) {
                val quote = routedResult.quote
                val entity = QuoteEntity(
                    symbol = quote.symbol,
                    price = quote.price,
                    changeAmount = quote.changeAmount,
                    changePercent = quote.changePercent,
                    dayOpen = quote.dayOpen,
                    dayHigh = quote.dayHigh,
                    dayLow = quote.dayLow,
                    volume = quote.volume,
                    fiftyTwoWeekHigh = quote.fiftyTwoWeekHigh,
                    fiftyTwoWeekLow = quote.fiftyTwoWeekLow,
                    peRatio = quote.peRatio,
                    marketCap = quote.marketCap,
                    source = routedResult.activeProvider,
                    sourceType = quote.freshness.sourceType.name,
                    retrievedAt = System.currentTimeMillis(),
                    publishedAt = quote.freshness.publishedAt,
                    effectiveAt = quote.freshness.effectiveAt,
                    freshnessStatus = quote.freshness.freshnessStatus.name,
                    reliability = quote.freshness.reliability.name,
                    confidence = quote.freshness.confidence
                )
                database.quoteDao().insertQuote(entity)

                // Update Feed Health record
                recordFeedAudit(
                    feedName = "US Market Tape",
                    providerName = routedResult.activeProvider,
                    isSuccess = true,
                    latencyMs = routedResult.latencyMs,
                    isFailover = routedResult.isFailoverActive,
                    errorMessage = routedResult.failureReason
                )

                quote
            } else {
                recordFeedAudit(
                    feedName = "US Market Tape",
                    providerName = routedResult.activeProvider,
                    isSuccess = false,
                    latencyMs = routedResult.latencyMs,
                    isFailover = true,
                    errorMessage = routedResult.failureReason
                )
                null
            }
        }
    }

    /**
     * Batch synchronization of multiple securities with concurrency cap.
     */
    suspend fun syncBatchQuotes(symbols: List<String>): List<SecurityQuote> = withContext(Dispatchers.IO) {
        symbols.map { sym ->
            async { syncQuoteWithFailover(sym) }
        }.awaitAll().filterNotNull()
    }

    /**
     * Reactive stream of verified quotes for multiple symbols.
     */
    fun observeQuotes(symbols: List<String>): Flow<List<SecurityQuote>> {
        return database.quoteDao().getQuotesForSymbols(symbols).map { entities ->
            entities.map { mapEntityToQuote(it) }
        }
    }

    /**
     * Returns true if a live trade/recommendation gate passes.
     * If false, returns CURRENT_DATA_UNAVAILABLE.
     */
    suspend fun verifyIntradayDataAvailability(symbol: String): Boolean {
        val quote = getVerifiedQuote(symbol) ?: return false
        return FreshnessEngine.isAdmissibleForDecision(quote.freshness)
    }

    /**
     * Records feed health updates in the Data Control Tower audit log.
     */
    suspend fun recordFeedAudit(
        feedName: String,
        providerName: String,
        isSuccess: Boolean,
        latencyMs: Long = 45L,
        isFailover: Boolean = false,
        errorMessage: String? = null
    ) {
        val now = System.currentTimeMillis()
        val existing = database.dataHealthDao().getHealthRecordsSnapshot().find { it.feedName == feedName }
        val failures = if (isSuccess) 0 else (existing?.failureCount ?: 0) + 1
        val freshness = if (isSuccess) FreshnessState.LIVE.name else FreshnessState.CURRENT_DATA_UNAVAILABLE.name

        val updated = DataHealthEntity(
            feedName = feedName,
            providerName = providerName,
            lastSyncTime = now,
            freshnessStatus = freshness,
            reliability = if (failures == 0) ReliabilityScore.HIGH.name else ReliabilityScore.LOW.name,
            itemsTracked = existing?.itemsTracked ?: 1,
            failureCount = failures,
            latencyMs = latencyMs,
            isFailoverActive = isFailover,
            lastError = errorMessage
        )
        database.dataHealthDao().insertHealthRecord(updated)
    }

    private fun mapEntityToQuote(entity: QuoteEntity): SecurityQuote {
        val currentFreshness = FreshnessEngine.evaluateFreshness(
            dataClass = DataClass.INTRADAY_PRICE,
            publishedAt = entity.publishedAt,
            retrievedAt = entity.retrievedAt
        )

        val metadata = FreshnessMetadata(
            source = entity.source,
            sourceType = runCatching { SourceType.valueOf(entity.sourceType) }.getOrDefault(SourceType.PRIMARY_EXCHANGE),
            publishedAt = entity.publishedAt,
            effectiveAt = entity.effectiveAt,
            retrievedAt = entity.retrievedAt,
            dataClass = DataClass.INTRADAY_PRICE,
            freshnessStatus = currentFreshness,
            reliability = runCatching { ReliabilityScore.valueOf(entity.reliability) }.getOrDefault(ReliabilityScore.HIGH),
            confidence = entity.confidence
        )

        return SecurityQuote(
            symbol = entity.symbol,
            price = entity.price,
            changeAmount = entity.changeAmount,
            changePercent = entity.changePercent,
            dayOpen = entity.dayOpen,
            dayHigh = entity.dayHigh,
            dayLow = entity.dayLow,
            volume = entity.volume,
            fiftyTwoWeekHigh = entity.fiftyTwoWeekHigh,
            fiftyTwoWeekLow = entity.fiftyTwoWeekLow,
            peRatio = entity.peRatio,
            marketCap = entity.marketCap,
            freshness = metadata
        )
    }
}
