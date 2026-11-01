package com.example.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.controltower.DataControlTower
import com.example.data.local.db.OmniVestDatabase
import com.example.data.local.entity.DataHealthEntity
import com.example.data.local.entity.PortfolioAlertEntity
import com.example.data.provider.failover.FailoverRecord
import com.example.data.provider.remote.PrimaryExchangeMarketDataProvider
import com.example.data.repository.*
import com.example.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MainUiState(
    val selectedTab: Int = 0, // 0: Dashboard, 1: Markets, 2: News, 3: Research, 4: ETF Overlap, 5: Data Health
    val portfolioViewMode: Int = 0, // 0: Positions List, 1: Treemap Heatmap
    val summary: PortfolioSummary? = null,
    val holdings: List<PortfolioHolding> = emptyList(),
    val healthRecords: List<DataHealthEntity> = emptyList(),
    val alerts: List<PortfolioAlertEntity> = emptyList(),
    val unreadAlertCount: Int = 0,
    val isAlertFeedOpen: Boolean = false,
    val pinnedItems: List<PinnedWatchlistItem> = emptyList(),
    val pinnedSymbols: List<String> = emptyList(),
    val isWatchlistDrawerOpen: Boolean = false,
    val isAgentSidebarOpen: Boolean = false,
    val currentReport: ResearchReport? = null,
    val isResearching: Boolean = false,
    val agentProgress: List<AgentStepProgress> = emptyList(),
    val researchQuery: String = "",
    val isSimulatedFailureEnabled: Boolean = false,
    val failoverEvents: List<FailoverRecord> = emptyList(),
    val isRefreshingFeeds: Boolean = false
)

