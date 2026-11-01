package com.example

import android.app.Application
import com.example.data.controltower.DataControlTower
import com.example.data.local.db.OmniVestDatabase
import com.example.data.provider.failover.ProviderFailoverRouter
import com.example.data.provider.remote.*
import com.example.data.repository.AlertRepository
import com.example.data.repository.PortfolioRepository
import com.example.data.repository.ResearchRepository
import com.example.data.repository.SecurityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class OmniVestApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { OmniVestDatabase.getDatabase(this, applicationScope) }

    val primaryMarketProvider by lazy { PrimaryExchangeMarketDataProvider() }
    val secondaryMarketProvider by lazy { SecondaryInstitutionalMarketDataProvider() }
    val newsProvider by lazy { InstitutionalNewsProvider() }
    val fundamentalProvider by lazy { FilingFundamentalProvider() }
    val macroProvider by lazy { CentralBankMacroProvider() }

    val failoverRouter by lazy {
        ProviderFailoverRouter(
            primaryMarketProvider = primaryMarketProvider,
            secondaryMarketProvider = secondaryMarketProvider,
            newsProvider = newsProvider,
            fundamentalProvider = fundamentalProvider,
            macroProvider = macroProvider
        )
    }

    val dataTower by lazy { DataControlTower(database, failoverRouter) }
    val securityRepository by lazy { SecurityRepository(database, dataTower) }
    val portfolioRepository by lazy { PortfolioRepository(database) }
    val researchRepository by lazy { ResearchRepository(database) }
    val alertRepository by lazy { AlertRepository(database) }
    val watchlistRepository by lazy { com.example.data.repository.WatchlistRepository(database) }

    override fun onCreate() {
        super.onCreate()
    }
}
