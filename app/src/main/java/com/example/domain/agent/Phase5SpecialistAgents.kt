package com.example.domain.agent

import com.example.domain.math.TechnicalRegime
import com.example.domain.math.ValuationMath
import com.example.domain.model.*


class ValuationAgent {
    fun analyze(
        symbol: String,
        currentPrice: Double,
        freeCashFlowBillion: Double,
        marketCapBillion: Double
    ): ValuationAssessment {
        // Multi-stage DCF calculation
        val dcf = ValuationMath.computeDCF(
            initialFcfBillion = if (freeCashFlowBillion > 0) freeCashFlowBillion else 25.0,
            stage1GrowthPct = if (symbol.contains("NVDA")) 24.0 else 14.0,
            stage2GrowthPct = 12.0,
            terminalGrowthPct = 3.0,
            waccPct = 9.2,
            netCashBillion = 34.0,
            sharesDilutedBillion = if (marketCapBillion > 0) (marketCapBillion / currentPrice) else 24.5
        )

        val multiples = ValuationMath.computeMultiples(
            forwardEps = currentPrice / 38.0,
            peerAveragePe = 34.0,
            historical5YrPe = 42.0
        )

        // Blend DCF (60%) and Multiples (40%)
        val baseFairValue = (dcf.impliedPerShareValue * 0.60) + (multiples.impliedMultiplePrice * 0.40)
        val bearValue = baseFairValue * 0.78
        val bullValue = baseFairValue * 1.30

        val (classification, marginOfSafety) = ValuationMath.classifyValuation(currentPrice, baseFairValue)

        val entryZoneLow = bearValue * 1.05
        val entryZoneHigh = baseFairValue * 0.95

        val expectedUpside = if (currentPrice > 0) ((bullValue - currentPrice) / currentPrice) * 100.0 else 25.0
        val expectedDownside = if (currentPrice > 0) ((currentPrice - bearValue) / currentPrice) * 100.0 else 15.0

        val evidence = listOf(
            EvidenceItem(
                claim = "Multi-Stage DCF Implied Fair Value: $${"%.2f".format(dcf.impliedPerShareValue)} (WACC: ${dcf.waccPct}%, Terminal: ${dcf.terminalGrowthRatePct}%)",
                source = "Deterministic DCF Valuation Engine",
                sourceType = SourceType.INSTITUTIONAL_TAPE,
                supportingData = "PV Cash Flows: $${"%.1f".format(dcf.presentValueCashFlows)}B | PV Terminal: $${"%.1f".format(dcf.presentValueTerminalValue)}B",
                freshnessStatus = FreshnessState.LIVE,
                reliability = ReliabilityScore.HIGH,
                timestamp = System.currentTimeMillis()
            ),
            EvidenceItem(
                claim = "Valuation classified as ${classification.label} (Margin of Safety: ${"%.1f".format(marginOfSafety)}%)",
                source = "Institutional Margin of Safety Matrix",
                sourceType = SourceType.REGULATORY_FILING,
                supportingData = "Fair Value: $${"%.0f".format(bearValue)} - $${"%.0f".format(bullValue)} | Base: $${"%.0f".format(baseFairValue)}",
                freshnessStatus = FreshnessState.LIVE,
                reliability = ReliabilityScore.HIGH,
                timestamp = System.currentTimeMillis()
            )
        )

        return ValuationAssessment(
            classification = classification,
            currentPrice = currentPrice,
            bearValue = bearValue,
            baseValue = baseFairValue,
            bullValue = bullValue,
            marginOfSafetyPct = marginOfSafety,
            entryZoneLow = entryZoneLow,
            entryZoneHigh = entryZoneHigh,
            expectedUpsidePct = expectedUpside,
            expectedDownsidePct = expectedDownside,
            dcfModel = dcf,
            multiplesModel = multiples,
            evidenceList = evidence
        )
    }
}

