package com.example.data.provider.remote

import com.example.data.provider.core.MarketDataProvider
import com.example.data.provider.core.ProviderMetadata
import com.example.data.provider.core.ProviderResult
import com.example.domain.model.*
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

class PrimaryExchangeMarketDataProvider : MarketDataProvider {
    override val providerName: String = "Direct Consolidated Tape (NASDAQ/NYSE/NSE)"
    override val tier: SourceType = SourceType.PRIMARY_EXCHANGE

    // Simulation toggle to allow testing failover in the UI
    var simulateFailure: Boolean = false

    override suspend fun fetchQuote(symbol: String): ProviderResult<SecurityQuote> {
        val now = System.currentTimeMillis()
        var quote: SecurityQuote? = null

        val latency = measureTimeMillis {
            delay(50) // network I/O simulation
            if (simulateFailure) {
                return ProviderResult.FailoverRequired("Primary Exchange feed timed out or returned HTTP 503")
            }
            quote = generatePrimaryQuote(symbol, now)
        }

        return if (quote != null) {
            ProviderResult.Success(
                data = quote!!,
                metadata = ProviderMetadata(
                    providerName = providerName,
                    tier = tier,
                    latencyMs = latency,
                    timestamp = now
                )
            )
        } else {
            ProviderResult.FailoverRequired("Symbol $symbol not found on primary exchange tape")
        }
    }

    override suspend fun fetchBatchQuotes(symbols: List<String>): Map<String, ProviderResult<SecurityQuote>> {
        return symbols.associateWith { fetchQuote(it) }
    }

    private fun generatePrimaryQuote(symbol: String, now: Long): SecurityQuote? {
        val (price, change, pct, fiftyTwoHigh, fiftyTwoLow, pe, mktCap) = when (symbol.uppercase()) {
            "NVDA" -> Tuple7(142.80, 4.10, 2.95, 144.42, 45.10, 54.2, 3510.0)
            "MSFT" -> Tuple7(433.50, 0.20, 0.05, 468.35, 366.50, 36.9, 3220.0)
            "GOOGL" -> Tuple7(179.20, 2.55, 1.44, 191.75, 129.40, 24.6, 2220.0)
            "AAPL" -> Tuple7(231.40, 1.80, 0.78, 237.23, 164.08, 34.1, 3520.0)
            "QQQM" -> Tuple7(213.10, 2.80, 1.33, 214.50, 162.30, 31.4, 31800.0)
            "VOO" -> Tuple7(539.80, 4.30, 0.80, 542.10, 420.50, 27.3, 514000.0)
            "SCHG" -> Tuple7(102.50, 1.10, 1.08, 103.80, 74.20, 33.2, 36000.0)
            "SMH" -> Tuple7(264.80, 5.20, 2.00, 280.40, 145.20, 42.1, 24000.0)
            "RELIANCE.NS" -> Tuple7(2758.00, 45.50, 1.68, 3217.90, 2221.05, 26.9, 18600.0)
            "TCS.NS" -> Tuple7(4065.00, -3.00, -0.07, 4585.90, 3311.00, 29.6, 14650.0)
            "GLD" -> Tuple7(254.90, 1.85, 0.73, 258.40, 182.50, null, 72500.0)
            else -> Tuple7(100.0, 1.0, 1.0, 120.0, 80.0, 20.0, 1000.0)
        }

        val freshness = FreshnessMetadata(
            source = providerName,
            sourceType = SourceType.PRIMARY_EXCHANGE,
            publishedAt = now,
            effectiveAt = now,
            retrievedAt = now,
            dataClass = DataClass.INTRADAY_PRICE,
            freshnessStatus = FreshnessState.LIVE,
            reliability = ReliabilityScore.HIGH,
            confidence = 0.99f
        )

        return SecurityQuote(
            symbol = symbol.uppercase(),
            price = price,
            changeAmount = change,
            changePercent = pct,
            dayOpen = price - change,
            dayHigh = price + 1.2,
            dayLow = price - change - 0.8,
            volume = 32000000,
            fiftyTwoWeekHigh = fiftyTwoHigh,
            fiftyTwoWeekLow = fiftyTwoLow,
            peRatio = pe,
            marketCap = mktCap,
            freshness = freshness
        )
    }

    private data class Tuple7(
        val price: Double,
        val change: Double,
        val pct: Double,
        val high: Double,
        val low: Double,
        val pe: Double?,
        val mktCap: Double
    )
}

class SecondaryInstitutionalMarketDataProvider : MarketDataProvider {
    override val providerName: String = "Secondary Institutional Liquidity Feed (BATS / IEX)"
    override val tier: SourceType = SourceType.INSTITUTIONAL_TAPE

    override suspend fun fetchQuote(symbol: String): ProviderResult<SecurityQuote> {
        val now = System.currentTimeMillis()
        var quote: SecurityQuote? = null

        val latency = measureTimeMillis {
            delay(120) // failover provider has slightly higher latency
            quote = generateSecondaryQuote(symbol, now)
        }

        return if (quote != null) {
            ProviderResult.Success(
                data = quote!!,
                metadata = ProviderMetadata(
                    providerName = providerName,
                    tier = tier,
                    latencyMs = latency,
                    timestamp = now
                )
            )
        } else {
            ProviderResult.CurrentDataUnavailable("Data unavailable from both primary and secondary market data feeds")
        }
    }

    override suspend fun fetchBatchQuotes(symbols: List<String>): Map<String, ProviderResult<SecurityQuote>> {
        return symbols.associateWith { fetchQuote(it) }
    }

    private fun generateSecondaryQuote(symbol: String, now: Long): SecurityQuote {
        val basePrice = when (symbol.uppercase()) {
            "NVDA" -> 142.75
            "MSFT" -> 433.40
            "QQQM" -> 213.05
            "VOO" -> 539.75
            "RELIANCE.NS" -> 2756.00
            else -> 100.0
        }

        val freshness = FreshnessMetadata(
            source = providerName,
            sourceType = SourceType.INSTITUTIONAL_TAPE,
            publishedAt = now - 15000, // 15 seconds aged
            effectiveAt = now,
            retrievedAt = now,
            dataClass = DataClass.INTRADAY_PRICE,
            freshnessStatus = FreshnessState.LIVE,
            reliability = ReliabilityScore.HIGH,
            confidence = 0.95f
        )

        return SecurityQuote(
            symbol = symbol.uppercase(),
            price = basePrice,
            changeAmount = 3.95,
            changePercent = 2.85,
            dayOpen = basePrice - 3.95,
            dayHigh = basePrice + 1.0,
            dayLow = basePrice - 4.5,
            volume = 29000000,
            fiftyTwoWeekHigh = 144.42,
            fiftyTwoWeekLow = 45.10,
            peRatio = 54.0,
            marketCap = 3500.0,
            freshness = freshness
        )
    }
}
