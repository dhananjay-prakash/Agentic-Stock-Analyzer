package com.example.data.repository

import com.example.data.local.db.OmniVestDatabase
import com.example.data.local.entity.PortfolioHoldingEntity
import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PortfolioRepository(
    private val database: OmniVestDatabase
) {

    /**
     * Observes real-time portfolio holdings combined with live quotes.
     */
    fun observePortfolioHoldings(): Flow<List<PortfolioHolding>> {
        val holdingsFlow = database.portfolioDao().getAllHoldings()
        val allQuotesFlow = database.quoteDao().getQuotesForSymbols(listOf("QQQM", "VOO", "NVDA", "MSFT", "RELIANCE.NS", "TCS.NS", "GOOGL", "AMZN", "GLD"))

        return combine(holdingsFlow, allQuotesFlow) { entities, quotes ->
            val quoteMap = quotes.associateBy { it.symbol }

            entities.map { entity ->
                val quote = quoteMap[entity.symbol]
                val currentPrice = quote?.price ?: entity.averageCost
                val dayChangePct = quote?.changePercent ?: 0.0

                PortfolioHolding(
                    id = entity.id,
                    symbol = entity.symbol,
                    securityName = entity.securityName,
                    assetClass = runCatching { AssetClass.valueOf(entity.assetClass) }.getOrDefault(AssetClass.EQUITY_US),
                    exchange = runCatching { Exchange.valueOf(entity.exchange) }.getOrDefault(Exchange.NASDAQ),
                    currency = runCatching { Currency.valueOf(entity.currency) }.getOrDefault(Currency.USD),
                    sharesQuantity = entity.sharesQuantity,
                    averageCost = entity.averageCost,
                    currentPrice = currentPrice,
                    dayChangePct = dayChangePct,
                    sector = entity.sector
                )
            }
        }
    }

    /**
     * Computes the aggregated institutional PortfolioSummary with Look-Through exposure.
     */
    fun observePortfolioSummary(): Flow<PortfolioSummary> {
        return observePortfolioHoldings().combine(database.etfDao().getConstituentsForETF("QQQM")) { holdings, qqqmConstituents ->
            calculateSummary(holdings, qqqmConstituents)
        }
    }

    private suspend fun calculateSummary(
        holdings: List<PortfolioHolding>,
        qqqmList: List<com.example.data.local.entity.ETFConstituentEntity>
    ): PortfolioSummary {
        // Exchange rate standard for Indian equities (INR to USD approx 1 USD = 84 INR)
        val inrToUsd = 1.0 / 84.0

        var totalValueUsd = 0.0
        var totalCostUsd = 0.0
        var todayChangeUsd = 0.0

        var usEquityVal = 0.0
        var indiaEquityVal = 0.0
        var etfVal = 0.0
        var commodityVal = 0.0

        val sectorValues = mutableMapOf<String, Double>()
        val directExposureMap = mutableMapOf<String, Double>()
        val indirectExposureMap = mutableMapOf<String, Double>()
        val contributorsMap = mutableMapOf<String, MutableList<String>>()

        for (holding in holdings) {
            val multiplier = if (holding.currency == Currency.INR) inrToUsd else 1.0
            val holdingValUsd = holding.currentMarketValue * multiplier
            val holdingCostUsd = holding.totalCostBasis * multiplier
            val holdingDayChangeUsd = (holdingValUsd * (holding.dayChangePct / 100.0))

            totalValueUsd += holdingValUsd
            totalCostUsd += holdingCostUsd
            todayChangeUsd += holdingDayChangeUsd

            when (holding.assetClass) {
                AssetClass.EQUITY_US -> usEquityVal += holdingValUsd
                AssetClass.EQUITY_INDIA -> indiaEquityVal += holdingValUsd
                AssetClass.ETF -> etfVal += holdingValUsd
                AssetClass.COMMODITY -> commodityVal += holdingValUsd
                else -> {}
            }

            // Direct sector
            val sec = if (holding.sector.isNotBlank()) holding.sector else "Other"
            sectorValues[sec] = (sectorValues[sec] ?: 0.0) + holdingValUsd

            // Direct stock exposures
            if (holding.assetClass != AssetClass.ETF) {
                directExposureMap[holding.symbol] = (directExposureMap[holding.symbol] ?: 0.0) + holdingValUsd
                contributorsMap.getOrPut(holding.symbol) { mutableListOf() }.add("Direct Holding (${holding.symbol})")
            }
        }

        // Deconstruct ETFs for Look-Through
        for (holding in holdings) {
            if (holding.assetClass == AssetClass.ETF) {
                val constituents = database.etfDao().getConstituentsSnapshot(holding.symbol)
                val etfValUsd = holding.currentMarketValue

                for (const in constituents) {
                    val indirectVal = etfValUsd * (const.weightPct / 100.0)
                    indirectExposureMap[const.constituentSymbol] = (indirectExposureMap[const.constituentSymbol] ?: 0.0) + indirectVal
                    contributorsMap.getOrPut(const.constituentSymbol) { mutableListOf() }.add("${holding.symbol} (${const.weightPct}%)")

                    // Also attribute indirect sector weight
                    sectorValues[const.sector] = (sectorValues[const.sector] ?: 0.0) + (indirectVal * 0.5) // partial factor adjustment
                }
            }
        }

        val totalGainLossUsd = totalValueUsd - totalCostUsd
        val totalGainLossPct = if (totalCostUsd > 0) (totalGainLossUsd / totalCostUsd) * 100 else 0.0
        val todayChangePct = if (totalValueUsd > 0) (todayChangeUsd / totalValueUsd) * 100 else 0.0

        // Compute top effective exposures (direct + indirect look-through)
        val allSymbols = (directExposureMap.keys + indirectExposureMap.keys).distinct()
        val effectiveExposures = allSymbols.map { sym ->
            val direct = directExposureMap[sym] ?: 0.0
            val indirect = indirectExposureMap[sym] ?: 0.0
            val total = direct + indirect
            val weight = if (totalValueUsd > 0) (total / totalValueUsd) * 100 else 0.0
            val name = when (sym) {
                "NVDA" -> "NVIDIA Corporation"
                "MSFT" -> "Microsoft Corporation"
                "AAPL" -> "Apple Inc."
                "AMZN" -> "Amazon.com Inc."
                "GOOGL" -> "Alphabet Inc."
                "RELIANCE.NS" -> "Reliance Industries"
                else -> sym
            }
            EffectiveExposure(
                symbol = sym,
                name = name,
                directValue = direct,
                indirectValueFromETFs = indirect,
                totalEffectiveValue = total,
                effectiveWeightPct = weight,
                contributingHoldings = contributorsMap[sym] ?: emptyList()
            )
        }.sortedByDescending { it.totalEffectiveValue }

        val sectorBreakdown = sectorValues.map { (sec, value) ->
            val weight = if (totalValueUsd > 0) (value / totalValueUsd) * 100 else 0.0
            SectorExposure(sec, value, 0.0, value, weight)
        }.sortedByDescending { it.weightPct }

        val topExposure = effectiveExposures.firstOrNull()
        val concentrationWarning = if (topExposure != null && topExposure.effectiveWeightPct > 25.0) {
            "High concentration: ${topExposure.symbol} represents ${"%.1f".format(topExposure.effectiveWeightPct)}% of total effective portfolio value (Direct + ETF Look-Through)."
        } else null

        return PortfolioSummary(
            totalValueUsd = totalValueUsd,
            totalCostBasisUsd = totalCostUsd,
            totalGainLossUsd = totalGainLossUsd,
            totalGainLossPct = totalGainLossPct,
            todayChangeUsd = todayChangeUsd,
            todayChangePct = todayChangePct,
            usEquityPct = if (totalValueUsd > 0) (usEquityVal / totalValueUsd) * 100 else 0.0,
            indiaEquityPct = if (totalValueUsd > 0) (indiaEquityVal / totalValueUsd) * 100 else 0.0,
            etfPct = if (totalValueUsd > 0) (etfVal / totalValueUsd) * 100 else 0.0,
            cashCommodityPct = if (totalValueUsd > 0) (commodityVal / totalValueUsd) * 100 else 0.0,
            sectorBreakdown = sectorBreakdown,
            topEffectiveExposures = effectiveExposures.take(8),
            concentrationWarning = concentrationWarning
        )
    }

    suspend fun addHolding(holding: PortfolioHoldingEntity) {
        database.portfolioDao().insertHolding(holding)
    }

    suspend fun removeHolding(symbol: String) {
        database.portfolioDao().deleteHoldingBySymbol(symbol)
    }
}