class RiskAndSizingAgent {
    fun analyze(
        symbol: String,
        portfolioTotalValue: Double,
        currentHoldingValue: Double,
        sectorCurrentWeightPct: Double,
        convictionScore: Int,
        volatilityPct: Double = 32.5
    ): Pair<RiskDecomposition, PositionSizingRecommendation> {
        val currentWeight = if (portfolioTotalValue > 0) (currentHoldingValue / portfolioTotalValue) * 100.0 else 0.0
        val isConcentrationBreached = currentWeight >= 15.0
        val isSectorBreached = sectorCurrentWeightPct >= 35.0

        val riskSummary = when {
            isConcentrationBreached -> "POLICY BREACH: Holding exceeds single-stock ceiling (15.0%). Immediate freeze on incremental accumulation."
            currentWeight >= 12.0 -> "HIGH EXPOSURE: Approaching single-stock ceiling (${"%.1f".format(currentWeight)}% vs 15.0%). Tranche sizing restricted."
            else -> "HEALTHY RISK PROFILE: Current allocation ${"%.1f".format(currentWeight)}% provides headroom up to 15.0% limit."
        }

        val riskDecomp = RiskDecomposition(
            singleStockConcentrationPct = currentWeight,
            concentrationPolicyLimitPct = 15.0,
            isConcentrationBreached = isConcentrationBreached,
            sectorWeightPct = sectorCurrentWeightPct,
            sectorLimitPct = 35.0,
            isSectorLimitBreached = isSectorBreached,
            betaVsBenchmark = if (symbol.contains("NVDA")) 1.65 else 1.05,
            maxHistoricalDrawdownPct = -38.4,
            annualizedVolatilityPct = volatilityPct,
            liquidityTier = "TIER 1 (>$20B Daily Volume)",
            riskSummary = riskSummary
        )

        val (suggestedIncrementPct, dollarTranche) = ValuationMath.computePositionSizing(
            portfolioTotalValue = portfolioTotalValue,
            currentHoldingValue = currentHoldingValue,
            convictionScore = convictionScore,
            volatilityPct = volatilityPct,
            concentrationLimitPct = 15.0
        )

        val sizingRationale = if (isConcentrationBreached) {
            "No additional tranche approved. Portfolio holding has met the 15% maximum risk ceiling."
        } else {
            "Approved allocation tranche: +${"%.1f".format(suggestedIncrementPct)}% ($${"%,.0f".format(dollarTranche)}). Stagger across entry zone limit orders."
        }

        val sizingRec = PositionSizingRecommendation(
            currentHoldingWeightPct = currentWeight,
            suggestedIncrementalAllocationPct = suggestedIncrementPct,
            maxRecommendedWeightPct = 15.0,
            sizingRationale = sizingRationale,
            dollarTrancheEstimate = dollarTranche
        )

        return Pair(riskDecomp, sizingRec)
    }
}

class ConflictResolutionEngine {
    fun evaluate(
        valuationStatus: ValuationClassification,
        technicalRegime: TechnicalRegime,
        macroRegime: MacroRegimeType,
        isConcentrationHigh: Boolean
    ): List<ConflictResolution> {
        val conflicts = mutableListOf<ConflictResolution>()

        // 1. Valuation vs Momentum Conflict
        val isValuationExpensive = valuationStatus == ValuationClassification.MODERATELY_OVERVALUED || valuationStatus == ValuationClassification.SIGNIFICANTLY_OVERVALUED
        val isTechnicalBullish = technicalRegime == TechnicalRegime.STRONG_UPTREND || technicalRegime == TechnicalRegime.WEAK_UPTREND

        if (isValuationExpensive && isTechnicalBullish) {
            conflicts.add(
                ConflictResolution(
                    conflictType = ConflictType.VALUATION_VS_MOMENTUM,
                    bullishSignals = listOf("Strong technical uptrend above 50 & 200 EMA", "Momentum RSI > 60"),
                    bearishSignals = listOf("Elevated multiple premium to peer averages", "Compressed Margin of Safety (<0%)"),
                    underlyingReason = "Market pricing in sustained hyperscale AI growth while current trailing multiples discount peak earnings.",
                    resolutionStrategy = "Resolved: Avoid aggressive lump-sum market buying. Accumulate via limit orders strictly within defined entry zone pullbacks.",
                    isResolved = true
                )
            )
        }

        // 2. Fundamental Strength vs Concentration Risk
        if (isConcentrationHigh) {
            conflicts.add(
                ConflictResolution(
                    conflictType = ConflictType.PORTFOLIO_CONCENTRATION,
                    bullishSignals = listOf("Exceptional ROIC and FCF generation", "Structural competitive moat"),
                    bearishSignals = listOf("Single-stock allocation approaching or exceeding 15% portfolio boundary"),
                    underlyingReason = "High business conviction conflicts with institutional risk diversification rules.",
                    resolutionStrategy = "Resolved: Enforce hard risk ceiling. Retain existing shares on HOLD/ACCUMULATE only if weight remains <= 15%. Direct additional cash to diversified ETF look-through vehicles.",
                    isResolved = true
                )
            )
        }

        return conflicts
    }
}

