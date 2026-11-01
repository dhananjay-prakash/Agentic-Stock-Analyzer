package com.example.domain.model

data class FreshnessMetadata(
    val source: String,
    val sourceType: SourceType,
    val publishedAt: Long,
    val effectiveAt: Long,
    val retrievedAt: Long,
    val dataClass: DataClass,
    val freshnessStatus: FreshnessState,
    val reliability: ReliabilityScore,
    val confidence: Float
)

data class Security(
    val symbol: String,
    val name: String,
    val assetClass: AssetClass,
    val exchange: Exchange,
    val currency: Currency,
    val sector: String,
    val industry: String = "",
    val isTracked: Boolean = true
)

data class SecurityQuote(
    val symbol: String,
    val price: Double,
    val changeAmount: Double,
    val changePercent: Double,
    val dayOpen: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val fiftyTwoWeekHigh: Double,
    val fiftyTwoWeekLow: Double,
    val peRatio: Double? = null,
    val marketCap: Double? = null,
    val freshness: FreshnessMetadata
)

data class CompanyFinancials(
    val symbol: String,
    val fiscalPeriod: String,
    val revenueBillion: Double,
    val revenueGrowthYoY: Double,
    val operatingMarginPct: Double,
    val netIncomeBillion: Double,
    val freeCashFlowBillion: Double,
    val totalCashBillion: Double,
    val totalDebtBillion: Double,
    val roicPct: Double,
    val filingDate: Long,
    val freshness: FreshnessMetadata
)

data class MacroIndicator(
    val id: String,
    val name: String,
    val region: String,
    val currentValue: Double,
    val previousValue: Double,
    val consensusEstimate: Double?,
    val unit: String,
    val releaseDate: Long,
    val freshness: FreshnessMetadata
)

data class NewsItem(
    val id: String,
    val symbol: String,
    val title: String,
    val publisher: String,
    val publishedAt: Long,
    val sentiment: String, // POSITIVE, NEUTRAL, NEGATIVE
    val impactMagnitude: String, // HIGH, MEDIUM, LOW
    val freshness: FreshnessMetadata
)

data class EvidenceItem(
    val claim: String,
    val source: String,
    val sourceType: SourceType,
    val supportingData: String,
    val freshnessStatus: FreshnessState,
    val reliability: ReliabilityScore,
    val timestamp: Long
)

data class ConflictItem(
    val type: ConflictType,
    val description: String,
    val agentSignals: Map<String, String>,
    val resolutionNote: String
)

data class AgentStepProgress(
    val agentId: String,
    val agentName: String,
    val status: AgentStatus,
    val detail: String = "",
    val durationMs: Long = 0L
)

data class ResearchReport(
    val reportId: String,
    val symbol: String,
    val securityName: String,
    val decision: ResearchDecision,
    val confidencePct: Int,
    val investmentHorizon: String,
    val thesis: String,
    val currentDataStatus: FreshnessState,
    val fairValueLow: Double,
    val fairValueHigh: Double,
    val currentPrice: Double,
    val entryZoneLow: Double,
    val entryZoneHigh: Double,
    val expectedUpsidePct: Double,
    val expectedDownsidePct: Double,
    val catalysts: List<String>,
    val risks: List<String>,
    val bearCase: String,
    val technicalView: String,
    val macroView: String,
    val portfolioFitNote: String,
    val whatWouldChangeThesis: String,
    val evidenceList: List<EvidenceItem>,
    val conflicts: List<ConflictItem>,
    val generatedAt: Long,
    val agentProgressList: List<AgentStepProgress> = emptyList(),
    val valuationAssessment: ValuationAssessment? = null,
    val riskDecomposition: RiskDecomposition? = null,
    val positionSizing: PositionSizingRecommendation? = null,
    val conflictResolutions: List<ConflictResolution> = emptyList(),
    val devilsAdvocate: DevilsAdvocateAssessment? = null,
    val auditVerdict: ResearchAuditVerdict? = null,
    val committeeVerdict: InvestmentCommitteeVerdict? = null
)
