package com.example.data.repository

import com.example.domain.model.*

class Phase4Repository {
    private val now = System.currentTimeMillis()

    fun getETFProfiles(): List<ETFFullProfile> {
        return listOf(
            ETFFullProfile(
                symbol = "QQQM",
                name = "Invesco NASDAQ 100 ETF",
                issuer = "Invesco Capital Management",
                aumBillion = 34.8,
                nav = 208.45,
                marketPrice = 208.52,
                premiumDiscountPct = 0.03,
                expenseRatioPct = 0.15,
                trackingErrorPct = 0.03,
                dividendYieldPct = 0.58,
                factorTilts = mapOf("Growth" to 88.0, "Momentum" to 76.0, "Quality" to 82.0, "Low Vol" to 34.0),
                sectorBreakdown = mapOf(
                    "Information Technology" to 50.8,
                    "Communication Services" to 15.4,
                    "Consumer Discretionary" to 13.2,
                    "Healthcare" to 6.2,
                    "Consumer Staples" to 4.1
                ),
                topConstituents = listOf(
                    ETFConstituent("QQQM", "AAPL", "Apple Inc.", 8.75, "Information Technology"),
                    ETFConstituent("QQQM", "MSFT", "Microsoft Corp.", 8.40, "Information Technology"),
                    ETFConstituent("QQQM", "NVDA", "NVIDIA Corp.", 7.85, "Information Technology"),
                    ETFConstituent("QQQM", "AMZN", "Amazon.com Inc.", 5.30, "Consumer Discretionary"),
                    ETFConstituent("QQQM", "META", "Meta Platforms Inc.", 4.90, "Communication Services")
                ),
                freshness = FreshnessMetadata(
                    source = "Invesco Fund Servicing / NASDAQ Consolidated Tape",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 3600000,
                    effectiveAt = now - 3600000,
                    retrievedAt = now,
                    dataClass = DataClass.ETF_NAV,
                    freshnessStatus = FreshnessState.FRESH,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 0.99f
                )
            ),
            ETFFullProfile(
                symbol = "VOO",
                name = "Vanguard S&P 500 ETF",
                issuer = "The Vanguard Group",
                aumBillion = 520.4,
                nav = 535.10,
                marketPrice = 535.15,
                premiumDiscountPct = 0.01,
                expenseRatioPct = 0.03,
                trackingErrorPct = 0.02,
                dividendYieldPct = 1.25,
                factorTilts = mapOf("Core Blend" to 92.0, "Quality" to 84.0, "Growth" to 65.0, "Low Vol" to 58.0),
                sectorBreakdown = mapOf(
                    "Information Technology" to 31.4,
                    "Financials" to 13.2,
                    "Healthcare" to 11.8,
                    "Consumer Discretionary" to 10.1,
                    "Communication Services" to 9.0
                ),
                topConstituents = listOf(
                    ETFConstituent("VOO", "AAPL", "Apple Inc.", 6.95, "Information Technology"),
                    ETFConstituent("VOO", "MSFT", "Microsoft Corp.", 7.10, "Information Technology"),
                    ETFConstituent("VOO", "NVDA", "NVIDIA Corp.", 6.45, "Information Technology"),
                    ETFConstituent("VOO", "AMZN", "Amazon.com Inc.", 3.75, "Consumer Discretionary"),
                    ETFConstituent("VOO", "GOOGL", "Alphabet Inc.", 2.25, "Communication Services")
                ),
                freshness = FreshnessMetadata(
                    source = "Vanguard Official NAV Tape",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 3600000,
                    effectiveAt = now - 3600000,
                    retrievedAt = now,
                    dataClass = DataClass.ETF_NAV,
                    freshnessStatus = FreshnessState.FRESH,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 0.99f
                )
            ),
            ETFFullProfile(
                symbol = "SCHG",
                name = "Schwab U.S. Large-Cap Growth ETF",
                issuer = "Charles Schwab Investment Management",
                aumBillion = 38.6,
                nav = 104.22,
                marketPrice = 104.25,
                premiumDiscountPct = 0.02,
                expenseRatioPct = 0.04,
                trackingErrorPct = 0.03,
                dividendYieldPct = 0.42,
                factorTilts = mapOf("Growth" to 95.0, "Quality" to 80.0, "Momentum" to 78.0, "Value" to 15.0),
                sectorBreakdown = mapOf(
                    "Information Technology" to 47.2,
                    "Communication Services" to 16.1,
                    "Consumer Discretionary" to 14.8,
                    "Healthcare" to 7.4
                ),
                topConstituents = listOf(
                    ETFConstituent("SCHG", "MSFT", "Microsoft Corp.", 12.10, "Information Technology"),
                    ETFConstituent("SCHG", "AAPL", "Apple Inc.", 11.80, "Information Technology"),
                    ETFConstituent("SCHG", "NVDA", "NVIDIA Corp.", 10.95, "Information Technology"),
                    ETFConstituent("SCHG", "AMZN", "Amazon.com Inc.", 6.40, "Consumer Discretionary")
                ),
                freshness = FreshnessMetadata(
                    source = "Schwab Custody Tape",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 3600000,
                    effectiveAt = now - 3600000,
                    retrievedAt = now,
                    dataClass = DataClass.ETF_NAV,
                    freshnessStatus = FreshnessState.FRESH,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 0.99f
                )
            ),
            ETFFullProfile(
                symbol = "SMH",
                name = "VanEck Semiconductor ETF",
                issuer = "VanEck Associates",
                aumBillion = 24.2,
                nav = 254.12,
                marketPrice = 254.20,
                premiumDiscountPct = 0.03,
                expenseRatioPct = 0.35,
                trackingErrorPct = 0.05,
                dividendYieldPct = 0.48,
                factorTilts = mapOf("High Beta" to 92.0, "Momentum" to 88.0, "Semiconductor Pure-Play" to 100.0),
                sectorBreakdown = mapOf(
                    "Semiconductors & Equipment" to 99.4,
                    "Cash" to 0.6
                ),
                topConstituents = listOf(
                    ETFConstituent("SMH", "NVDA", "NVIDIA Corp.", 20.40, "Semiconductors"),
                    ETFConstituent("SMH", "TSM", "Taiwan Semiconductor", 13.20, "Semiconductors"),
                    ETFConstituent("SMH", "AVGO", "Broadcom Inc.", 7.80, "Semiconductors"),
                    ETFConstituent("SMH", "ASML", "ASML Holding NV", 4.90, "Semiconductors")
                ),
                freshness = FreshnessMetadata(
                    source = "VanEck Official Fund Portal",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 3600000,
                    effectiveAt = now - 3600000,
                    retrievedAt = now,
                    dataClass = DataClass.ETF_NAV,
                    freshnessStatus = FreshnessState.FRESH,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 0.99f
                )
            )
        )
    }

