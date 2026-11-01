package com.example.domain.model

enum class AssetClass(val displayName: String) {
    EQUITY_US("US Equity"),
    EQUITY_INDIA("Indian Equity"),
    ETF("Exchange Traded Fund"),
    MUTUAL_FUND("Mutual Fund"),
    COMMODITY("Commodity / Gold"),
    BOND("Govt Bond / Yield"),
    FOREX("Currency / FX")
}

enum class Exchange(val code: String, val region: String) {
    NASDAQ("NASDAQ", "US"),
    NYSE("NYSE", "US"),
    NSE("NSE", "India"),
    BSE("BSE", "India"),
    MCX("MCX", "India"),
    GLOBAL("GLOBAL", "Global")
}

enum class Currency(val symbol: String) {
    USD("$"),
    INR("₹")
}

enum class FreshnessState(val label: String, val isPermittedForDecisions: Boolean) {
    LIVE("LIVE", true),
    FRESH("FRESH", true),
    LATEST_AVAILABLE("LATEST OFFICIAL", true),
    AGING("AGING", false),
    STALE("STALE", false),
    CURRENT_DATA_UNAVAILABLE("CURRENT DATA UNAVAILABLE", false)
}

enum class SourceType(val tierLevel: Int) {
    PRIMARY_EXCHANGE(1),
    REGULATORY_FILING(1),
    CENTRAL_BANK(1),
    INSTITUTIONAL_TAPE(2),
    REPUTABLE_MEDIA(3),
    SECONDARY(4)
}

enum class ReliabilityScore {
    HIGH,
    MEDIUM,
    LOW,
    UNVERIFIED
}

enum class DataClass(val maxAgeMinutes: Long) {
    INTRADAY_PRICE(15),
    DAILY_CLOSE(1440),
    FINANCIAL_STATEMENT(172800), // ~120 days
    MACRO_INDICATOR(64800),      // ~45 days
    NEWS_EVENT(360),            // 6 hours
    ETF_CONSTITUENTS(10080),     // 7 days
    ETF_NAV(1440),
    MUTUAL_FUND_NAV(1440),
    MACRO_CENTRAL_BANK(43200),
    TREASURY_YIELD(60),
    COMPANY_NEWS(360)
}

enum class ResearchDecision(val actionColorHex: Long) {
    BUY(0xFF00E676),
    ACCUMULATE(0xFF00E5FF),
    HOLD(0xFFFFD700),
    WATCH(0xFFB388FF),
    AVOID(0xFFFFB300),
    SELL(0xFFFF5252)
}

enum class AgentStatus {
    QUEUED,
    RUNNING,
    WAITING,
    COMPLETED,
    FAILED,
    CANCELLED,
    RESEARCH_REQUIRED
}

enum class ConflictType(val label: String) {
    VALUATION_VS_MOMENTUM("Valuation Premium vs Momentum Trend"),
    MICRO_VS_MACRO("Company Micro Moat vs Macro Headwind"),
    FUNDAMENTAL_VS_TECHNICAL("Fundamental Quality vs Technical Breakdown"),
    PORTFOLIO_CONCENTRATION("High Conviction vs Portfolio Concentration Limit"),
    EARNINGS_VS_GUIDANCE("Beat Earnings vs Softened Forward Guidance")
}

