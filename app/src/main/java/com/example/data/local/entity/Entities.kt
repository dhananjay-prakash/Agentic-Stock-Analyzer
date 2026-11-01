package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "securities")
data class SecurityEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val assetClass: String,
    val exchange: String,
    val currency: String,
    val sector: String,
    val industry: String,
    val isTracked: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "quotes",
    indices = [Index("symbol")]
)
data class QuoteEntity(
    @PrimaryKey val symbol: String,
    val price: Double,
    val changeAmount: Double,
    val changePercent: Double,
    val dayOpen: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val fiftyTwoWeekHigh: Double,
    val fiftyTwoWeekLow: Double,
    val peRatio: Double?,
    val marketCap: Double?,
    val source: String,
    val sourceType: String,
    val retrievedAt: Long,
    val publishedAt: Long,
    val effectiveAt: Long,
    val freshnessStatus: String,
    val reliability: String,
    val confidence: Float
)

@Entity(
    tableName = "financial_reports",
    indices = [Index("symbol")]
)
data class FinancialReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val source: String,
    val retrievedAt: Long
)

@Entity(
    tableName = "etf_constituents",
    primaryKeys = ["etfSymbol", "constituentSymbol"],
    indices = [Index("etfSymbol"), Index("constituentSymbol")]
)
data class ETFConstituentEntity(
    val etfSymbol: String,
    val constituentSymbol: String,
    val constituentName: String,
    val weightPct: Double,
    val sector: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "portfolio_holdings",
    indices = [Index("symbol")]
)
data class PortfolioHoldingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val securityName: String,
    val assetClass: String,
    val exchange: String,
    val currency: String,
    val sharesQuantity: Double,
    val averageCost: Double,
    val sector: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "research_reports",
    indices = [Index("symbol")]
)
data class ResearchReportEntity(
    @PrimaryKey val reportId: String,
    val symbol: String,
    val securityName: String,
    val decision: String,
    val confidencePct: Int,
    val investmentHorizon: String,
    val thesis: String,
    val currentDataStatus: String,
    val fairValueLow: Double,
    val fairValueHigh: Double,
    val currentPrice: Double,
    val entryZoneLow: Double,
    val entryZoneHigh: Double,
    val expectedUpsidePct: Double,
    val expectedDownsidePct: Double,
    val bearCase: String,
    val technicalView: String,
    val macroView: String,
    val portfolioFitNote: String,
    val whatWouldChangeThesis: String,
    val catalystsJson: String,
    val risksJson: String,
    val evidenceJson: String,
    val conflictsJson: String,
    val generatedAt: Long
)

@Entity(tableName = "data_health_records")
data class DataHealthEntity(
    @PrimaryKey val feedName: String,
    val providerName: String,
    val lastSyncTime: Long,
    val freshnessStatus: String,
    val reliability: String,
    val itemsTracked: Int,
    val failureCount: Int = 0,
    val latencyMs: Long = 42L,
    val isFailoverActive: Boolean = false,
    val lastError: String? = null
)

@Entity(
    tableName = "portfolio_alerts",
    indices = [Index("symbol"), Index("severity")]
)
data class PortfolioAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val title: String,
    val message: String,
    val severity: String, // CRITICAL, WARNING, OPPORTUNITY, INFO
    val category: String, // CONCENTRATION, VALUATION, TECHNICAL, THESIS, NEWS
    val triggerMetric: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionableSymbol: String? = null
)

@Entity(
    tableName = "watchlist",
    indices = [Index("symbol")]
)
data class WatchlistEntity(
    @PrimaryKey val symbol: String,
    val securityName: String,
    val assetClass: String,
    val exchange: String,
    val currency: String,
    val sector: String,
    val pinnedAt: Long = System.currentTimeMillis()
)