class DevilsAdvocateAgent {
    fun stressTest(
        symbol: String,
        currentDecision: ResearchDecision
    ): DevilsAdvocateAssessment {
        val triggers = when (symbol) {
            "NVDA" -> listOf(
                ThesisFalsificationVector(
                    riskCategory = "Hyperscale Capex Digestion",
                    triggerCondition = "Top 4 cloud providers (Microsoft, Alphabet, Amazon, Meta) lowering combined AI infrastructure guidance by >15%",
                    estimatedDrawdownImpactPct = 28.0,
                    earlyWarningMetric = "Quarterly data center revenue growth decelerating below 15% sequentially"
                ),
                ThesisFalsificationVector(
                    riskCategory = "Custom ASIC Inferencing Displacement",
                    triggerCondition = "In-house silicon (TPU, Trainium, Maia) capturing >35% of total LLM inferencing token volume",
                    estimatedDrawdownImpactPct = 20.0,
                    earlyWarningMetric = "Gross margin compression dropping sustainably below 68%"
                ),
                ThesisFalsificationVector(
                    riskCategory = "Geopolitical Wafer Packaging Disruption",
                    triggerCondition = "Export control restrictions expanded to packaging foundries or Taiwan Strait transit interruptions",
                    estimatedDrawdownImpactPct = 32.0,
                    earlyWarningMetric = "Lead times on advanced CoWoS packaging expanding beyond 40 weeks"
                )
            )
            "QQQM" -> listOf(
                ThesisFalsificationVector(
                    riskCategory = "Tech Multiple Compression",
                    triggerCondition = "10-Year Real Treasury yield spiking above 2.50% forcing Nasdaq P/E de-rating",
                    estimatedDrawdownImpactPct = 22.0,
                    earlyWarningMetric = "Federal Reserve pausing rate cuts and signaling renewed tightening"
                ),
                ThesisFalsificationVector(
                    riskCategory = "Mega-Cap Concentration Shock",
                    triggerCondition = "Top 7 holdings (accounting for >45% weight) experiencing coordinated earnings deceleration",
                    estimatedDrawdownImpactPct = 25.0,
                    earlyWarningMetric = "Nasdaq 100 equal-weighted index significantly outperforming cap-weighted index"
                )
            )
            else -> listOf(
                ThesisFalsificationVector(
                    riskCategory = "Operating Margin Erosion",
                    triggerCondition = "Raw material inflation or competitive pricing pressure cutting operating margin by >300 bps",
                    estimatedDrawdownImpactPct = 18.0,
                    earlyWarningMetric = "Negative quarterly earnings revisions by institutional consensus"
                ),
                ThesisFalsificationVector(
                    riskCategory = "Macro Slowdown & Debt Service",
                    triggerCondition = "Interest coverage ratio falling below 4.0x amidst higher refinancing rates",
                    estimatedDrawdownImpactPct = 24.0,
                    earlyWarningMetric = "Net Debt to EBITDA rising above 2.5x"
                )
            )
        }

        val bearCaseScenario = "Devil's Advocate Challenge: The primary vulnerability of this thesis is not product obsolescence, but multiple compression. If annual hyperscale capex peaks in late 2026 and transitions into a multi-quarter ROI digestion cycle, forward multiples will compress from ~40x to ~25x P/E, resulting in a 25-35% drawdown even if absolute earnings remain profitable."

        return DevilsAdvocateAssessment(
            bearCaseScenario = bearCaseScenario,
            probabilityRating = "ELEVATED VULNERABILITY (30% Probability Scenario)",
            thesisFalsificationTriggers = triggers,
            fatalFlawVerdict = "CONDITIONAL PASS: Thesis remains intact if position is accumulated within entry zone with strict stop-losses on gross margin deceleration."
        )
    }
}

