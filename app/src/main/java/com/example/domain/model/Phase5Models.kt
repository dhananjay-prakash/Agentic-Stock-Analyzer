package com.example.domain.model

// --- Phase 5: Valuation Models ---

enum class ValuationClassification(val label: String, val colorHex: Long) {
    SIGNIFICANTLY_UNDERVALUED("Significantly Undervalued (>20% MoS)", 0xFF00E676),
    MODERATELY_UNDERVALUED("Moderately Undervalued (10-20% MoS)", 0xFF00E5FF),
    FAIRLY_VALUED("Fairly Valued (Within ±10% Band)", 0xFFFFD700),
    MODERATELY_OVERVALUED("Moderately Overvalued (10-25% Premium)", 0xFFFFB300),
    SIGNIFICANTLY_OVERVALUED("Significantly Overvalued (>25% Premium)", 0xFFFF5252)
}

data class DCFValuation(
    val stage1GrowthRatePct: Double,
    val stage2GrowthRatePct: Double,
    val terminalGrowthRatePct: Double,
    val waccPct: Double,
    val presentValueCashFlows: Double,
    val presentValueTerminalValue: Double,
    val enterpriseValue: Double,
    val netCashDebt: Double,
    val equityValue: Double,
    val impliedPerShareValue: Double
)

data class MultiplesValuation(
    val trailingPe: Double,
    val forwardPe: Double,
    val pegRatio: Double,
    val evToEbitda: Double,
    val evToSales: Double,
    val priceToFcf: Double,
    val peerAveragePe: Double,
    val historical5YrAveragePe: Double,
    val impliedMultiplePrice: Double
)

data class ValuationAssessment(
    val classification: ValuationClassification,
    val currentPrice: Double,
    val bearValue: Double,
    val baseValue: Double,
    val bullValue: Double,
    val marginOfSafetyPct: Double,
    val entryZoneLow: Double,
    val entryZoneHigh: Double,
    val expectedUpsidePct: Double,
    val expectedDownsidePct: Double,
    val dcfModel: DCFValuation,
    val multiplesModel: MultiplesValuation,
    val evidenceList: List<EvidenceItem>
)

// --- Phase 5: Risk & Position Sizing Models ---

data class RiskDecomposition(
    val singleStockConcentrationPct: Double,
    val concentrationPolicyLimitPct: Double = 15.0,
    val isConcentrationBreached: Boolean,
    val sectorWeightPct: Double,
    val sectorLimitPct: Double = 35.0,
    val isSectorLimitBreached: Boolean,
    val betaVsBenchmark: Double,
    val maxHistoricalDrawdownPct: Double,
    val annualizedVolatilityPct: Double,
    val liquidityTier: String,
    val riskSummary: String
)

data class PositionSizingRecommendation(
    val currentHoldingWeightPct: Double,
    val suggestedIncrementalAllocationPct: Double,
    val maxRecommendedWeightPct: Double,
    val sizingRationale: String,
    val dollarTrancheEstimate: Double
)

// --- Phase 5: Conflict Engine Models ---

data class ConflictResolution(
    val conflictType: ConflictType,
    val bullishSignals: List<String>,
    val bearishSignals: List<String>,
    val underlyingReason: String,
    val resolutionStrategy: String, // e.g. "Stagger purchases across entry zone"
    val isResolved: Boolean
)

// --- Phase 5: Devil's Advocate Models ---

data class ThesisFalsificationVector(
    val riskCategory: String, // e.g. "Customer Concentration", "Capex Normalization"
    val triggerCondition: String, // "Top 4 cloud providers reducing GPU purchases by >20%"
    val estimatedDrawdownImpactPct: Double,
    val earlyWarningMetric: String // "Quarterly backlog growth rate dropping below 10%"
)

data class DevilsAdvocateAssessment(
    val bearCaseScenario: String,
    val probabilityRating: String, // "LOW-MEDIUM (25%)"
    val thesisFalsificationTriggers: List<ThesisFalsificationVector>,
    val fatalFlawVerdict: String
)

// --- Phase 5: Research Auditor Models ---

data class AuditCheckItem(
    val checkName: String,
    val isPassed: Boolean,
    val detail: String
)

data class ResearchAuditVerdict(
    val isAuditPassed: Boolean,
    val overallStatus: String, // "PASSED - CERTIFIED FOR COMMITTEE", "REJECTED - DATA STALE"
    val checklist: List<AuditCheckItem>,
    val auditorTimestamp: Long = System.currentTimeMillis()
)

// --- Phase 5: Investment Committee Models ---

data class CommitteeFactorVote(
    val factorName: String, // Fundamental, Technical, Valuation, Macro, Risk
    val convictionScore: Int, // 0 to 100
    val weightPct: Double,
    val summary: String
)

data class InvestmentCommitteeVerdict(
    val decision: ResearchDecision,
    val aggregateConvictionScore: Int, // 0 to 100
    val factorVotes: List<CommitteeFactorVote>,
    val executiveSummary: String,
    val investmentHorizon: String,
    val actionPlan: String,
    val timestamp: Long = System.currentTimeMillis()
)
