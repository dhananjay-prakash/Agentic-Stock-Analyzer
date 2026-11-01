package com.example.data.repository

import com.example.data.controltower.DataControlTower
import com.example.data.local.db.OmniVestDatabase
import com.example.domain.model.AssetClass
import com.example.domain.model.Currency
import com.example.domain.model.Exchange
import com.example.domain.model.Security
import com.example.domain.model.SecurityQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SecurityRepository(
    private val database: OmniVestDatabase,
    private val dataTower: DataControlTower
) {

    fun getAllSecurities(): Flow<List<Security>> {
        return database.securityDao().getAllSecurities().map { entities ->
            entities.map { entity ->
                Security(
                    symbol = entity.symbol,
                    name = entity.name,
                    assetClass = runCatching { AssetClass.valueOf(entity.assetClass) }.getOrDefault(AssetClass.EQUITY_US),
                    exchange = runCatching { Exchange.valueOf(entity.exchange) }.getOrDefault(Exchange.NASDAQ),
                    currency = runCatching { Currency.valueOf(entity.currency) }.getOrDefault(Currency.USD),
                    sector = entity.sector,
                    industry = entity.industry,
                    isTracked = entity.isTracked
                )
            }
        }
    }

    fun searchSecurities(query: String): Flow<List<Security>> {
        return database.securityDao().searchSecurities(query).map { entities ->
            entities.map { entity ->
                Security(
                    symbol = entity.symbol,
                    name = entity.name,
                    assetClass = runCatching { AssetClass.valueOf(entity.assetClass) }.getOrDefault(AssetClass.EQUITY_US),
                    exchange = runCatching { Exchange.valueOf(entity.exchange) }.getOrDefault(Exchange.NASDAQ),
                    currency = runCatching { Currency.valueOf(entity.currency) }.getOrDefault(Currency.USD),
                    sector = entity.sector,
                    industry = entity.industry,
                    isTracked = entity.isTracked
                )
            }
        }
    }

    suspend fun getSecurity(symbol: String): Security? {
        val entity = database.securityDao().getSecurityBySymbol(symbol) ?: return null
        return Security(
            symbol = entity.symbol,
            name = entity.name,
            assetClass = runCatching { AssetClass.valueOf(entity.assetClass) }.getOrDefault(AssetClass.EQUITY_US),
            exchange = runCatching { Exchange.valueOf(entity.exchange) }.getOrDefault(Exchange.NASDAQ),
            currency = runCatching { Currency.valueOf(entity.currency) }.getOrDefault(Currency.USD),
            sector = entity.sector,
            industry = entity.industry,
            isTracked = entity.isTracked
        )
    }

    suspend fun getVerifiedQuote(symbol: String): SecurityQuote? {
        return dataTower.getVerifiedQuote(symbol)
    }
}
