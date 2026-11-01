package com.example.data.provider.remote

import com.example.data.provider.core.*
import com.example.domain.model.*
import kotlinx.coroutines.delay

class InstitutionalNewsProvider : NewsProvider {
    override val providerName: String = "Dow Jones & Regulatory Wire"
    override val tier: SourceType = SourceType.REPUTABLE_MEDIA

    override suspend fun fetchCompanyNews(symbol: String, limit: Int): ProviderResult<List<NewsItem>> {
        delay(60)
        val now = System.currentTimeMillis()

        val sampleNews = when (symbol.uppercase()) {
            "NVDA" -> listOf(
                NewsItem(
                    id = "NEWS_NVDA_1",
                    symbol = "NVDA",
                    title = "Hyperscalers commit record $65B aggregate quarterly capex toward AI data center expansion.",
                    publisher = "Dow Jones Institutional Wire",
                    publishedAt = now - 1800000,
                    sentiment = "POSITIVE",
                    impactMagnitude = "HIGH",
                    freshness = FreshnessMetadata("Dow Jones", SourceType.REPUTABLE_MEDIA, now - 1800000, now, now, DataClass.NEWS_EVENT, FreshnessState.FRESH, ReliabilityScore.HIGH, 0.95f)
                ),
                NewsItem(
                    id = "NEWS_NVDA_2",
                    symbol = "NVDA",
                    title = "Commerce department issues revised guidance on advanced semiconductor export licensing.",
                    publisher = "Regulatory Transparency Feed",
                    publishedAt = now - 7200000,
                    sentiment = "NEUTRAL",
                    impactMagnitude = "MEDIUM",
                    freshness = FreshnessMetadata("Federal Register", SourceType.REGULATORY_FILING, now - 7200000, now, now, DataClass.NEWS_EVENT, FreshnessState.FRESH, ReliabilityScore.HIGH, 0.98f)
                )
            )
            "RELIANCE.NS" -> listOf(
                NewsItem(
                    id = "NEWS_REL_1",
                    symbol = "RELIANCE.NS",
                    title = "Jio Platforms reports 14% growth in average revenue per user following tariff optimization.",
                    publisher = "BSE Corporate Announcements",
                    publishedAt = now - 3600000,
                    sentiment = "POSITIVE",
                    impactMagnitude = "HIGH",
                    freshness = FreshnessMetadata("BSE Exchange", SourceType.REGULATORY_FILING, now - 3600000, now, now, DataClass.NEWS_EVENT, FreshnessState.FRESH, ReliabilityScore.HIGH, 0.99f)
                )
            )
            else -> listOf(
                NewsItem(
                    id = "NEWS_GEN_1",
                    symbol = symbol,
                    title = "Trading volume steady as institutional investors assess macro rate policy.",
                    publisher = "Financial News Desk",
                    publishedAt = now - 5400000,
                    sentiment = "NEUTRAL",
                    impactMagnitude = "LOW",
                    freshness = FreshnessMetadata("News Wire", SourceType.REPUTABLE_MEDIA, now - 5400000, now, now, DataClass.NEWS_EVENT, FreshnessState.FRESH, ReliabilityScore.HIGH, 0.90f)
                )
            )
        }

        return ProviderResult.Success(
            data = sampleNews.take(limit),
            metadata = ProviderMetadata(providerName, tier, 60, now)
        )
    }
}

class CentralBankMacroProvider : MacroDataProvider {
    override val providerName: String = "Federal Reserve & Reserve Bank of India Statistical Releases"
    override val tier: SourceType = SourceType.CENTRAL_BANK

    override suspend fun fetchMacroIndicators(): ProviderResult<List<MacroIndicator>> {
        delay(40)
        val now = System.currentTimeMillis()

        val indicators = listOf(
            MacroIndicator(
                id = "FED_FUNDS",
                name = "Federal Funds Effective Rate",
                region = "US",
                currentValue = 4.75,
                previousValue = 5.00,
                consensusEstimate = 4.75,
                unit = "%",
                releaseDate = now - 864000000, // 10 days ago
                freshness = FreshnessMetadata("Federal Reserve", SourceType.CENTRAL_BANK, now - 864000000, now, now, DataClass.MACRO_INDICATOR, FreshnessState.LATEST_AVAILABLE, ReliabilityScore.HIGH, 1.0f)
            ),
            MacroIndicator(
                id = "US_10Y_YIELD",
                name = "US 10-Year Treasury Constant Maturity",
                region = "US",
                currentValue = 4.22,
                previousValue = 4.28,
                consensusEstimate = null,
                unit = "%",
                releaseDate = now - 3600000,
                freshness = FreshnessMetadata("US Treasury", SourceType.PRIMARY_EXCHANGE, now - 3600000, now, now, DataClass.MACRO_INDICATOR, FreshnessState.LIVE, ReliabilityScore.HIGH, 1.0f)
            ),
            MacroIndicator(
                id = "RBI_REPO",
                name = "RBI Benchmark Policy Repo Rate",
                region = "India",
                currentValue = 6.50,
                previousValue = 6.50,
                consensusEstimate = 6.50,
                unit = "%",
                releaseDate = now - 1296000000, // 15 days ago
                freshness = FreshnessMetadata("Reserve Bank of India", SourceType.CENTRAL_BANK, now - 1296000000, now, now, DataClass.MACRO_INDICATOR, FreshnessState.LATEST_AVAILABLE, ReliabilityScore.HIGH, 1.0f)
            ),
            MacroIndicator(
                id = "US_CPI",
                name = "US Consumer Price Index (Headline YoY)",
                region = "US",
                currentValue = 2.40,
                previousValue = 2.50,
                consensusEstimate = 2.30,
                unit = "%",
                releaseDate = now - 1728000000, // 20 days ago
                freshness = FreshnessMetadata("Bureau of Labor Statistics", SourceType.CENTRAL_BANK, now - 1728000000, now, now, DataClass.MACRO_INDICATOR, FreshnessState.LATEST_AVAILABLE, ReliabilityScore.HIGH, 1.0f)
            )
        )

        return ProviderResult.Success(
            data = indicators,
            metadata = ProviderMetadata(providerName, tier, 40, now)
        )
    }
}

