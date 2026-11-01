package com.example.domain.model

data class PortfolioHolding(
    val id: Long = 0,
    val symbol: String,
    val securityName: String,
    val assetClass: AssetClass,
    val exchange: Exchange,
    val currency: Currency,
    val sharesQuantity: Double,
    val averageCost: Double,
    val currentPrice: Double,
    val totalCostBasis: Double = sharesQuantity * averageCost,
    val currentMarketValue: Double = sharesQuantity * currentPrice,
    val unrealizedGainLoss: Double = currentMarketValue - totalCostBasis,
    val unrealizedGainLossPct: Double = if (totalCostBasis > 0) (unrealizedGainLoss / totalCostBasis) * 100 else 0.0,
    val dayChangePct: Double = 0.0,
    val sector: String = ""
)

data class ETFConstituent(
    val etfSymbol: String,
    val constituentSymbol: String,
    val constituentName: String,
    val weightPct: Double,
    val sector: String
)

data class EffectiveExposure(
    val symbol: String,
    val name: String,
    val directValue: Double,
    val indirectValueFromETFs: Double,
    val totalEffectiveValue: Double,
    val effectiveWeightPct: Double,
    val contributingHoldings: List<String>
)

data class SectorExposure(
    val sector: String,
    val directValue: Double,
    val indirectValue: Double,
    val totalValue: Double,
    val weightPct: Double
)

data class RegionExposure(
    val region: String,
    val totalValue: Double,
    val weightPct: Double
)

data class PortfolioSummary(
    val totalValueUsd: Double,
    val totalCostBasisUsd: Double,
    val totalGainLossUsd: Double,
    val totalGainLossPct: Double,
    val todayChangeUsd: Double,
    val todayChangePct: Double,
    val usEquityPct: Double,
    val indiaEquityPct: Double,
    val etfPct: Double,
    val cashCommodityPct: Double,
    val sectorBreakdown: List<SectorExposure>,
    val topEffectiveExposures: List<EffectiveExposure>,
    val concentrationWarning: String? = null
)

data class ETFOverlapComparison(
    val etfA: String,
    val etfB: String,
    val overlapWeightPct: Double,
    val commonHoldingsCount: Int,
    val totalHoldingsA: Int,
    val totalHoldingsB: Int,
    val sharedTopHoldings: List<String>
)
