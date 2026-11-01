package com.example

import com.example.data.controltower.DataQualityGate
import com.example.data.controltower.FreshnessEngine
import com.example.data.controltower.QualityValidationResult
import com.example.data.provider.failover.ProviderFailoverRouter
import com.example.data.provider.remote.*
import com.example.domain.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testFreshnessEngine_intradayPrices() {
        val now = System.currentTimeMillis()

        // 5 minutes old intraday price should be LIVE
        val fiveMinutesAgo = now - (5 * 60 * 1000)
        val stateLive = FreshnessEngine.evaluateFreshness(
            dataClass = DataClass.INTRADAY_PRICE,
            publishedAt = fiveMinutesAgo,
            retrievedAt = now,
            currentTime = now
        )
        assertEquals(FreshnessState.LIVE, stateLive)

        // 30 minutes old price should be FRESH
        val thirtyMinutesAgo = now - (30 * 60 * 1000)
        val stateFresh = FreshnessEngine.evaluateFreshness(
            dataClass = DataClass.INTRADAY_PRICE,
            publishedAt = thirtyMinutesAgo,
            retrievedAt = now,
            currentTime = now
        )
        assertEquals(FreshnessState.FRESH, stateFresh)

        // 25 hours old price MUST be STALE (24-hour rule)
        val twentyFiveHoursAgo = now - (25 * 60 * 60 * 1000)
        val stateStale = FreshnessEngine.evaluateFreshness(
            dataClass = DataClass.INTRADAY_PRICE,
            publishedAt = twentyFiveHoursAgo,
            retrievedAt = now,
            currentTime = now
        )
        assertEquals(FreshnessState.STALE, stateStale)
    }

    @Test
    fun testDataQualityGate_rejectsNegativePriceAndCorruptData() {
        val now = System.currentTimeMillis()
        val metadata = FreshnessMetadata(
            source = "Test Feed",
            sourceType = SourceType.PRIMARY_EXCHANGE,
            publishedAt = now,
            effectiveAt = now,
            retrievedAt = now,
            dataClass = DataClass.INTRADAY_PRICE,
            freshnessStatus = FreshnessState.LIVE,
            reliability = ReliabilityScore.HIGH,
            confidence = 1.0f
        )

        // Corrupt price (<= 0)
        val invalidPriceQuote = SecurityQuote(
            symbol = "BAD",
            price = -10.0,
            changeAmount = 0.0,
            changePercent = 0.0,
            dayOpen = 10.0,
            dayHigh = 10.0,
            dayLow = 10.0,
            volume = 1000,
            fiftyTwoWeekHigh = 100.0,
            fiftyTwoWeekLow = 10.0,
            freshness = metadata
        )
        val result = DataQualityGate.validateQuote(invalidPriceQuote)
        assertTrue(result is QualityValidationResult.Rejected)

        // Valid quote
        val validQuote = invalidPriceQuote.copy(price = 150.0)
        val validResult = DataQualityGate.validateQuote(validQuote)
        assertEquals(QualityValidationResult.Valid, validResult)
    }

    @Test
    fun testProviderFailoverRouter_engagesSecondaryOnPrimaryFailure() = runBlocking {
        val primary = PrimaryExchangeMarketDataProvider()
        val secondary = SecondaryInstitutionalMarketDataProvider()
        val router = ProviderFailoverRouter(
            primaryMarketProvider = primary,
            secondaryMarketProvider = secondary,
            newsProvider = InstitutionalNewsProvider(),
            fundamentalProvider = FilingFundamentalProvider(),
            macroProvider = CentralBankMacroProvider()
        )

        // 1. Normal state: primary succeeds
        val primaryResult = router.getRoutedQuote("NVDA")
        assertTrue(primaryResult.isAvailable)
        assertFalse(primaryResult.isFailoverActive)
        assertEquals(primary.providerName, primaryResult.activeProvider)

        // 2. Simulate primary exchange failure (Layer 15)
        primary.simulateFailure = true
        val failoverResult = router.getRoutedQuote("NVDA")
        assertTrue(failoverResult.isAvailable)
        assertTrue(failoverResult.isFailoverActive)
        assertEquals(secondary.providerName, failoverResult.activeProvider)
        assertTrue(failoverResult.failureReason?.contains("Primary failed") == true)
    }

    @Test
    fun testFinancialMath_rsiCalculation() {
        // Monotonically increasing prices should produce an overbought RSI (>70)
        val upwardPrices = (1..20).map { it * 10.0 }
        val rsiUp = com.example.domain.math.FinancialMath.calculateRSI(upwardPrices, 14)
        assertTrue("Expected RSI > 70 for uptrend but got $rsiUp", rsiUp > 70.0)

        // Monotonically decreasing prices should produce an oversold RSI (<30)
        val downwardPrices = (20 downTo 1).map { it * 10.0 }
        val rsiDown = com.example.domain.math.FinancialMath.calculateRSI(downwardPrices, 14)
        assertTrue("Expected RSI < 30 for downtrend but got $rsiDown", rsiDown < 30.0)
    }

    @Test
    fun testFinancialMath_fundamentalScorecard() {
        val scorecard = com.example.domain.math.FinancialMath.computeFundamentalScorecard(
            marketCapBillion = 3000.0,
            freeCashFlowBillion = 90.0,
            roicPct = 35.0,
            totalCashBillion = 50.0,
            totalDebtBillion = 20.0,
            operatingMarginPct = 65.0
        )
        assertEquals(3.0, scorecard.fcfYieldPct, 0.01)
        assertEquals("STRONG (Net Cash Positive)", scorecard.financialHealthRating)
        assertEquals("EXCELLENT", scorecard.capitalAllocationRating)
    }

    @Test
    fun testPortfolioAlerts_thresholdBreachClassification() {
        val alert = com.example.data.local.entity.PortfolioAlertEntity(
            symbol = "NVDA",
            title = "Concentration Threshold Exceeded",
            message = "Effective weight 17.8% exceeds 15.0% policy ceiling.",
            severity = "CRITICAL",
            category = "CONCENTRATION",
            triggerMetric = "Weight: 17.8% (Limit: 15.0%)",
            isRead = false,
            actionableSymbol = "NVDA"
        )
        assertEquals("CRITICAL", alert.severity)
        assertEquals("CONCENTRATION", alert.category)
        assertFalse(alert.isRead)
        assertEquals("NVDA", alert.actionableSymbol)
    }

    @Test
    fun testETFAgent_factorAttributionAndTrackingError() {
        val repo = com.example.data.repository.Phase4Repository()
        val etfAgent = com.example.domain.agent.ETFAgent()
        val qqqmProfile = repo.getETFProfiles().first { it.symbol == "QQQM" }

        val output = etfAgent.analyze(qqqmProfile)
        assertEquals("QQQM", output.profile.symbol)
        assertTrue(output.trackingEfficiency.contains("EXCELLENT"))
        assertTrue(output.factorTiltsSummary.contains("Growth"))
        assertTrue(output.evidence.isNotEmpty())
    }

    @Test
    fun testMutualFundAgent_riskAdjustedMetricsAndStyleDriftAudit() {
        val repo = com.example.data.repository.Phase4Repository()
        val mfAgent = com.example.domain.agent.MutualFundAgent()
        val ppfasProfile = repo.getMutualFundProfiles().first { it.fundCode == "PPFAS_FLEXI" }

        val output = mfAgent.analyze(ppfasProfile)
        assertTrue(output.riskAdjustedScore.contains("EXCEPTIONAL"))
        assertTrue(output.consistencyVerdict.contains("Multi-Year High Conviction"))
        assertFalse(output.profile.styleDriftDetected)
        assertTrue(output.styleDriftAudit.contains("CLEAN"))
    }

    @Test
    fun testMacroEconomicAgent_regimeClassification() {
        val repo = com.example.data.repository.Phase4Repository()
        val macroAgent = com.example.domain.agent.MacroEconomicAgent()

        val output = macroAgent.analyze(
            policies = repo.getCentralBankPolicies(),
            yieldCurve = repo.getYieldCurveMetrics(),
            surprises = repo.getMacroReleaseSurprises()
        )

        assertNotNull(output.assessment)
        assertEquals(com.example.domain.model.MacroRegimeType.DISINFLATIONARY_EXPANSION, output.assessment.currentRegime)
        assertTrue(output.rateTrajectory.contains("Fed: 5.0%"))
        assertTrue(output.rateTrajectory.contains("RBI: 6.5%"))
    }

    @Test
    fun testGeopoliticalAgent_transmissionPipeline() {
        val repo = com.example.data.repository.Phase4Repository()
        val geoAgent = com.example.domain.agent.GeopoliticalAgent()

        val output = geoAgent.analyze(repo.getGeopoliticalTheaters())
        assertTrue(output.activeTheaters.size >= 2)
        assertTrue(output.highRiskChokepoints.any { it.contains("TSMC") || it.contains("Taiwan") })
        assertTrue(output.portfolioVulnerabilitySummary.contains("NVDA"))
    }

    @Test
    fun testSectorRotationAgent_cyclePhaseClassification() {
        val repo = com.example.data.repository.Phase4Repository()
        val sectorAgent = com.example.domain.agent.SectorRotationAgent()

        val output = sectorAgent.analyze(repo.getGicsSectors())
        assertEquals(com.example.domain.model.BusinessCyclePhase.MID_CYCLE, output.rotationAnalysis.cyclePhase)
        assertTrue(output.rotationAnalysis.topLeadingSectors.contains("Information Technology"))
        assertTrue(output.rotationAnalysis.laggingSectors.contains("Energy") || output.rotationAnalysis.laggingSectors.contains("Real Estate"))
    }

    @Test
    fun testValuationMath_multiStageDcfAndMarginOfSafety() {
        val dcf = com.example.domain.math.ValuationMath.computeDCF(
            initialFcfBillion = 30.0,
            stage1GrowthPct = 20.0,
            stage2GrowthPct = 10.0,
            terminalGrowthPct = 3.0,
            waccPct = 9.0,
            netCashBillion = 25.0,
            sharesDilutedBillion = 24.5
        )
        assertTrue(dcf.presentValueCashFlows > 0)
        assertTrue(dcf.presentValueTerminalValue > 0)
        assertTrue(dcf.impliedPerShareValue > 50.0)

        // Margin of Safety & Classification
        val (undervaluedClass, mosUnder) = com.example.domain.math.ValuationMath.classifyValuation(
            currentPrice = 100.0,
            baseFairValue = 135.0
        )
        assertEquals(com.example.domain.model.ValuationClassification.SIGNIFICANTLY_UNDERVALUED, undervaluedClass)
        assertTrue(mosUnder >= 20.0)

        val (overvaluedClass, mosOver) = com.example.domain.math.ValuationMath.classifyValuation(
            currentPrice = 160.0,
            baseFairValue = 120.0
        )
        assertEquals(com.example.domain.model.ValuationClassification.SIGNIFICANTLY_OVERVALUED, overvaluedClass)
        assertTrue(mosOver < -25.0)
    }

    @Test
    fun testRiskAndSizing_concentrationLimitEnforcement() {
        val riskAgent = com.example.domain.agent.RiskAndSizingAgent()

        // 1. Holding under limit (e.g. $80k in $1M portfolio = 8%)
        val (riskNormal, sizingNormal) = riskAgent.analyze(
            symbol = "NVDA",
            portfolioTotalValue = 1_000_000.0,
            currentHoldingValue = 80_000.0,
            sectorCurrentWeightPct = 25.0,
            convictionScore = 85
        )
        assertFalse(riskNormal.isConcentrationBreached)
        assertTrue(sizingNormal.suggestedIncrementalAllocationPct > 0)
        assertTrue(sizingNormal.dollarTrancheEstimate > 0)

        // 2. Holding exceeding 15% single-stock ceiling (e.g. $165k in $1M portfolio = 16.5%)
        val (riskBreach, sizingBreach) = riskAgent.analyze(
            symbol = "NVDA",
            portfolioTotalValue = 1_000_000.0,
            currentHoldingValue = 165_000.0,
            sectorCurrentWeightPct = 32.0,
            convictionScore = 95
        )
        assertTrue(riskBreach.isConcentrationBreached)
        assertEquals(0.0, sizingBreach.suggestedIncrementalAllocationPct, 0.001)
        assertEquals(0.0, sizingBreach.dollarTrancheEstimate, 0.001)
        assertTrue(sizingBreach.sizingRationale.contains("15% maximum risk ceiling"))
    }

    @Test
    fun testDevilsAdvocate_falsificationTriggersAndFatalFlaws() {
        val advocate = com.example.domain.agent.DevilsAdvocateAgent()
        val assessment = advocate.stressTest("NVDA", com.example.domain.model.ResearchDecision.BUY)

        assertNotNull(assessment)
        assertTrue(assessment.thesisFalsificationTriggers.isNotEmpty())
        assertTrue(assessment.thesisFalsificationTriggers.any { it.triggerCondition.contains("Top 4 cloud providers") || it.riskCategory.contains("Capex") })
        assertTrue(assessment.thesisFalsificationTriggers.all { it.estimatedDrawdownImpactPct > 0 })
        assertTrue(assessment.fatalFlawVerdict.isNotEmpty())
    }

    @Test
    fun testResearchAuditor_gatingVerification() {
        val auditor = com.example.domain.agent.ResearchAuditorAgent()

        // Gating pass scenario
        val passVerdict = auditor.audit(
            quoteFreshness = com.example.domain.model.FreshnessState.LIVE,
            financialFreshness = com.example.domain.model.FreshnessState.LATEST_AVAILABLE,
            macroFreshness = com.example.domain.model.FreshnessState.LIVE,
            hasConflictsResolved = true,
            hasDevilsAdvocatePassed = true,
            isConcentrationUnderLimit = true
        )
        assertTrue(passVerdict.isAuditPassed)
        assertEquals(7, passVerdict.checklist.size)
        assertTrue(passVerdict.overallStatus.contains("PASSED"))

        // Gating fail scenario: Stale market quote
        val failVerdict = auditor.audit(
            quoteFreshness = com.example.domain.model.FreshnessState.STALE,
            financialFreshness = com.example.domain.model.FreshnessState.LATEST_AVAILABLE,
            macroFreshness = com.example.domain.model.FreshnessState.LIVE,
            hasConflictsResolved = true,
            hasDevilsAdvocatePassed = true,
            isConcentrationUnderLimit = true
        )
        assertFalse(failVerdict.isAuditPassed)
        assertTrue(failVerdict.overallStatus.contains("REJECTED"))
    }

    @Test
    fun testInvestmentCommittee_decisionAndConvictionWeighting() {
        val committee = com.example.domain.agent.InvestmentCommitteeAgent()

        // High conviction scenario
        val highConvictionVerdict = committee.evaluate(
            symbol = "NVDA",
            fundamentalMoat = "STRUCTURAL AI SEMICONDUCTOR MONOPOLY",
            technicalRegime = com.example.domain.math.TechnicalRegime.STRONG_UPTREND,
            valuationClass = com.example.domain.model.ValuationClassification.FAIRLY_VALUED,
            macroRegime = com.example.domain.model.MacroRegimeType.DISINFLATIONARY_EXPANSION,
            auditPassed = true,
            isConcentrationBreached = false
        )
        assertTrue(highConvictionVerdict.aggregateConvictionScore >= 75)
        assertEquals(com.example.domain.model.ResearchDecision.BUY, highConvictionVerdict.decision)
        assertEquals(5, highConvictionVerdict.factorVotes.size)

        // Concentration breach forces HOLD
        val concentrationHoldVerdict = committee.evaluate(
            symbol = "NVDA",
            fundamentalMoat = "STRUCTURAL AI SEMICONDUCTOR MONOPOLY",
            technicalRegime = com.example.domain.math.TechnicalRegime.STRONG_UPTREND,
            valuationClass = com.example.domain.model.ValuationClassification.SIGNIFICANTLY_UNDERVALUED,
            macroRegime = com.example.domain.model.MacroRegimeType.DISINFLATIONARY_EXPANSION,
            auditPassed = true,
            isConcentrationBreached = true
        )
        assertEquals(com.example.domain.model.ResearchDecision.HOLD, concentrationHoldVerdict.decision)

        // Audit failure forces AVOID
        val auditFailVerdict = committee.evaluate(
            symbol = "NVDA",
            fundamentalMoat = "STRUCTURAL AI SEMICONDUCTOR MONOPOLY",
            technicalRegime = com.example.domain.math.TechnicalRegime.STRONG_UPTREND,
            valuationClass = com.example.domain.model.ValuationClassification.SIGNIFICANTLY_UNDERVALUED,
            macroRegime = com.example.domain.model.MacroRegimeType.DISINFLATIONARY_EXPANSION,
            auditPassed = false,
            isConcentrationBreached = false
        )
        assertEquals(com.example.domain.model.ResearchDecision.AVOID, auditFailVerdict.decision)
    }
}


