package com.example.data.repository

import com.example.data.local.db.OmniVestDatabase
import com.example.data.local.entity.WatchlistEntity
import com.example.domain.model.AssetClass
import com.example.domain.model.Currency
import com.example.domain.model.Exchange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class PinnedWatchlistItem(
    val symbol: String,
    val securityName: String,
    val assetClass: AssetClass,
    val exchange: Exchange,
    val currency: Currency,
    val sector: String,
    val currentPrice: Double,
    val changePercent: Double,
    val pinnedAt: Long
)

class WatchlistRepository(
    private val database: OmniVestDatabase
) {

    fun observeAllPinned(): Flow<List<PinnedWatchlistItem>> {
        val watchlistFlow = database.watchlistDao().getAllWatchlist()
        val quotesFlow = database.quoteDao().getQuotesForSymbols(
            listOf("NVDA", "MSFT", "GOOGL", "AAPL", "QQQM", "VOO", "SCHG", "SMH", "RELIANCE.NS", "TCS.NS", "GLD")
        )

        return combine(watchlistFlow, quotesFlow) { pinnedEntities, quotes ->
            val quoteMap = quotes.associateBy { it.symbol }

            pinnedEntities.map { entity ->
                val quote = quoteMap[entity.symbol]
                PinnedWatchlistItem(
                    symbol = entity.symbol,
                    securityName = entity.securityName,
                    assetClass = runCatching { AssetClass.valueOf(entity.assetClass) }.getOrDefault(AssetClass.EQUITY_US),
                    exchange = runCatching { Exchange.valueOf(entity.exchange) }.getOrDefault(Exchange.NASDAQ),
                    currency = runCatching { Currency.valueOf(entity.currency) }.getOrDefault(Currency.USD),
                    sector = entity.sector,
                    currentPrice = quote?.price ?: 100.0,
                    changePercent = quote?.changePercent ?: 0.0,
                    pinnedAt = entity.pinnedAt
                )
            }
        }
    }

    fun observePinnedSymbols(): Flow<List<String>> {
        return database.watchlistDao().getAllPinnedSymbols()
    }

    suspend fun pinSecurity(
        symbol: String,
        name: String,
        assetClass: String,
        exchange: String,
        currency: String,
        sector: String
    ) {
        database.watchlistDao().pinSecurity(
            WatchlistEntity(
                symbol = symbol,
                securityName = name,
                assetClass = assetClass,
                exchange = exchange,
                currency = currency,
                sector = sector,
                pinnedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun unpinSecurity(symbol: String) {
        database.watchlistDao().unpinSecurity(symbol)
    }

    suspend fun togglePin(
        symbol: String,
        name: String,
        assetClass: String,
        exchange: String,
        currency: String,
        sector: String,
        isCurrentlyPinned: Boolean
    ) {
        if (isCurrentlyPinned) {
            unpinSecurity(symbol)
        } else {
            pinSecurity(symbol, name, assetClass, exchange, currency, sector)
        }
    }
}