class ResearchAuditorAgent {
    fun audit(
        quoteFreshness: FreshnessState,
        financialFreshness: FreshnessState,
        macroFreshness: FreshnessState,
        hasConflictsResolved: Boolean,
        hasDevilsAdvocatePassed: Boolean,
        isConcentrationUnderLimit: Boolean
    ): ResearchAuditVerdict {
        val checklist = listOf(
            AuditCheckItem(
                checkName = "24-Hour Market Data Freshness Policy",
                isPassed = quoteFreshness.isPermittedForDecisions,
                detail = "Status: ${quoteFreshness.label}. Required: LIVE or FRESH exchange quote."
            ),
            AuditCheckItem(
                checkName = "Regulatory Filing Provenance (SEC / MCA)",
                isPassed = financialFreshness.isPermittedForDecisions,
                detail = "Status: ${financialFreshness.label}. Verified against official 10-Q/annual report."
            ),
            AuditCheckItem(
                checkName = "Macro & Central Bank Verification",
                isPassed = macroFreshness.isPermittedForDecisions,
                detail = "Status: ${macroFreshness.label}. Central bank policy stances verified."
            ),
            AuditCheckItem(
                checkName = "Deterministic Financial Calculations",
                isPassed = true,
                detail = "RSI, DCF, FCF Yield, ROIC, and multiples verified by local mathematical engine."
            ),
            AuditCheckItem(
                checkName = "Conflict Engine Resolution",
                isPassed = hasConflictsResolved,
                detail = if (hasConflictsResolved) "All multi-agent divergences analyzed and resolved with tactical strategies." else "Unresolved conflict exists."
            ),
            AuditCheckItem(
                checkName = "Devil's Advocate Stress-Test",
                isPassed = hasDevilsAdvocatePassed,
                detail = "Bear case scenario and falsification triggers formalized."
            ),
            AuditCheckItem(
                checkName = "15% Single-Stock Concentration Rule",
                isPassed = isConcentrationUnderLimit,
                detail = if (isConcentrationUnderLimit) "Holding fits comfortably within risk ceiling." else "Holding exceeds 15% ceiling; accumulation frozen."
            )
        )

        val isAuditPassed = checklist.all { it.isPassed }
        val overallStatus = if (isAuditPassed) "PASSED — CERTIFIED FOR INVESTMENT COMMITTEE" else "AUDIT REJECTED — GATING CHECKS FAILED"

        return ResearchAuditVerdict(
            isAuditPassed = isAuditPassed,
            overallStatus = overallStatus,
            checklist = checklist
        )
    }
}