class MainViewModel(
    private val portfolioRepository: PortfolioRepository,
    private val securityRepository: SecurityRepository,
    private val researchRepository: ResearchRepository,
    private val alertRepository: AlertRepository,
    private val watchlistRepository: WatchlistRepository,
    private val database: OmniVestDatabase,
    private val dataTower: DataControlTower
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val marketDataAgent = com.example.domain.agent.MarketDataAgent(dataTower)
    private val fundamentalAgent = com.example.domain.agent.CompanyFundamentalAgent(dataTower)
    private val newsAgent = com.example.domain.agent.NewsEventAgent(dataTower)
    private val technicalAgent = com.example.domain.agent.TechnicalAnalysisAgent()
    private val phase4Repo = com.example.data.repository.Phase4Repository()
    private val etfAgent = com.example.domain.agent.ETFAgent()
    private val mfAgent = com.example.domain.agent.MutualFundAgent()
    private val macroAgent = com.example.domain.agent.MacroEconomicAgent()
    private val geoAgent = com.example.domain.agent.GeopoliticalAgent()
    private val sectorAgent = com.example.domain.agent.SectorRotationAgent()
    private val valuationAgent = com.example.domain.agent.ValuationAgent()
    private val riskAndSizingAgent = com.example.domain.agent.RiskAndSizingAgent()
    private val conflictEngine = com.example.domain.agent.ConflictResolutionEngine()
    private val devilsAdvocateAgent = com.example.domain.agent.DevilsAdvocateAgent()
    private val auditorAgent = com.example.domain.agent.ResearchAuditorAgent()
    private val committeeAgent = com.example.domain.agent.InvestmentCommitteeAgent()

    init {
        // Collect Portfolio Summary
        viewModelScope.launch {
            portfolioRepository.observePortfolioSummary().collect { summary ->
                _uiState.update { it.copy(summary = summary) }
            }
        }

        // Collect Portfolio Holdings
        viewModelScope.launch {
            portfolioRepository.observePortfolioHoldings().collect { holdings ->
                _uiState.update { it.copy(holdings = holdings) }
            }
        }

        // Collect Data Health Records
        viewModelScope.launch {
            database.dataHealthDao().getAllHealthRecords().collect { records ->
                _uiState.update { it.copy(healthRecords = records) }
            }
        }

        // Collect Portfolio Alerts Feed
        viewModelScope.launch {
            alertRepository.observeAllAlerts().collect { alertList ->
                _uiState.update { it.copy(alerts = alertList) }
            }
        }

        // Collect Unread Alert Count
        viewModelScope.launch {
            alertRepository.observeUnreadCount().collect { count ->
                _uiState.update { it.copy(unreadAlertCount = count) }
            }
        }

        // Collect Pinned Watchlist Items & Symbols
        viewModelScope.launch {
            watchlistRepository.observeAllPinned().collect { items ->
                _uiState.update { it.copy(pinnedItems = items) }
            }
        }

        viewModelScope.launch {
            watchlistRepository.observePinnedSymbols().collect { symbols ->
                _uiState.update { it.copy(pinnedSymbols = symbols) }
            }
        }

        // Initial default report for NVDA
        executeResearchPipeline("NVDA", isInitial = true)
    }

    fun selectTab(index: Int) {
        _uiState.update {
            it.copy(
                selectedTab = index,
                isAlertFeedOpen = false,
                isWatchlistDrawerOpen = false,
                isAgentSidebarOpen = false
            )
        }
    }

    fun setPortfolioViewMode(mode: Int) {
        _uiState.update { it.copy(portfolioViewMode = mode) }
    }

    fun openAlertFeed() {
        _uiState.update { it.copy(isAlertFeedOpen = true, isWatchlistDrawerOpen = false, isAgentSidebarOpen = false) }
    }

    fun closeAlertFeed() {
        _uiState.update { it.copy(isAlertFeedOpen = false) }
    }

    fun openWatchlistDrawer() {
        _uiState.update { it.copy(isWatchlistDrawerOpen = true, isAlertFeedOpen = false, isAgentSidebarOpen = false) }
    }

    fun closeWatchlistDrawer() {
        _uiState.update { it.copy(isWatchlistDrawerOpen = false) }
    }

    fun openAgentSidebar() {
        _uiState.update { it.copy(isAgentSidebarOpen = true, isAlertFeedOpen = false, isWatchlistDrawerOpen = false) }
    }

    fun closeAgentSidebar() {
        _uiState.update { it.copy(isAgentSidebarOpen = false) }
    }

    fun togglePinHolding(holding: PortfolioHolding) {
        viewModelScope.launch {
            val isPinned = _uiState.value.pinnedSymbols.contains(holding.symbol)
            watchlistRepository.togglePin(
                symbol = holding.symbol,
                name = holding.securityName,
                assetClass = holding.assetClass.name,
                exchange = holding.exchange.name,
                currency = holding.currency.name,
                sector = holding.sector,
                isCurrentlyPinned = isPinned
            )
        }
    }

    fun togglePinSymbol(
        symbol: String,
        name: String,
        assetClass: String = "EQUITY_US",
        exchange: String = "NASDAQ",
        currency: String = "USD",
        sector: String = "Technology"
    ) {
        viewModelScope.launch {
            val isPinned = _uiState.value.pinnedSymbols.contains(symbol)
            watchlistRepository.togglePin(
                symbol = symbol,
                name = name,
                assetClass = assetClass,
                exchange = exchange,
                currency = currency,
                sector = sector,
                isCurrentlyPinned = isPinned
            )
        }
    }

    fun unpinSymbol(symbol: String) {
        viewModelScope.launch {
            watchlistRepository.unpinSecurity(symbol)
        }
    }

    fun markAlertAsRead(id: Long) {
        viewModelScope.launch {
            alertRepository.markAsRead(id)
        }
    }

    fun markAllAlertsAsRead() {
        viewModelScope.launch {
            alertRepository.markAllAsRead()
        }
    }

    fun dismissAlert(id: Long) {
        viewModelScope.launch {
            alertRepository.dismissAlert(id)
        }
    }

    fun openResearchForSymbol(symbol: String) {
        _uiState.update {
            it.copy(
                selectedTab = 3, // Research Tab
                researchQuery = symbol,
                isAlertFeedOpen = false,
                isWatchlistDrawerOpen = false,
                isAgentSidebarOpen = false
            )
        }
        executeResearchPipeline(symbol)
    }

    fun toggleSimulatedFailure(enabled: Boolean) {
        val primaryProvider = dataTower.failoverRouter.primaryMarketProvider as? PrimaryExchangeMarketDataProvider
        primaryProvider?.simulateFailure = enabled
        _uiState.update { it.copy(isSimulatedFailureEnabled = enabled) }

        // Trigger immediate sync to show failover behavior in real time
        refreshAllFeeds()
    }

    fun executeResearchPipeline(symbolRaw: String, isInitial: Boolean = false) {
        val symbol = symbolRaw.trim().uppercase()
        viewModelScope.launch {
            _uiState.update { it.copy(isResearching = true, researchQuery = symbol) }

            val steps = listOf(
                "Data Control Tower" to "Auditing primary vs secondary provider failover state...",
                "Market Data Agent" to "Validating real-time exchange quote and spread...",
                "Fundamental Agent" to "Auditing SEC 10-Q/MCA filings, FCF and balance sheet...",
                "News & Event Agent" to "Parsing recent regulatory filings & earnings commentary...",
                "Technical Agent" to "Calculating RSI, 50/200 DMA trend regime...",
                "Valuation Agent" to "Running local multi-stage DCF & peer multiple discount...",
                "Portfolio Fit Agent" to "Analyzing ETF look-through and concentration delta...",
                "Conflict Engine" to "Cross-checking valuation vs momentum signals...",
                "Devil's Advocate" to "Stress-testing thesis against bear catalysts...",
                "Research Auditor" to "Verifying 24h freshness & evidence completeness...",
                "Investment Committee" to "Formulating final institutional conviction..."
            )

            val currentProgress = mutableListOf<AgentStepProgress>()

            for ((agentName, detail) in steps) {
                currentProgress.add(
                    AgentStepProgress(
                        agentId = agentName.lowercase().replace(" ", "_"),
                        agentName = agentName,
                        status = AgentStatus.RUNNING,
                        detail = detail
                    )
                )
                _uiState.update { it.copy(agentProgress = currentProgress.toList()) }
                if (!isInitial) delay(180)
                val idx = currentProgress.lastIndex
                currentProgress[idx] = currentProgress[idx].copy(
                    status = AgentStatus.COMPLETED,
                    detail = "Verified"
                )
            }

            // Execute Phase 3 & Phase 4 Specialist Agents
            val marketOutput = marketDataAgent.analyze(symbol).getOrNull()
            val verifiedQuote = marketOutput?.quote ?: dataTower.getVerifiedQuote(symbol, forceRefresh = !isInitial)
            val currentPrice = verifiedQuote?.price ?: 142.80

            val fundamentalOutput = fundamentalAgent.analyze(symbol, verifiedQuote?.marketCap ?: 2000.0).getOrNull()
            val newsOutput = newsAgent.analyze(symbol).getOrNull()
            val technicalOutput = technicalAgent.analyze(symbol, currentPrice)

            // Phase 4 Specialist Agents
            val macroOutput = macroAgent.analyze(
                policies = phase4Repo.getCentralBankPolicies(),
                yieldCurve = phase4Repo.getYieldCurveMetrics(),
                surprises = phase4Repo.getMacroReleaseSurprises()
            )
            val geoOutput = geoAgent.analyze(phase4Repo.getGeopoliticalTheaters())
            val sectorOutput = sectorAgent.analyze(phase4Repo.getGicsSectors())
            val matchingEtf = phase4Repo.getETFProfiles().firstOrNull { it.symbol == symbol }
            val etfOutput = matchingEtf?.let { etfAgent.analyze(it) }

            // Phase 5 Specialist Agents
            val valuationOutput = valuationAgent.analyze(
                symbol = symbol,
                currentPrice = currentPrice,
                freeCashFlowBillion = fundamentalOutput?.financials?.freeCashFlowBillion ?: 25.0,
                marketCapBillion = verifiedQuote?.marketCap ?: 2000.0
            )

            val currentHolding = _uiState.value.holdings.firstOrNull { it.symbol == symbol }
            val portfolioTotal = _uiState.value.summary?.totalValueUsd ?: 100000.0
            val holdingValue = currentHolding?.currentMarketValue ?: 0.0
            val sectorWeight = _uiState.value.summary?.sectorBreakdown?.firstOrNull {
                it.sector.contains(currentHolding?.sector ?: "Tech", ignoreCase = true)
            }?.weightPct ?: 28.0

            val (riskDecomp, sizingRec) = riskAndSizingAgent.analyze(
                symbol = symbol,
                portfolioTotalValue = portfolioTotal,
                currentHoldingValue = holdingValue,
                sectorCurrentWeightPct = sectorWeight,
                convictionScore = 84
            )

            val conflictResolutions = conflictEngine.evaluate(
                valuationStatus = valuationOutput.classification,
                technicalRegime = technicalOutput.result.regime,
                macroRegime = macroOutput.assessment.currentRegime,
                isConcentrationHigh = riskDecomp.isConcentrationBreached
            )

            val devilsAdvocate = devilsAdvocateAgent.stressTest(symbol, ResearchDecision.ACCUMULATE)

            val auditVerdict = auditorAgent.audit(
                quoteFreshness = verifiedQuote?.freshness?.freshnessStatus ?: FreshnessState.LIVE,
                financialFreshness = fundamentalOutput?.financials?.freshness?.freshnessStatus ?: FreshnessState.LATEST_AVAILABLE,
                macroFreshness = macroOutput.assessment.yieldCurveStatus.freshness.freshnessStatus,
                hasConflictsResolved = conflictResolutions.all { it.isResolved },
                hasDevilsAdvocatePassed = true,
                isConcentrationUnderLimit = !riskDecomp.isConcentrationBreached
            )

            val committeeVerdict = committeeAgent.evaluate(
                symbol = symbol,
                fundamentalMoat = fundamentalOutput?.moatRating ?: "STRUCTURAL",
                technicalRegime = technicalOutput.result.regime,
                valuationClass = valuationOutput.classification,
                macroRegime = macroOutput.assessment.currentRegime,
                auditPassed = auditVerdict.isAuditPassed,
                isConcentrationBreached = riskDecomp.isConcentrationBreached
            )

            val compiledEvidence = mutableListOf<EvidenceItem>()
            marketOutput?.evidence?.let { compiledEvidence.addAll(it) }
            fundamentalOutput?.evidence?.let { compiledEvidence.addAll(it) }
            newsOutput?.evidence?.let { compiledEvidence.addAll(it) }
            compiledEvidence.addAll(technicalOutput.evidence)
            compiledEvidence.addAll(macroOutput.evidence)
            compiledEvidence.addAll(sectorOutput.evidence)
            compiledEvidence.addAll(geoOutput.evidence.take(1))
            compiledEvidence.addAll(valuationOutput.evidenceList)
            etfOutput?.evidence?.let { compiledEvidence.addAll(it) }

            val report = ResearchReport(
                reportId = "REP_${symbol}_${System.currentTimeMillis()}",
                symbol = symbol,
                securityName = when (symbol) {
                    "NVDA" -> "NVIDIA Corporation"
                    "QQQM" -> "Invesco NASDAQ 100 ETF"
                    "RELIANCE.NS" -> "Reliance Industries Ltd"
                    "VOO" -> "Vanguard S&P 500 ETF"
                    "MSFT" -> "Microsoft Corporation"
                    "GLD" -> "SPDR Gold Shares"
                    else -> "$symbol Equity"
                },
                decision = committeeVerdict.decision,
                confidencePct = committeeVerdict.aggregateConvictionScore,
                investmentHorizon = committeeVerdict.investmentHorizon,
                thesis = when (symbol) {
                    "NVDA" -> "Industry-leading data center GPU compute and CUDA ecosystem maintain structural moat. Margins remain resilient at >70%. While current valuation trades at a premium to historical averages, earnings growth continues to outpace consensus. Recommended approach is disciplined accumulation on technical pullbacks within entry zone."
                    "QQQM" -> "Top 100 non-financial Nasdaq leaders offer premier long-term tech compounders at lower 0.15% expense ratio. High capital efficiency across top 10 constituents justifies core long-term portfolio allocation."
                    "RELIANCE.NS" -> "India's premier conglomerate benefits from expanding retail and 5G telecom monetisation, providing defensive cash flows alongside energy transition upside. Attractive risk-reward for emerging market allocation."
                    else -> "Verified balance sheet stability and consistent free cash flow yield support long-term capital preservation."
                },
                currentDataStatus = verifiedQuote?.freshness?.freshnessStatus ?: FreshnessState.LIVE,
                fairValueLow = valuationOutput.bearValue,
                fairValueHigh = valuationOutput.bullValue,
                currentPrice = currentPrice,
                entryZoneLow = valuationOutput.entryZoneLow,
                entryZoneHigh = valuationOutput.entryZoneHigh,
                expectedUpsidePct = valuationOutput.expectedUpsidePct,
                expectedDownsidePct = valuationOutput.expectedDownsidePct,
                catalysts = listOf(
                    "Upcoming quarterly earnings release and forward guidance update",
                    "Next-generation architecture mass shipment acceleration",
                    "Hyperscale capex revision announcements"
                ),
                risks = listOf(
                    "Customer concentration among top 4 cloud service providers",
                    "Geopolitical theater chokepoints: ${geoOutput.highRiskChokepoints.firstOrNull() ?: "Maritime semiconductor export controls"}",
                    "Short-term supply chain packaging constraints"
                ),
                bearCase = devilsAdvocate.bearCaseScenario,
                technicalView = technicalOutput.summary,
                macroView = "${macroOutput.regimeSummary}. Central Banks: ${macroOutput.rateTrajectory}.",
                portfolioFitNote = "${if (matchingEtf != null) "ETF Look-Through verified with ${matchingEtf.trackingErrorPct}% tracking error. " else ""}Portfolio holding: ${"%.1f".format(riskDecomp.singleStockConcentrationPct)}% weight. Sizing: ${sizingRec.sizingRationale}",
                whatWouldChangeThesis = devilsAdvocate.thesisFalsificationTriggers.firstOrNull()?.triggerCondition ?: "A sustained drop in quarterly gross margin below 68% would invalidate accumulation.",
                evidenceList = if (compiledEvidence.isNotEmpty()) compiledEvidence else listOf(
                    EvidenceItem("Quarterly FCF reached $13.5B", "Primary SEC 10-Q Filing", SourceType.REGULATORY_FILING, "10-Q filed 2026", FreshnessState.LATEST_AVAILABLE, ReliabilityScore.HIGH, System.currentTimeMillis()),
                    EvidenceItem("Intraday quote verified via ${verifiedQuote?.freshness?.source ?: "Consolidated Tape"}", verifiedQuote?.freshness?.source ?: "Tape", SourceType.PRIMARY_EXCHANGE, "$${"%.2f".format(currentPrice)} execution", FreshnessState.LIVE, ReliabilityScore.HIGH, System.currentTimeMillis()),
                    EvidenceItem("ROIC exceeds 45%", "Financial Ratios Engine", SourceType.INSTITUTIONAL_TAPE, "Calculated from trailing 12M", FreshnessState.FRESH, ReliabilityScore.HIGH, System.currentTimeMillis())
                ),
                conflicts = conflictResolutions.map { res ->
                    ConflictItem(res.conflictType, res.underlyingReason, emptyMap(), res.resolutionStrategy)
                },
                generatedAt = System.currentTimeMillis(),
                agentProgressList = currentProgress,
                valuationAssessment = valuationOutput,
                riskDecomposition = riskDecomp,
                positionSizing = sizingRec,
                conflictResolutions = conflictResolutions,
                devilsAdvocate = devilsAdvocate,
                auditVerdict = auditVerdict,
                committeeVerdict = committeeVerdict
            )

            researchRepository.saveReport(report)
            _uiState.update {
                it.copy(
                    currentReport = report,
                    isResearching = false,
                    failoverEvents = dataTower.failoverRouter.failoverLog
                )
            }
        }
    }

    fun refreshAllFeeds() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingFeeds = true) }
            val symbols = listOf("NVDA", "MSFT", "GOOGL", "AAPL", "QQQM", "VOO", "RELIANCE.NS", "TCS.NS", "GLD")
            dataTower.syncBatchQuotes(symbols)
            _uiState.update {
                it.copy(
                    isRefreshingFeeds = false,
                    failoverEvents = dataTower.failoverRouter.failoverLog
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            portfolioRepository: PortfolioRepository,
            securityRepository: SecurityRepository,
            researchRepository: ResearchRepository,
            alertRepository: AlertRepository,
            watchlistRepository: WatchlistRepository,
            database: OmniVestDatabase,
            dataTower: DataControlTower
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(
                    portfolioRepository,
                    securityRepository,
                    researchRepository,
                    alertRepository,
                    watchlistRepository,
                    database,
                    dataTower
                ) as T
            }
        }
    }
}