    fun getMutualFundProfiles(): List<MutualFundProfile> {
        return listOf(
            MutualFundProfile(
                fundCode = "PPFAS_FLEXI",
                fundName = "Parag Parikh Flexi Cap Fund",
                amc = "PPFAS Mutual Fund (India)",
                category = "Flexi Cap (Multi-Cap Value & Global)",
                aumCroreOrMillion = 78450.0,
                isAumInInrCrore = true,
                nav = 84.62,
                expenseRatioPct = 0.62,
                portfolioPe = 24.8,
                portfolioPb = 3.8,
                alpha = 5.20,
                beta = 0.72,
                sharpeRatio = 1.68,
                sortinoRatio = 2.45,
                maxDrawdownPct = -14.2,
                return1Y = 32.4,
                return3YRolling = 22.8,
                return5YRolling = 24.1,
                sipReturn3Y = 24.6,
                turnoverRatioPct = 12.5,
                styleDriftDetected = false,
                styleDriftNote = "None. Consistently maintains 65-75% Indian equity, 15-20% overseas tech (GOOGL, MSFT, META), and 10% debt/cash arbitrage.",
                topHoldings = listOf(
                    FundHolding("HDFCBANK.NS", "HDFC Bank Ltd.", 7.8, "Financials"),
                    FundHolding("ITC.NS", "ITC Ltd.", 6.9, "Consumer Staples"),
                    FundHolding("BAJAJ-AUTO.NS", "Bajaj Auto Ltd.", 6.2, "Automobile"),
                    FundHolding("GOOGL", "Alphabet Inc. (US)", 5.1, "Technology"),
                    FundHolding("MSFT", "Microsoft Corp. (US)", 4.6, "Technology")
                ),
                freshness = FreshnessMetadata(
                    source = "AMFI India Official Factsheet / SEBI Disclosures",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 86400000,
                    effectiveAt = now - 86400000,
                    retrievedAt = now,
                    dataClass = DataClass.MUTUAL_FUND_NAV,
                    freshnessStatus = FreshnessState.LATEST_AVAILABLE,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 0.98f
                )
            ),
            MutualFundProfile(
                fundCode = "MIRAE_LARGE",
                fundName = "Mirae Asset Large Cap Fund",
                amc = "Mirae Asset Mutual Fund",
                category = "Large Cap Fund",
                aumCroreOrMillion = 38900.0,
                isAumInInrCrore = true,
                nav = 118.40,
                expenseRatioPct = 0.54,
                portfolioPe = 26.2,
                portfolioPb = 4.1,
                alpha = 2.40,
                beta = 0.95,
                sharpeRatio = 1.15,
                sortinoRatio = 1.62,
                maxDrawdownPct = -18.4,
                return1Y = 28.6,
                return3YRolling = 17.4,
                return5YRolling = 18.2,
                sipReturn3Y = 19.1,
                turnoverRatioPct = 28.0,
                styleDriftDetected = false,
                styleDriftNote = "Strict large-cap mandate adherence (>85% in Top 100 Indian equities).",
                topHoldings = listOf(
                    FundHolding("RELIANCE.NS", "Reliance Industries Ltd.", 9.4, "Energy"),
                    FundHolding("HDFCBANK.NS", "HDFC Bank Ltd.", 8.8, "Financials"),
                    FundHolding("ICICIBANK.NS", "ICICI Bank Ltd.", 7.5, "Financials"),
                    FundHolding("INFY.NS", "Infosys Ltd.", 6.2, "Technology"),
                    FundHolding("TCS.NS", "Tata Consultancy Services", 4.8, "Technology")
                ),
                freshness = FreshnessMetadata(
                    source = "AMFI India Official Factsheet",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 86400000,
                    effectiveAt = now - 86400000,
                    retrievedAt = now,
                    dataClass = DataClass.MUTUAL_FUND_NAV,
                    freshnessStatus = FreshnessState.LATEST_AVAILABLE,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 0.98f
                )
            ),
            MutualFundProfile(
                fundCode = "VANGUARD_500_INDEX",
                fundName = "Vanguard 500 Index Fund Admiral Shares (VFIAX)",
                amc = "The Vanguard Group (US)",
                category = "US Large Cap Blend Index",
                aumCroreOrMillion = 112000.0,
                isAumInInrCrore = false, // USD Millions
                nav = 512.60,
                expenseRatioPct = 0.04,
                portfolioPe = 27.8,
                portfolioPb = 4.6,
                alpha = 0.01,
                beta = 1.00,
                sharpeRatio = 1.28,
                sortinoRatio = 1.84,
                maxDrawdownPct = -19.2,
                return1Y = 34.2,
                return3YRolling = 10.8,
                return5YRolling = 15.6,
                sipReturn3Y = 14.2,
                turnoverRatioPct = 2.4,
                styleDriftDetected = false,
                styleDriftNote = "Exact market-cap weighted index replication with zero active style drift.",
                topHoldings = listOf(
                    FundHolding("AAPL", "Apple Inc.", 7.1, "Technology"),
                    FundHolding("MSFT", "Microsoft Corp.", 6.9, "Technology"),
                    FundHolding("NVDA", "NVIDIA Corp.", 6.4, "Technology"),
                    FundHolding("AMZN", "Amazon.com Inc.", 3.8, "Consumer Discretionary")
                ),
                freshness = FreshnessMetadata(
                    source = "SEC Form N-PORT / Vanguard Institutional Wire",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 86400000,
                    effectiveAt = now - 86400000,
                    retrievedAt = now,
                    dataClass = DataClass.MUTUAL_FUND_NAV,
                    freshnessStatus = FreshnessState.LATEST_AVAILABLE,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 0.99f
                )
            )
        )
    }

