package com.example.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.alerts.AlertFeedScreen
import com.example.presentation.dashboard.DashboardScreen
import com.example.presentation.datahealth.DataHealthScreen
import com.example.presentation.etf.ETFLookThroughScreen
import com.example.presentation.market.MarketOverviewDashboard
import com.example.presentation.news.MarketNewsFeed
import com.example.presentation.research.ResearchAgentSidebar
import com.example.presentation.research.ResearchScreen
import com.example.presentation.watchlist.WatchlistSideDrawer
import com.example.ui.theme.*

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isAnyOverlayOpen = uiState.isAlertFeedOpen || uiState.isWatchlistDrawerOpen || uiState.isAgentSidebarOpen

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = DarkNavySurface,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = uiState.selectedTab == 0 && !isAnyOverlayOpen,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Portfolio") },
                    label = { Text("Portfolio", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkNavyBackground,
                        selectedTextColor = PrimaryCyan,
                        indicatorColor = PrimaryCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_portfolio")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 1 && !isAnyOverlayOpen,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = "Markets") },
                    label = { Text("Markets", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkNavyBackground,
                        selectedTextColor = PrimaryCyan,
                        indicatorColor = PrimaryCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_markets")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 2 && !isAnyOverlayOpen,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.Newspaper, contentDescription = "News") },
                    label = { Text("News", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkNavyBackground,
                        selectedTextColor = PrimaryCyan,
                        indicatorColor = PrimaryCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_news")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 3 && !isAnyOverlayOpen,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.AutoGraph, contentDescription = "Research") },
                    label = { Text("Research", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkNavyBackground,
                        selectedTextColor = PrimaryCyan,
                        indicatorColor = PrimaryCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_research")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 4 && !isAnyOverlayOpen,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "ETFs") },
                    label = { Text("ETFs", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkNavyBackground,
                        selectedTextColor = CommitteeGold,
                        indicatorColor = CommitteeGold,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_etf")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 5 && !isAnyOverlayOpen,
                    onClick = { viewModel.selectTab(5) },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Health") },
                    label = { Text("Health", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkNavyBackground,
                        selectedTextColor = SuccessGreen,
                        indicatorColor = SuccessGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_health")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkNavyBackground)
                .padding(innerPadding)
        ) {
            when {
                uiState.isAlertFeedOpen -> {
                    BackHandler {
                        viewModel.closeAlertFeed()
                    }
                    AlertFeedScreen(
                        alerts = uiState.alerts,
                        unreadCount = uiState.unreadAlertCount,
                        onMarkAsRead = { id -> viewModel.markAlertAsRead(id) },
                        onMarkAllAsRead = { viewModel.markAllAlertsAsRead() },
                        onDismissAlert = { id -> viewModel.dismissAlert(id) },
                        onInvestigateSymbol = { sym -> viewModel.openResearchForSymbol(sym) },
                        onClose = { viewModel.closeAlertFeed() }
                    )
                }
                uiState.isWatchlistDrawerOpen -> {
                    BackHandler {
                        viewModel.closeWatchlistDrawer()
                    }
                    WatchlistSideDrawer(
                        pinnedItems = uiState.pinnedItems,
                        onUnpin = { symbol -> viewModel.unpinSymbol(symbol) },
                        onInvestigate = { symbol -> viewModel.openResearchForSymbol(symbol) },
                        onClose = { viewModel.closeWatchlistDrawer() }
                    )
                }
                uiState.isAgentSidebarOpen -> {
                    BackHandler {
                        viewModel.closeAgentSidebar()
                    }
                    ResearchAgentSidebar(
                        onClose = { viewModel.closeAgentSidebar() }
                    )
                }
                else -> {
                    when (uiState.selectedTab) {
                        0 -> DashboardScreen(
                            summary = uiState.summary,
                            holdings = uiState.holdings,
                            pinnedSymbols = uiState.pinnedSymbols,
                            portfolioViewMode = uiState.portfolioViewMode,
                            onSetViewMode = { mode -> viewModel.setPortfolioViewMode(mode) },
                            onHoldingClick = { symbol -> viewModel.openResearchForSymbol(symbol) },
                            onTogglePin = { holding -> viewModel.togglePinHolding(holding) },
                            unreadAlertCount = uiState.unreadAlertCount,
                            onOpenAlertFeed = { viewModel.openAlertFeed() },
                            onOpenWatchlist = { viewModel.openWatchlistDrawer() },
                            onOpenAgentSidebar = { viewModel.openAgentSidebar() }
                        )
                        1 -> MarketOverviewDashboard(
                            onIndexSelectedForResearch = { indexName ->
                                val targetSymbol = when {
                                    indexName.contains("NASDAQ") -> "QQQM"
                                    indexName.contains("S&P") -> "VOO"
                                    indexName.contains("NIFTY") -> "RELIANCE.NS"
                                    indexName.contains("Gold") -> "GLD"
                                    else -> "NVDA"
                                }
                                viewModel.openResearchForSymbol(targetSymbol)
                            }
                        )
                        2 -> MarketNewsFeed(
                            onInvestigateSymbol = { symbol -> viewModel.openResearchForSymbol(symbol) }
                        )
                        3 -> ResearchScreen(
                            currentReport = uiState.currentReport,
                            isResearching = uiState.isResearching,
                            agentProgress = uiState.agentProgress,
                            onTriggerResearch = { query -> viewModel.executeResearchPipeline(query) },
                            initialQuery = uiState.researchQuery
                        )
                        4 -> ETFLookThroughScreen(
                            onAnalyzeEtfPair = { _, _ -> }
                        )
                        5 -> DataHealthScreen(
                            healthRecords = uiState.healthRecords,
                            failoverEvents = uiState.failoverEvents,
                            isSimulatedFailureEnabled = uiState.isSimulatedFailureEnabled,
                            isRefreshing = uiState.isRefreshingFeeds,
                            onToggleSimulatedFailure = { enabled -> viewModel.toggleSimulatedFailure(enabled) },
                            onRefreshFeeds = { viewModel.refreshAllFeeds() }
                        )
                        else -> DashboardScreen(
                            summary = uiState.summary,
                            holdings = uiState.holdings,
                            pinnedSymbols = uiState.pinnedSymbols,
                            portfolioViewMode = uiState.portfolioViewMode,
                            onSetViewMode = { mode -> viewModel.setPortfolioViewMode(mode) },
                            onHoldingClick = { symbol -> viewModel.openResearchForSymbol(symbol) },
                            onTogglePin = { holding -> viewModel.togglePinHolding(holding) },
                            unreadAlertCount = uiState.unreadAlertCount,
                            onOpenAlertFeed = { viewModel.openAlertFeed() },
                            onOpenWatchlist = { viewModel.openWatchlistDrawer() },
                            onOpenAgentSidebar = { viewModel.openAgentSidebar() }
                        )
                    }
                }
            }
        }
    }
}