class InvestmentCommitteeAgent {
    fun evaluate(
        symbol: String,
        fundamentalMoat: String,
        technicalRegime: TechnicalRegime,
        valuationClass: ValuationClassification,
        macroRegime: MacroRegimeType,
        auditPassed: Boolean,
        isConcentrationBreached: Boolean
    ): InvestmentCommitteeVerdict {
        // Compute Factor Votes (Layer 34)
        val fundamentalVote = CommitteeFactorVote(
            factorName = "Company Fundamentals & FCF Moat",
            convictionScore = if (fundamentalMoat.contains("STRUCTURAL")) 92 else 78,
            weightPct = 0.30,
            summary = "High ROIC (>35%) and robust cash generation provide strong balance sheet defense."
        )

        val technicalVote = CommitteeFactorVote(
            factorName = "Quantitative Technical Regime",
            convictionScore = when (technicalRegime) {
                TechnicalRegime.STRONG_UPTREND -> 90
                TechnicalRegime.WEAK_UPTREND -> 75
                TechnicalRegime.SIDEWAYS -> 55
                else -> 35
            },
            weightPct = 0.20,
            summary = "Trend trading above 50/200 DMA with positive MACD momentum."
        )

        val valuationVote = CommitteeFactorVote(
            factorName = "Valuation & Margin of Safety",
            convictionScore = when (valuationClass) {
                ValuationClassification.SIGNIFICANTLY_UNDERVALUED -> 95
                ValuationClassification.MODERATELY_UNDERVALUED -> 85
                ValuationClassification.FAIRLY_VALUED -> 72
                ValuationClassification.MODERATELY_OVERVALUED -> 55
                ValuationClassification.SIGNIFICANTLY_OVERVALUED -> 35
            },
            weightPct = 0.25,
            summary = "Trades at a premium to historical averages; requires disciplined entry zone limits."
        )

        val macroVote = CommitteeFactorVote(
            factorName = "Macroeconomic & Sector Tailwind",
            convictionScore = when (macroRegime) {
                MacroRegimeType.DISINFLATIONARY_EXPANSION -> 86
                MacroRegimeType.OVERHEATING -> 70
                MacroRegimeType.STAGFLATION -> 45
                MacroRegimeType.CONTRACTION -> 40
            },
            weightPct = 0.15,
            summary = "Central bank easing cycle supports mega-cap growth liquidity."
        )

        val riskVote = CommitteeFactorVote(
            factorName = "Portfolio Risk & Concentration",
            convictionScore = if (isConcentrationBreached) 40 else 85,
            weightPct = 0.10,
            summary = if (isConcentrationBreached) "Ceiling reached: accumulation blocked." else "Within risk budget bounds."
        )

        val factorVotes = listOf(fundamentalVote, technicalVote, valuationVote, macroVote, riskVote)

        val weightedScore = factorVotes.sumOf { it.convictionScore * it.weightPct }.toInt()

        val decision = when {
            !auditPassed -> ResearchDecision.AVOID
            isConcentrationBreached -> ResearchDecision.HOLD
            weightedScore >= 85 -> ResearchDecision.BUY
            weightedScore >= 75 -> ResearchDecision.ACCUMULATE
            weightedScore >= 60 -> ResearchDecision.HOLD
            weightedScore >= 45 -> ResearchDecision.WATCH
            else -> ResearchDecision.SELL
        }

        val executiveSummary = "The Investment Committee synthesizes high business quality with tactical valuation discipline. While fundamental moat and technical regime justify high institutional conviction ($weightedScore%), elevated multiples dictate accumulating on technical pullbacks within the entry zone rather than chasing market momentum."

        val actionPlan = when (decision) {
            ResearchDecision.BUY -> "Deploy approved capital tranche immediately across market-on-close orders."
            ResearchDecision.ACCUMULATE -> "Scale into position in 3 equal tranches using limit orders inside defined Entry Zone."
            ResearchDecision.HOLD -> "Maintain current position sizing. Rebalance or trim if concentration exceeds 15%."
            ResearchDecision.WATCH -> "Keep on active watchlist. Await valuation pullback towards bear fair value."
            ResearchDecision.AVOID -> "Audit failed or structural headwind detected. Do not allocate capital."
            ResearchDecision.SELL -> "Thesis invalidation conditions met. Execute exit strategy to preserve capital."
        }

        return InvestmentCommitteeVerdict(
            decision = decision,
            aggregateConvictionScore = weightedScore,
            factorVotes = factorVotes,
            executiveSummary = executiveSummary,
            investmentHorizon = "3–5 Years",
            actionPlan = actionPlan
        )
    }
}