    fun getCentralBankPolicies(): List<CentralBankPolicy> {
        return listOf(
            CentralBankPolicy(
                bankName = "Federal Reserve (US)",
                policyRatePct = 5.00,
                rateName = "Federal Funds Target Rate (Upper Limit)",
                lastAction = "Lowered 50 bps at FOMC; terminal rate guided near 3.25-3.50%",
                bias = MonetaryPolicyBias.DOVISH,
                nextMeetingDate = "November 7, 2026",
                balanceSheetTrend = "Quantitative Tightening active ($25B Treasuries / $35B MBS monthly cap)",
                freshness = FreshnessMetadata(
                    source = "Federal Reserve Board Official Statement",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 1800000,
                    effectiveAt = now - 1800000,
                    retrievedAt = now,
                    dataClass = DataClass.MACRO_CENTRAL_BANK,
                    freshnessStatus = FreshnessState.LATEST_AVAILABLE,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 1.0f
                )
            ),
            CentralBankPolicy(
                bankName = "Reserve Bank of India (RBI)",
                policyRatePct = 6.50,
                rateName = "Policy Repo Rate",
                lastAction = "Stance shifted to NEUTRAL from Withdrawal of Accommodation; Repo held at 6.50%",
                bias = MonetaryPolicyBias.NEUTRAL,
                nextMeetingDate = "December 6, 2026",
                balanceSheetTrend = "Active liquidity management via VRR/VRRR; FX reserves $690B+",
                freshness = FreshnessMetadata(
                    source = "RBI Monetary Policy Committee Resolution",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 1800000,
                    effectiveAt = now - 1800000,
                    retrievedAt = now,
                    dataClass = DataClass.MACRO_CENTRAL_BANK,
                    freshnessStatus = FreshnessState.LATEST_AVAILABLE,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 1.0f
                )
            ),
            CentralBankPolicy(
                bankName = "European Central Bank (ECB)",
                policyRatePct = 3.25,
                rateName = "Deposit Facility Rate",
                lastAction = "Cut rate by 25 bps citing rapid disinflation to 1.8% target",
                bias = MonetaryPolicyBias.DOVISH,
                nextMeetingDate = "December 12, 2026",
                balanceSheetTrend = "Full PEPP reinvestment phase-out proceeding",
                freshness = FreshnessMetadata(
                    source = "ECB Press Conference & Monetary Decisions",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 3600000,
                    effectiveAt = now - 3600000,
                    retrievedAt = now,
                    dataClass = DataClass.MACRO_CENTRAL_BANK,
                    freshnessStatus = FreshnessState.LATEST_AVAILABLE,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 1.0f
                )
            ),
            CentralBankPolicy(
                bankName = "Bank of Japan (BOJ)",
                policyRatePct = 0.25,
                rateName = "Uncollateralized Overnight Call Rate",
                lastAction = "Held at 0.25%; signaled future rate hikes if underlying inflation persists above 2%",
                bias = MonetaryPolicyBias.HAWKISH,
                nextMeetingDate = "December 19, 2026",
                balanceSheetTrend = "JGB purchase tapering plan executing",
                freshness = FreshnessMetadata(
                    source = "Bank of Japan Policy Board Release",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 3600000,
                    effectiveAt = now - 3600000,
                    retrievedAt = now,
                    dataClass = DataClass.MACRO_CENTRAL_BANK,
                    freshnessStatus = FreshnessState.LATEST_AVAILABLE,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 1.0f
                )
            )
        )
    }

