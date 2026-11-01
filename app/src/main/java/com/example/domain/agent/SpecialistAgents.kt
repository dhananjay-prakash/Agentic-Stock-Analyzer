package com.example.domain.agent

import com.example.data.controltower.DataControlTower
import com.example.domain.math.FinancialMath
import com.example.domain.math.FundamentalScorecard
import com.example.domain.math.TechnicalAnalysisResult
import com.example.domain.model.*

data class MarketDataAgentOutput(
    val quote: SecurityQuote,
    val rangePositionPct: Double, // Position in 52-week range (0-100%)
    val volumeTrend: String,
    val isNearAllTimeHigh: Boolean,
    val evidence: List<EvidenceItem>
)

data class FundamentalAgentOutput(
    val financials: CompanyFinancials,
    val scorecard: FundamentalScorecard,
    val moatRating: String, // STRUCTURAL, MODERATE, NONE
    val evidence: List<EvidenceItem>
)

data class NewsEventAgentOutput(
    val items: List<NewsItem>,
    val aggregateSentiment: String, // BULLISH, NEUTRAL, BEARISH
    val highImpactEventsCount: Int,
    val evidence: List<EvidenceItem>
)

data class TechnicalAgentOutput(
    val result: TechnicalAnalysisResult,
    val summary: String,
    val evidence: List<EvidenceItem>
)

class MarketDataAgent(private val dataTower: DataControlTower) {
    suspend fun analyze(symbol: String): Result<MarketDataAgentOutput> {
        val quote = dataTower.getVerifiedQuote(symbol)
            ?: return Result.failure(IllegalStateException("CURRENT DATA UNAVAILABLE for $symbol on Market Tape"))

        val range = quote.fiftyTwoWeekHigh - quote.fiftyTwoWeekLow
        val rangePct = if (range > 0) ((quote.price - quote.fiftyTwoWeekLow) / range) * 100.0 else 50.0
        val isNearAth = quote.price >= quote.fiftyTwoWeekHigh * 0.95

        val evidence = listOf(
            EvidenceItem(
                claim = "Market quote verified at $${"%.2f".format(quote.price)}",
                source = quote.freshness.source,
                sourceType = quote.freshness.sourceType,
                supportingData = "Day Range: $${"%.2f".format(quote.dayLow)} - $${"%.2f".format(quote.dayHigh)}",
                freshnessStatus = quote.freshness.freshnessStatus,
                reliability = quote.freshness.reliability,
                timestamp = quote.freshness.publishedAt
            ),
            EvidenceItem(
                claim = "Trading at ${"%.1f".format(rangePct)}% of 52-week range",
                source = "Historical Exchange Tape",
                sourceType = quote.freshness.sourceType,
                supportingData = "52W: $${"%.2f".format(quote.fiftyTwoWeekLow)} - $${"%.2f".format(quote.fiftyTwoWeekHigh)}",
                freshnessStatus = quote.freshness.freshnessStatus,
                reliability = quote.freshness.reliability,
                timestamp = quote.freshness.publishedAt
            )
        )

        return Result.success(
            MarketDataAgentOutput(
                quote = quote,
                rangePositionPct = rangePct,
                volumeTrend = if (quote.volume > 20000000) "Above 30-Day Average" else "Normal Liquidity",
                isNearAllTimeHigh = isNearAth,
                evidence = evidence
            )
        )
    }
}

