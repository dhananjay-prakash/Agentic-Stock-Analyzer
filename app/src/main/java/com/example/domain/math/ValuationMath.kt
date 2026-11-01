package com.example.domain.math

import com.example.domain.model.DCFValuation
import com.example.domain.model.MultiplesValuation
import com.example.domain.model.ValuationClassification
import kotlin.math.pow

object ValuationMath {

    /**
     * Multi-stage deterministic Discounted Cash Flow (DCF) model:
     * - Stage 1 (Years 1-5): High growth period
     * - Stage 2 (Years 6-10): Fading growth towards long-term GDP
     * - Terminal Value: Gordon Growth Model with WACC discount
     */
    fun computeDCF(
        initialFcfBillion: Double,
        stage1GrowthPct: Double = 22.0,
        stage2GrowthPct: Double = 12.0,
        terminalGrowthPct: Double = 3.0,
        waccPct: Double = 9.5,
        netCashBillion: Double = 30.0,
        sharesDilutedBillion: Double = 24.5
    ): DCFValuation {
        val r = waccPct / 100.0
        val g1 = stage1GrowthPct / 100.0
        val g2 = stage2GrowthPct / 100.0
        val gT = terminalGrowthPct / 100.0

        var pvCashFlows = 0.0
        var currentFcf = initialFcfBillion

        // Stage 1: Years 1 to 5
        for (year in 1..5) {
            currentFcf *= (1.0 + g1)
            val discountFactor = (1.0 + r).pow(year.toDouble())
            pvCashFlows += (currentFcf / discountFactor)
        }

        // Stage 2: Years 6 to 10
        for (year in 6..10) {
            currentFcf *= (1.0 + g2)
            val discountFactor = (1.0 + r).pow(year.toDouble())
            pvCashFlows += (currentFcf / discountFactor)
        }

        // Terminal Value at Year 10
        val terminalFcfYear11 = currentFcf * (1.0 + gT)
        val terminalValueYear10 = terminalFcfYear11 / (r - gT).coerceAtLeast(0.01)
        val pvTerminalValue = terminalValueYear10 / (1.0 + r).pow(10.0)

        val enterpriseValue = pvCashFlows + pvTerminalValue
        val equityValue = enterpriseValue + netCashBillion
        val impliedPerShare = if (sharesDilutedBillion > 0) equityValue / sharesDilutedBillion else 100.0

        return DCFValuation(
            stage1GrowthRatePct = stage1GrowthPct,
            stage2GrowthRatePct = stage2GrowthPct,
            terminalGrowthRatePct = terminalGrowthPct,
            waccPct = waccPct,
            presentValueCashFlows = pvCashFlows,
            presentValueTerminalValue = pvTerminalValue,
            enterpriseValue = enterpriseValue,
            netCashDebt = netCashBillion,
            equityValue = equityValue,
            impliedPerShareValue = impliedPerShare
        )
    }

    /**
     * Computes peer and historical multiple-based fair value.
     */
    fun computeMultiples(
        forwardEps: Double = 4.25,
        peerAveragePe: Double = 34.0,
        historical5YrPe: Double = 42.0,
        trailingPe: Double = 54.0,
        evToEbitda: Double = 38.5,
        evToSales: Double = 28.0,
        priceToFcf: Double = 45.0,
        pegRatio: Double = 1.35
    ): MultiplesValuation {
        val targetPe = (peerAveragePe * 0.4) + (historical5YrPe * 0.6)
        val impliedPrice = forwardEps * targetPe

        return MultiplesValuation(
            trailingPe = trailingPe,
            forwardPe = if (forwardEps > 0) impliedPrice / forwardEps else trailingPe,
            pegRatio = pegRatio,
            evToEbitda = evToEbitda,
            evToSales = evToSales,
            priceToFcf = priceToFcf,
            peerAveragePe = peerAveragePe,
            historical5YrAveragePe = historical5YrPe,
            impliedMultiplePrice = impliedPrice
        )
    }

    /**
     * Evaluates valuation classification and Margin of Safety.
     */
    fun classifyValuation(
        currentPrice: Double,
        baseFairValue: Double
    ): Pair<ValuationClassification, Double> {
        val marginOfSafetyPct = if (baseFairValue > 0) ((baseFairValue - currentPrice) / baseFairValue) * 100.0 else 0.0

        val classification = when {
            marginOfSafetyPct >= 20.0 -> ValuationClassification.SIGNIFICANTLY_UNDERVALUED
            marginOfSafetyPct >= 10.0 -> ValuationClassification.MODERATELY_UNDERVALUED
            marginOfSafetyPct >= -10.0 -> ValuationClassification.FAIRLY_VALUED
            marginOfSafetyPct >= -25.0 -> ValuationClassification.MODERATELY_OVERVALUED
            else -> ValuationClassification.SIGNIFICANTLY_OVERVALUED
        }

        return Pair(classification, marginOfSafetyPct)
    }

    /**
     * Deterministic position sizing based on conviction score, existing allocation, and volatility risk budget.
     */
    fun computePositionSizing(
        portfolioTotalValue: Double,
        currentHoldingValue: Double,
        convictionScore: Int, // 0 to 100
        volatilityPct: Double,
        concentrationLimitPct: Double = 15.0
    ): Pair<Double, Double> {
        val currentWeightPct = if (portfolioTotalValue > 0) (currentHoldingValue / portfolioTotalValue) * 100.0 else 0.0
        val remainingHeadroomPct = (concentrationLimitPct - currentWeightPct).coerceAtLeast(0.0)

        // Conviction multiplier: 80+ conviction allows up to 4% increment, 60-79 allows up to 2.5%, <60 allows 0%
        val baseIncrement = when {
            convictionScore >= 85 -> 4.0
            convictionScore >= 75 -> 2.5
            convictionScore >= 65 -> 1.5
            else -> 0.0
        }

        // Volatility penalty: if annualized vol > 35%, reduce increment size by 30%
        val volFactor = if (volatilityPct > 35.0) 0.70 else 1.0
        val suggestedIncrementPct = (baseIncrement * volFactor).coerceAtMost(remainingHeadroomPct)
        val dollarTranche = portfolioTotalValue * (suggestedIncrementPct / 100.0)

        return Pair(suggestedIncrementPct, dollarTranche)
    }
}