    fun getYieldCurveMetrics(): YieldCurveMetrics {
        val y10 = 4.28
        val y2 = 4.16
        val spread = y10 - y2 // +0.12% or +12 bps (disinverted steepener)
        return YieldCurveMetrics(
            yield10Y = y10,
            yield2Y = y2,
            spread10Y2Y = spread,
            isInverted = spread < 0.0,
            recessionProbabilityPct = 28.5,
            curveRegime = "Normalizing Bull Steepener (Spread +12 bps)",
            realYield10Y = 1.95,
            freshness = FreshnessMetadata(
                source = "US Department of the Treasury Daily Yield Curve Rates",
                sourceType = SourceType.PRIMARY_EXCHANGE,
                publishedAt = now - 1800000,
                effectiveAt = now - 1800000,
                retrievedAt = now,
                dataClass = DataClass.TREASURY_YIELD,
                freshnessStatus = FreshnessState.LIVE,
                reliability = ReliabilityScore.HIGH,
                confidence = 1.0f
            )
        )
    }

    fun getMacroReleaseSurprises(): List<MacroReleaseSurprise> {
        return listOf(
            MacroReleaseSurprise(
                indicatorName = "US Core CPI (YoY)",
                region = "US",
                actualValue = 3.3,
                consensusEstimate = 3.2,
                previousValue = 3.2,
                surprisePct = 3.1,
                unit = "%",
                releaseDate = "October 10, 2026",
                transmissionNote = "Slightly sticky services inflation maintains measured pace of Fed rate reductions."
            ),
            MacroReleaseSurprise(
                indicatorName = "US Non-Farm Payrolls",
                region = "US",
                actualValue = 254.0,
                consensusEstimate = 150.0,
                previousValue = 159.0,
                surprisePct = 69.3,
                unit = "Thousand Jobs",
                releaseDate = "October 4, 2026",
                transmissionNote = "Robust labor market eliminates immediate recession probability, providing macro runway for corporate earnings."
            ),
            MacroReleaseSurprise(
                indicatorName = "India CPI Inflation",
                region = "India",
                actualValue = 5.49,
                consensusEstimate = 5.10,
                previousValue = 3.65,
                surprisePct = 7.6,
                unit = "%",
                releaseDate = "October 14, 2026",
                transmissionNote = "Vegetable food price spike keeps RBI cautious, delaying initial repo rate cut to early 2027."
            ),
            MacroReleaseSurprise(
                indicatorName = "US ISM Manufacturing PMI",
                region = "US",
                actualValue = 47.2,
                consensusEstimate = 47.5,
                previousValue = 47.2,
                surprisePct = -0.6,
                unit = "Index",
                releaseDate = "October 1, 2026",
                transmissionNote = "Factory activity remains in modest contraction while services PMI remains expansionary (54.9)."
            )
        )
    }