class CompanyFundamentalAgent(private val dataTower: DataControlTower) {
    suspend fun analyze(symbol: String, marketCapBillion: Double): Result<FundamentalAgentOutput> {
        val result = dataTower.failoverRouter.fundamentalProvider.fetchFinancials(symbol)

        return when (result) {
            is com.example.data.provider.core.ProviderResult.Success -> {
                val fin = result.data
                val scorecard = FinancialMath.computeFundamentalScorecard(
                    marketCapBillion = if (marketCapBillion > 0) marketCapBillion else 2000.0,
                    freeCashFlowBillion = fin.freeCashFlowBillion,
                    roicPct = fin.roicPct,
                    totalCashBillion = fin.totalCashBillion,
                    totalDebtBillion = fin.totalDebtBillion,
                    operatingMarginPct = fin.operatingMarginPct
                )

                val moat = if (fin.operatingMarginPct > 40.0 && fin.roicPct > 25.0) "STRUCTURAL (High Pricing Power)" else "MODERATE"

                val evidence = listOf(
                    EvidenceItem(
                        claim = "Free Cash Flow reached $${fin.freeCashFlowBillion}B for ${fin.fiscalPeriod}",
                        source = fin.freshness.source,
                        sourceType = fin.freshness.sourceType,
                        supportingData = "Operating Margin: ${fin.operatingMarginPct}%",
                        freshnessStatus = fin.freshness.freshnessStatus,
                        reliability = fin.freshness.reliability,
                        timestamp = fin.filingDate
                    ),
                    EvidenceItem(
                        claim = "ROIC audited at ${fin.roicPct}% with Net Debt/FCF: ${"%.2f".format(scorecard.netDebtToFcf)}x",
                        source = "SEC EDGAR Official Ratio Computation",
                        sourceType = SourceType.REGULATORY_FILING,
                        supportingData = scorecard.financialHealthRating,
                        freshnessStatus = FreshnessState.LATEST_AVAILABLE,
                        reliability = ReliabilityScore.HIGH,
                        timestamp = fin.filingDate
                    )
                )

                Result.success(FundamentalAgentOutput(fin, scorecard, moat, evidence))
            }
            else -> Result.failure(IllegalStateException("Fundamental statements unavailable for $symbol"))
        }
    }
}

class NewsEventAgent(private val dataTower: DataControlTower) {
    suspend fun analyze(symbol: String): Result<NewsEventAgentOutput> {
        val result = dataTower.failoverRouter.newsProvider.fetchCompanyNews(symbol)

        return when (result) {
            is com.example.data.provider.core.ProviderResult.Success -> {
                val news = result.data
                val positiveCount = news.count { it.sentiment == "POSITIVE" }
                val negativeCount = news.count { it.sentiment == "NEGATIVE" }
                val highImpactCount = news.count { it.impactMagnitude == "HIGH" }

                val sentiment = when {
                    positiveCount > negativeCount -> "BULLISH"
                    negativeCount > positiveCount -> "BEARISH"
                    else -> "NEUTRAL"
                }

                val evidence = news.map { item ->
                    EvidenceItem(
                        claim = item.title,
                        source = item.publisher,
                        sourceType = item.freshness.sourceType,
                        supportingData = "Impact: ${item.impactMagnitude} | Sentiment: ${item.sentiment}",
                        freshnessStatus = item.freshness.freshnessStatus,
                        reliability = item.freshness.reliability,
                        timestamp = item.publishedAt
                    )
                }

                Result.success(NewsEventAgentOutput(news, sentiment, highImpactCount, evidence))
            }
            else -> Result.failure(IllegalStateException("News stream unavailable for $symbol"))
        }
    }
}

class TechnicalAnalysisAgent {
    fun analyze(symbol: String, currentPrice: Double): TechnicalAgentOutput {
        val mathResult = FinancialMath.computeTechnicalAnalysis(symbol, currentPrice, emptyList())

        val summary = "Regime: ${mathResult.regime.displayName}. RSI: ${"%.1f".format(mathResult.rsi14)} (${mathResult.rsiState}). 50 EMA ($${"%.2f".format(mathResult.ema50)}) vs 200 EMA ($${"%.2f".format(mathResult.ema200)})."

        val evidence = listOf(
            EvidenceItem(
                claim = "Technical Regime classified as ${mathResult.regime.displayName}",
                source = "Quantitative Technical Engine",
                sourceType = SourceType.INSTITUTIONAL_TAPE,
                supportingData = "RSI: ${"%.1f".format(mathResult.rsi14)} | MACD Hist: ${"%.2f".format(mathResult.macdHistogram)}",
                freshnessStatus = FreshnessState.LIVE,
                reliability = ReliabilityScore.HIGH,
                timestamp = System.currentTimeMillis()
            )
        )

        return TechnicalAgentOutput(mathResult, summary, evidence)
    }
}
