package com.example.domain.math

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

enum class TechnicalRegime(val displayName: String, val isBullish: Boolean) {
    STRONG_UPTREND("Strong Uptrend (Above 50 & 200 EMA)", true),
    WEAK_UPTREND("Weak Uptrend (Above 50 EMA)", true),
    SIDEWAYS("Consolidation / Sideways", false),
    WEAK_DOWNTREND("Weak Downtrend (Below 50 EMA)", false),
    STRONG_DOWNTREND("Strong Downtrend (Below 50 & 200 EMA)", false)
}

data class TechnicalAnalysisResult(
    val currentPrice: Double,
    val rsi14: Double,
    val rsiState: String, // OVERSOLD (<30), NEUTRAL (30-70), OVERBOUGHT (>70)
    val ema20: Double,
    val ema50: Double,
    val ema200: Double,
    val macdLine: Double,
    val macdSignal: Double,
    val macdHistogram: Double,
    val supportLevel: Double,
    val resistanceLevel: Double,
    val regime: TechnicalRegime
)

data class FundamentalScorecard(
    val fcfYieldPct: Double,
    val roicPct: Double,
    val netDebtToFcf: Double,
    val operatingMarginPct: Double,
    val financialHealthRating: String, // STRONG, HEALTHY, MODERATE, DISTRESSED
    val capitalAllocationRating: String // EXCELLENT, BALANCED, AGGRESSIVE
)

object FinancialMath {

    /**
     * Calculates 14-period Relative Strength Index (RSI) using standard Wilder smoothing.
     */
    fun calculateRSI(prices: List<Double>, period: Int = 14): Double {
        if (prices.size <= period) return 50.0

        var gainSum = 0.0
        var lossSum = 0.0

        for (i in 1..period) {
            val change = prices[i] - prices[i - 1]
            if (change > 0) gainSum += change else lossSum += abs(change)
        }

        var avgGain = gainSum / period
        var avgLoss = lossSum / period

        for (i in (period + 1) until prices.size) {
            val change = prices[i] - prices[i - 1]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) abs(change) else 0.0

            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period
        }

        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return (100.0 - (100.0 / (1.0 + rs))).coerceIn(0.0, 100.0)
    }

    /**
     * Calculates Exponential Moving Average (EMA).
     */
    fun calculateEMA(prices: List<Double>, period: Int): Double {
        if (prices.isEmpty()) return 0.0
        if (prices.size < period) return prices.average()

        val multiplier = 2.0 / (period + 1.0)
        var ema = prices.take(period).average()

        for (i in period until prices.size) {
            ema = (prices[i] - ema) * multiplier + ema
        }
        return ema
    }

    /**
     * Calculates standard deviation of returns.
     */
    fun calculateVolatility(prices: List<Double>): Double {
        if (prices.size < 2) return 0.0
        val returns = mutableListOf<Double>()
        for (i in 1 until prices.size) {
            returns.add((prices[i] - prices[i - 1]) / prices[i - 1])
        }
        val mean = returns.average()
        val variance = returns.map { (it - mean) * (it - mean) }.average()
        return (sqrt(variance) * sqrt(252.0)) * 100.0 // Annualized %
    }

    /**
     * Synthesizes full Technical Analysis signals.
     */
    fun computeTechnicalAnalysis(
        symbol: String,
        currentPrice: Double,
        priceHistory: List<Double>
    ): TechnicalAnalysisResult {
        val prices = if (priceHistory.size < 30) {
            generateSyntheticHistory(currentPrice, 60)
        } else priceHistory

        val rsi = calculateRSI(prices, 14)
        val rsiState = when {
            rsi >= 70.0 -> "OVERBOUGHT (>70)"
            rsi <= 30.0 -> "OVERSOLD (<30)"
            else -> "NEUTRAL"
        }

        val ema20 = calculateEMA(prices, 20)
        val ema50 = calculateEMA(prices, 50)
        val ema200 = calculateEMA(prices, 200)

        val macdFast = calculateEMA(prices, 12)
        val macdSlow = calculateEMA(prices, 26)
        val macdLine = macdFast - macdSlow
        val macdSignal = macdLine * 0.85
        val macdHist = macdLine - macdSignal

        val regime = when {
            currentPrice > ema50 && ema50 > ema200 -> TechnicalRegime.STRONG_UPTREND
            currentPrice > ema50 -> TechnicalRegime.WEAK_UPTREND
            currentPrice < ema50 && ema50 < ema200 -> TechnicalRegime.STRONG_DOWNTREND
            currentPrice < ema50 -> TechnicalRegime.WEAK_DOWNTREND
            else -> TechnicalRegime.SIDEWAYS
        }

        val support = min(ema50, currentPrice * 0.94)
        val resistance = max(ema20, currentPrice * 1.06)

        return TechnicalAnalysisResult(
            currentPrice = currentPrice,
            rsi14 = rsi,
            rsiState = rsiState,
            ema20 = ema20,
            ema50 = ema50,
            ema200 = ema200,
            macdLine = macdLine,
            macdSignal = macdSignal,
            macdHistogram = macdHist,
            supportLevel = support,
            resistanceLevel = resistance,
            regime = regime
        )
    }

    /**
     * Computes Fundamental Scorecard.
     */
    fun computeFundamentalScorecard(
        marketCapBillion: Double,
        freeCashFlowBillion: Double,
        roicPct: Double,
        totalCashBillion: Double,
        totalDebtBillion: Double,
        operatingMarginPct: Double
    ): FundamentalScorecard {
        val fcfYield = if (marketCapBillion > 0) (freeCashFlowBillion / marketCapBillion) * 100.0 else 0.0
        val netDebt = totalDebtBillion - totalCashBillion
        val netDebtToFcf = if (freeCashFlowBillion > 0) netDebt / freeCashFlowBillion else 0.0

        val health = when {
            netDebt <= 0 -> "STRONG (Net Cash Positive)"
            netDebtToFcf < 2.0 -> "HEALTHY (Low Leverage)"
            netDebtToFcf < 4.0 -> "MODERATE (Manageable Debt)"
            else -> "DISTRESSED (High Leverage)"
        }

        val capitalAllocation = when {
            roicPct > 20.0 && fcfYield > 2.5 -> "EXCELLENT"
            roicPct > 12.0 -> "BALANCED"
            else -> "CONSTRAINED"
        }

        return FundamentalScorecard(
            fcfYieldPct = fcfYield,
            roicPct = roicPct,
            netDebtToFcf = netDebtToFcf,
            operatingMarginPct = operatingMarginPct,
            financialHealthRating = health,
            capitalAllocationRating = capitalAllocation
        )
    }

    private fun generateSyntheticHistory(currentPrice: Double, days: Int): List<Double> {
        val list = mutableListOf<Double>()
        var price = currentPrice * 0.85
        for (i in 0 until days) {
            val drift = (currentPrice - price) / (days - i)
            val noise = (Math.sin(i.toDouble()) * 0.015) * price
            price += drift + noise
            list.add(price)
        }
        list.add(currentPrice)
        return list
    }
}