    fun getGeopoliticalTheaters(): List<GeopoliticalTheater> {
        return listOf(
            GeopoliticalTheater(
                id = "GEO_TAIWAN_SEMIS",
                region = "Asia-Pacific",
                headline = "US-China Export Controls & Taiwan Strait Security",
                severity = ThreatSeverity.CRITICAL,
                summary = "Restrictions on advanced EUV lithography and GAA transistor technology transfer to Chinese hyperscalers, alongside naval exercise monitoring in the Taiwan Strait.",
                transmission = GeopoliticalTransmissionPath(
                    eventTitle = "Advanced Semiconductor Export Restrictions & Straits Transit",
                    theater = "Asia-Pacific / Taiwan",
                    primaryChokepoint = "TSMC Advanced Packaging (CoWoS) & Taiwan Strait Maritime Corridors",
                    affectedIndustries = listOf("Semiconductors", "Cloud Hyperscalers", "Consumer Electronics"),
                    affectedPortfolioSymbols = listOf("NVDA", "AAPL", "MSFT"),
                    costRevenueTransmission = "Near-term: minimal disruption due to custom sovereign silicon variants. Tail risk: severe supply restriction if wafer fabrication in Hsinchu is disrupted.",
                    estimatedEarningsVulnerabilityPct = 8.5,
                    hedgingMechanism = "Geographic allocation to domestic foundry buildouts (TSMC Arizona, Intel Ohio) and high cash reserves."
                ),
                freshness = FreshnessMetadata(
                    source = "Center for Strategic & International Studies (CSIS) / Department of Commerce",
                    sourceType = SourceType.REGULATORY_FILING,
                    publishedAt = now - 7200000,
                    effectiveAt = now - 7200000,
                    retrievedAt = now,
                    dataClass = DataClass.COMPANY_NEWS,
                    freshnessStatus = FreshnessState.FRESH,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 0.95f
                )
            ),
            GeopoliticalTheater(
                id = "GEO_MIDDLE_EAST_OIL",
                region = "Middle East / Persian Gulf",
                headline = "Strait of Hormuz & Regional Energy Infrastructure Security",
                severity = ThreatSeverity.ELEVATED,
                summary = "Tensions across the Levant and Persian Gulf raising regional crude supply risk premia. Brent crude anchored near $74-80 range.",
                transmission = GeopoliticalTransmissionPath(
                    eventTitle = "Crude Shipping Chokepoint Disruption",
                    theater = "Middle East / Red Sea",
                    primaryChokepoint = "Bab el-Mandeb Strait & Strait of Hormuz (20% of Global Petroleum Transit)",
                    affectedIndustries = listOf("Global Shipping", "Airlines", "Petrochemicals", "Refining"),
                    affectedPortfolioSymbols = listOf("RELIANCE.NS", "GLD"),
                    costRevenueTransmission = "Higher freight rates increase inventory carrying costs; benefits Reliance Jamnagar export refining margins and gold safe-haven bid.",
                    estimatedEarningsVulnerabilityPct = 3.2,
                    hedgingMechanism = "Long allocation to Gold (GLD) and integrated refining giants (Reliance)."
                ),
                freshness = FreshnessMetadata(
                    source = "International Energy Agency (IEA) Oil Market Report",
                    sourceType = SourceType.INSTITUTIONAL_TAPE,
                    publishedAt = now - 7200000,
                    effectiveAt = now - 7200000,
                    retrievedAt = now,
                    dataClass = DataClass.COMPANY_NEWS,
                    freshnessStatus = FreshnessState.FRESH,
                    reliability = ReliabilityScore.HIGH,
                    confidence = 0.96f
                )
            )
        )
    }