class FilingFundamentalProvider : FundamentalDataProvider {
    override val providerName: String = "SEC EDGAR / Ministry of Corporate Affairs (MCA)"
    override val tier: SourceType = SourceType.REGULATORY_FILING

    override suspend fun fetchFinancials(symbol: String): ProviderResult<CompanyFinancials> {
        delay(70)
        val now = System.currentTimeMillis()

        val financials = when (symbol.uppercase()) {
            "NVDA" -> CompanyFinancials(
                symbol = "NVDA",
                fiscalPeriod = "Q3 FY26",
                revenueBillion = 35.1,
                revenueGrowthYoY = 94.0,
                operatingMarginPct = 65.4,
                netIncomeBillion = 19.3,
                freeCashFlowBillion = 13.5,
                totalCashBillion = 34.8,
                totalDebtBillion = 9.8,
                roicPct = 48.2,
                filingDate = now - 1814400000, // ~21 days ago
                freshness = FreshnessMetadata("SEC EDGAR 10-Q", SourceType.REGULATORY_FILING, now - 1814400000, now, now, DataClass.FINANCIAL_STATEMENT, FreshnessState.LATEST_AVAILABLE, ReliabilityScore.HIGH, 1.0f)
            )
            "MSFT" -> CompanyFinancials(
                symbol = "MSFT",
                fiscalPeriod = "Q1 FY26",
                revenueBillion = 65.6,
                revenueGrowthYoY = 16.0,
                operatingMarginPct = 44.8,
                netIncomeBillion = 24.7,
                freeCashFlowBillion = 19.3,
                totalCashBillion = 78.4,
                totalDebtBillion = 44.0,
                roicPct = 28.5,
                filingDate = now - 2419200000, // ~28 days ago
                freshness = FreshnessMetadata("SEC EDGAR 10-Q", SourceType.REGULATORY_FILING, now - 2419200000, now, now, DataClass.FINANCIAL_STATEMENT, FreshnessState.LATEST_AVAILABLE, ReliabilityScore.HIGH, 1.0f)
            )
            "RELIANCE.NS" -> CompanyFinancials(
                symbol = "RELIANCE.NS",
                fiscalPeriod = "Q2 FY26",
                revenueBillion = 28.4,
                revenueGrowthYoY = 8.5,
                operatingMarginPct = 17.2,
                netIncomeBillion = 2.3,
                freeCashFlowBillion = 1.9,
                totalCashBillion = 14.5,
                totalDebtBillion = 38.0,
                roicPct = 11.2,
                filingDate = now - 2160000000,
                freshness = FreshnessMetadata("BSE/NSE Filing", SourceType.REGULATORY_FILING, now - 2160000000, now, now, DataClass.FINANCIAL_STATEMENT, FreshnessState.LATEST_AVAILABLE, ReliabilityScore.HIGH, 0.99f)
            )
            else -> CompanyFinancials(
                symbol = symbol.uppercase(),
                fiscalPeriod = "FY25",
                revenueBillion = 10.0,
                revenueGrowthYoY = 10.0,
                operatingMarginPct = 20.0,
                netIncomeBillion = 2.0,
                freeCashFlowBillion = 1.5,
                totalCashBillion = 5.0,
                totalDebtBillion = 2.0,
                roicPct = 15.0,
                filingDate = now - 5184000000,
                freshness = FreshnessMetadata("Annual Report", SourceType.REGULATORY_FILING, now - 5184000000, now, now, DataClass.FINANCIAL_STATEMENT, FreshnessState.LATEST_AVAILABLE, ReliabilityScore.HIGH, 0.95f)
            )
        }

        return ProviderResult.Success(
            data = financials,
            metadata = ProviderMetadata(providerName, tier, 70, now)
        )
    }
}
