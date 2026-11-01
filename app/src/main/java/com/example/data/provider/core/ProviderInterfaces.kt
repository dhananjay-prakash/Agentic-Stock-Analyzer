package com.example.data.provider.core

import com.example.domain.model.*

data class ProviderMetadata(
    val providerName: String,
    val tier: SourceType,
    val latencyMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class ProviderResult<out T> {
    data class Success<out T>(val data: T, val metadata: ProviderMetadata) : ProviderResult<T>()
    data class FailoverRequired(val reason: String, val throwable: Throwable? = null) : ProviderResult<Nothing>()
    data class CurrentDataUnavailable(val reason: String, val lastKnownTimestamp: Long? = null) : ProviderResult<Nothing>()
}

interface MarketDataProvider {
    val providerName: String
    val tier: SourceType
    suspend fun fetchQuote(symbol: String): ProviderResult<SecurityQuote>
    suspend fun fetchBatchQuotes(symbols: List<String>): Map<String, ProviderResult<SecurityQuote>>
}

interface NewsProvider {
    val providerName: String
    val tier: SourceType
    suspend fun fetchCompanyNews(symbol: String, limit: Int = 10): ProviderResult<List<NewsItem>>
}

interface FundamentalDataProvider {
    val providerName: String
    val tier: SourceType
    suspend fun fetchFinancials(symbol: String): ProviderResult<CompanyFinancials>
}

interface MacroDataProvider {
    val providerName: String
    val tier: SourceType
    suspend fun fetchMacroIndicators(): ProviderResult<List<MacroIndicator>>
}

interface ETFDataProvider {
    val providerName: String
    val tier: SourceType
    suspend fun fetchETFConstituents(etfSymbol: String): ProviderResult<List<ETFConstituent>>
}
