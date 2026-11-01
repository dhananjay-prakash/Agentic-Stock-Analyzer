package com.example.domain.model

// --- Phase 4: ETF & Mutual Fund Models ---

data class ETFFullProfile(
    val symbol: String,
    val name: String,
    val issuer: String,
    val aumBillion: Double,
    val nav: Double,
    val marketPrice: Double,
    val premiumDiscountPct: Double,
    val expenseRatioPct: Double,
    val trackingErrorPct: Double,
    val dividendYieldPct: Double,
    val factorTilts: Map<String, Double>, // e.g. "Growth" to 85.0, "Momentum" to 78.0
    val sectorBreakdown: Map<String, Double>,
    val topConstituents: List<ETFConstituent>,
    val freshness: FreshnessMetadata
)

data class FundHolding(
    val symbol: String,
    val name: String,
    val weightPct: Double,
    val sector: String
)

data class MutualFundProfile(
    val fundCode: String,
    val fundName: String,
    val amc: String,
    val category: String, // e.g. "Flexi Cap", "Large Cap", "Index Fund"
    val aumCroreOrMillion: Double,
    val isAumInInrCrore: Boolean,
    val nav: Double,
    val expenseRatioPct: Double,
    val portfolioPe: Double,
    val portfolioPb: Double,
    val alpha: Double,
    val beta: Double,
    val sharpeRatio: Double,
    val sortinoRatio: Double,
    val maxDrawdownPct: Double,
    val return1Y: Double,
    val return3YRolling: Double,
    val return5YRolling: Double,
    val sipReturn3Y: Double,
    val turnoverRatioPct: Double,
    val styleDriftDetected: Boolean,
    val styleDriftNote: String,
    val topHoldings: List<FundHolding>,
    val freshness: FreshnessMetadata
)

// --- Phase 4: Macro Models ---

enum class MonetaryPolicyBias(val label: String) {
    HAWKISH("Hawkish (Tightening Bias)"),
    NEUTRAL("Neutral (Data Dependent)"),
    DOVISH("Dovish (Easing Bias)")
}

data class CentralBankPolicy(
    val bankName: String, // Federal Reserve, RBI, ECB, BOJ
    val policyRatePct: Double,
    val rateName: String, // Fed Funds, Repo Rate, Deposit Facility Rate
    val lastAction: String, // "Paused at 5.25-5.50%", "Held at 6.50%"
    val bias: MonetaryPolicyBias,
    val nextMeetingDate: String,
    val balanceSheetTrend: String, // Quantitative Tightening ($25B/mo run-off)
    val freshness: FreshnessMetadata
)

data class YieldCurveMetrics(
    val yield10Y: Double,
    val yield2Y: Double,
    val spread10Y2Y: Double, // 10Y - 2Y in basis points / percentage
    val isInverted: Boolean,
    val recessionProbabilityPct: Double,
    val curveRegime: String, // "Steepening", "Flattening", "Inverted Bearish"
    val realYield10Y: Double, // 10Y TIPS proxy
    val freshness: FreshnessMetadata
)

data class MacroReleaseSurprise(
    val indicatorName: String,
    val region: String,
    val actualValue: Double,
    val consensusEstimate: Double,
    val previousValue: Double,
    val surprisePct: Double,
    val unit: String,
    val releaseDate: String,
    val transmissionNote: String
)

enum class MacroRegimeType(val displayName: String, val assetImplication: String) {
    DISINFLATIONARY_EXPANSION("Disinflationary Expansion (Goldilocks)", "Equities Bullish, Growth/Tech Leadership"),
    STAGFLATION("Stagflationary Pressure", "Commodities/Gold Outperform, Equities P/E Compression"),
    OVERHEATING("Late-Cycle Overheating", "Energy/Materials Lead, Central Banks Tightening"),
    CONTRACTION("Economic Contraction / Slowdown", "Bonds/Defensives Lead, Cyclicals Lag")
}

data class MacroEconomicAssessment(
    val currentRegime: MacroRegimeType,
    val centralBankSummary: String,
    val yieldCurveStatus: YieldCurveMetrics,
    val keySurprises: List<MacroReleaseSurprise>,
    val globalLiquidityTrend: String,
    val fxRegime: String, // USD/INR and DXY dynamics
    val commodityRegime: String, // Oil, Gold, Copper
    val evidenceList: List<EvidenceItem>
)

// --- Phase 4: Global & Geopolitical Models ---

enum class ThreatSeverity(val label: String) {
    CRITICAL("CRITICAL TIER 1"),
    ELEVATED("ELEVATED TIER 2"),
    GUARDED("GUARDED TIER 3")
}

data class GeopoliticalTransmissionPath(
    val eventTitle: String,
    val theater: String,
    val primaryChokepoint: String, // e.g. "Taiwan Strait Wafer Fab Shipping Lanes"
    val affectedIndustries: List<String>,
    val affectedPortfolioSymbols: List<String>,
    val costRevenueTransmission: String,
    val estimatedEarningsVulnerabilityPct: Double,
    val hedgingMechanism: String
)

data class GeopoliticalTheater(
    val id: String,
    val region: String,
    val headline: String,
    val severity: ThreatSeverity,
    val summary: String,
    val transmission: GeopoliticalTransmissionPath,
    val freshness: FreshnessMetadata
)

// --- Phase 4: Sector & Rotation Models ---

data class GicsSectorProfile(
    val sectorName: String,
    val etfProxy: String,
    val return1D: Double,
    val return1W: Double,
    val return1M: Double,
    val return3M: Double,
    val return6M: Double,
    val return1Y: Double,
    val relativeStrengthVsBenchmark: Double, // > 1.0 means outperforming S&P / Nifty
    val pctAbove50Ema: Double,
    val medianPe: Double,
    val institutionalFlow: String // "ACCUMULATING", "NEUTRAL", "DISTRIBUTING"
)

enum class BusinessCyclePhase(val phaseName: String, val favoredSectors: List<String>) {
    EARLY_CYCLE("Early Cycle Recovery", listOf("Financials", "Consumer Discretionary", "Industrials")),
    MID_CYCLE("Mid Cycle Expansion", listOf("Information Technology", "Communication Services")),
    LATE_CYCLE("Late Cycle Peak", listOf("Energy", "Materials", "Healthcare")),
    RECESSION("Recessionary Defensive", listOf("Utilities", "Consumer Staples", "Healthcare"))
}

data class SectorRotationAnalysis(
    val cyclePhase: BusinessCyclePhase,
    val topLeadingSectors: List<String>,
    val laggingSectors: List<String>,
    val marketBreadthSummary: String,
    val sectors: List<GicsSectorProfile>,
    val evidenceList: List<EvidenceItem>
)
