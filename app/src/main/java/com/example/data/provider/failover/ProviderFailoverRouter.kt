package com.example.data.provider.failover

import com.example.data.controltower.DataQualityGate
import com.example.data.controltower.FreshnessEngine
import com.example.data.controltower.QualityValidationResult
import com.example.data.provider.core.*
import com.example.domain.model.SecurityQuote
import kotlinx.coroutines.withTimeoutOrNull

data class FailoverRecord(
    val timestamp: Long,
    val symbol: String,
    val primaryProvider: String,
    val failureReason: String,
    val secondaryProvider: String,
    val isSecondarySuccess: Boolean
)

data class RoutedQuoteResult(
    val quote: SecurityQuote?,
    val activeProvider: String,
    val isFailoverActive: Boolean,
    val latencyMs: Long,
    val isAvailable: Boolean,
    val failureReason: String? = null
)

class ProviderFailoverRouter(
    val primaryMarketProvider: MarketDataProvider,
    val secondaryMarketProvider: MarketDataProvider,
    val newsProvider: NewsProvider,
    val fundamentalProvider: FundamentalDataProvider,
    val macroProvider: MacroDataProvider
) {

    private val _failoverLog = mutableListOf<FailoverRecord>()
    val failoverLog: List<FailoverRecord> get() = _failoverLog.toList()

    /**
     * Executes the strict failover pipeline for market quotes.
     */
    suspend fun getRoutedQuote(symbol: String): RoutedQuoteResult {
        // Step 1: Attempt Primary Provider with 6s timeout
        val primaryResult = withTimeoutOrNull(6000) {
            runCatching { primaryMarketProvider.fetchQuote(symbol) }.getOrNull()
        }

        if (primaryResult is ProviderResult.Success) {
            val quote = primaryResult.data
            val qualityResult = DataQualityGate.validateQuote(quote)
            val isFresh = FreshnessEngine.isAdmissibleForDecision(quote.freshness)

            if (qualityResult is QualityValidationResult.Valid && isFresh) {
                return RoutedQuoteResult(
                    quote = quote,
                    activeProvider = primaryMarketProvider.providerName,
                    isFailoverActive = false,
                    latencyMs = primaryResult.metadata.latencyMs,
                    isAvailable = true
                )
            }
        }

        // Step 2: Primary Failed or Disqualified -> Engage Failover
        val primaryReason = when (primaryResult) {
            is ProviderResult.FailoverRequired -> primaryResult.reason
            is ProviderResult.CurrentDataUnavailable -> primaryResult.reason
            null -> "Primary feed request timed out (>6000ms)"
            is ProviderResult.Success -> "Primary quote failed Data Quality Gate or Freshness check"
        }

        val secondaryResult = withTimeoutOrNull(6000) {
            runCatching { secondaryMarketProvider.fetchQuote(symbol) }.getOrNull()
        }

        val secondarySuccess = secondaryResult is ProviderResult.Success

        // Record Failover Event in audit memory
        _failoverLog.add(
            FailoverRecord(
                timestamp = System.currentTimeMillis(),
                symbol = symbol,
                primaryProvider = primaryMarketProvider.providerName,
                failureReason = primaryReason,
                secondaryProvider = secondaryMarketProvider.providerName,
                isSecondarySuccess = secondarySuccess
            )
        )

        if (secondaryResult is ProviderResult.Success) {
            val secondaryQuote = secondaryResult.data
            val quality = DataQualityGate.validateQuote(secondaryQuote)
            val fresh = FreshnessEngine.isAdmissibleForDecision(secondaryQuote.freshness)

            if (quality is QualityValidationResult.Valid && fresh) {
                return RoutedQuoteResult(
                    quote = secondaryQuote,
                    activeProvider = secondaryMarketProvider.providerName,
                    isFailoverActive = true,
                    latencyMs = secondaryResult.metadata.latencyMs,
                    isAvailable = true,
                    failureReason = "Primary failed: $primaryReason. Running on Secondary."
                )
            }
        }

        // Step 3: Both Failed -> CURRENT DATA UNAVAILABLE
        return RoutedQuoteResult(
            quote = null,
            activeProvider = "NONE (ALL PROVIDERS FAILED)",
            isFailoverActive = true,
            latencyMs = 0L,
            isAvailable = false,
            failureReason = "CURRENT DATA UNAVAILABLE: Both primary and secondary market data feeds failed verification."
        )
    }
}