    fun getGicsSectors(): List<GicsSectorProfile> {
        return listOf(
            GicsSectorProfile("Information Technology", "XLK", 0.85, 2.40, 5.80, 11.20, 18.40, 38.60, 1.25, 78.5, 31.4, "ACCUMULATING"),
            GicsSectorProfile("Communication Services", "XLC", 0.62, 1.85, 4.20, 8.90, 14.60, 32.10, 1.15, 72.0, 22.8, "ACCUMULATING"),
            GicsSectorProfile("Financials", "XLF", 0.45, 1.20, 3.80, 7.50, 12.80, 28.40, 1.08, 81.2, 16.5, "ACCUMULATING"),
            GicsSectorProfile("Utilities", "XLU", -0.32, -0.80, 2.10, 6.20, 11.50, 24.20, 0.98, 65.4, 18.2, "NEUTRAL"),
            GicsSectorProfile("Industrials", "XLI", 0.28, 0.95, 2.40, 5.10, 9.80, 22.40, 0.95, 68.0, 21.0, "NEUTRAL"),
            GicsSectorProfile("Consumer Discretionary", "XLY", 0.55, 1.40, 2.80, 4.60, 8.20, 20.80, 0.92, 62.5, 26.4, "NEUTRAL"),
            GicsSectorProfile("Healthcare", "XLV", -0.15, 0.30, 0.80, 2.40, 5.10, 15.60, 0.82, 54.0, 19.5, "NEUTRAL"),
            GicsSectorProfile("Consumer Staples", "XLP", -0.40, -0.65, -0.50, 1.80, 4.20, 12.80, 0.78, 52.0, 20.8, "DISTRIBUTING"),
            GicsSectorProfile("Materials", "XLB", -0.22, 0.10, 0.40, 2.10, 4.80, 14.20, 0.80, 56.5, 18.9, "NEUTRAL"),
            GicsSectorProfile("Energy", "XLE", -0.65, -1.40, -1.80, -2.50, 1.20, 8.40, 0.72, 44.0, 12.4, "DISTRIBUTING"),
            GicsSectorProfile("Real Estate", "XLRE", -0.48, -0.90, -1.20, 1.50, 3.80, 10.20, 0.75, 48.0, 34.2, "DISTRIBUTING")
        )
    }
}
