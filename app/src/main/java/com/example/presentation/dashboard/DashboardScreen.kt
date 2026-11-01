package com.example.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Currency
import com.example.domain.model.EffectiveExposure
import com.example.domain.model.PortfolioHolding
import com.example.domain.model.PortfolioSummary
import com.example.presentation.components.FreshnessBadge
import com.example.presentation.components.MetricStatBox
import com.example.presentation.portfolio.PortfolioHeatmap
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    summary: PortfolioSummary?,
    holdings: List<PortfolioHolding>,
    pinnedSymbols: List<String>,
    portfolioViewMode: Int,
    onSetViewMode: (Int) -> Unit,
    onHoldingClick: (String) -> Unit,
    onTogglePin: (PortfolioHolding) -> Unit,
    unreadAlertCount: Int = 0,
    onOpenAlertFeed: () -> Unit = {},
    onOpenWatchlist: () -> Unit = {},
    onOpenAgentSidebar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (portfolioViewMode == 1) {
        // Render Full Sector Treemap Heatmap
        Column(modifier = modifier.fillMaxSize().background(DarkNavyBackground)) {
            // View Mode Switcher Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = false,
                        onClick = { onSetViewMode(0) },
                        label = { Text("Positions List", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = DarkNavySurfaceVariant,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = DarkNavyOutline)
                    )
                    FilterChip(
                        selected = true,
                        onClick = { },
                        label = { Text("Treemap Heatmap", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCyan,
                            selectedLabelColor = DarkNavyBackground
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onOpenWatchlist, modifier = Modifier.testTag("dashboard_watchlist_btn_heatmap")) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Watchlist", tint = PrimaryCyan)
                    }
                    IconButton(onClick = onOpenAlertFeed, modifier = Modifier.testTag("dashboard_alerts_btn_heatmap")) {
                        BadgedBox(
                            badge = {
                                if (unreadAlertCount > 0) {
                                    Badge(containerColor = DangerRed, contentColor = Color.White) {
                                        Text("$unreadAlertCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = PrimaryCyan)
                        }
                    }
                }
            }

            PortfolioHeatmap(
                holdings = holdings,
                pinnedSymbols = pinnedSymbols,
                onHoldingSelected = onHoldingClick,
                onTogglePin = onTogglePin
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Institutional Identification
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OMNIVEST RESEARCH",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Portfolio Intelligence",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                }

                // Action Bar: AI Agent Orchestrator, Watchlist Drawer, Alerts Feed, Live Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onOpenAgentSidebar,
                        modifier = Modifier.testTag("dashboard_agent_sidebar_button")
                    ) {
                        Icon(Icons.Default.Memory, contentDescription = "AI Agents", tint = PrimaryCyan)
                    }

                    IconButton(
                        onClick = onOpenWatchlist,
                        modifier = Modifier.testTag("dashboard_watchlist_button")
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Watchlist", tint = PrimaryCyan)
                    }

                    IconButton(
                        onClick = onOpenAlertFeed,
                        modifier = Modifier.testTag("dashboard_notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadAlertCount > 0) {
                                    Badge(containerColor = DangerRed, contentColor = Color.White) {
                                        Text("$unreadAlertCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = PrimaryCyan)
                        }
                    }

                    FreshnessBadge(
                        state = com.example.domain.model.FreshnessState.LIVE,
                        modifier = Modifier.testTag("portfolio_live_badge")
                    )
                }
            }
        }

        // Critical Alerts Banner (if any unread alerts)
        if (unreadAlertCount > 0) {
            item {
                Card(
                    onClick = onOpenAlertFeed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_alerts_banner"),
                    colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.12f)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DangerRed.copy(alpha = 0.5f))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = DangerRed)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CRITICAL PORTFOLIO ALERTS ($unreadAlertCount UNREAD)",
                                style = MaterialTheme.typography.labelSmall,
                                color = DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Threshold breaches detected in concentration & valuation zones. Tap to inspect feed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DangerRed)
                    }
                }
            }
        }

        // 2. High-Level Metrics Summary Card
        item {
            if (summary != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("portfolio_summary_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AGGREGATE NET ASSET VALUE",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "$${"%,.2f".format(summary.totalValueUsd)}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            val dayColor = if (summary.todayChangeUsd >= 0) SuccessGreen else DangerRed
                            val dayPrefix = if (summary.todayChangeUsd >= 0) "+" else ""
                            Text(
                                text = "$dayPrefix$${"%,.2f".format(summary.todayChangeUsd)} ($dayPrefix${"%.2f".format(summary.todayChangePct)}%) today",
                                style = MaterialTheme.typography.bodyMedium,
                                color = dayColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val gainColor = if (summary.totalGainLossUsd >= 0) SuccessGreen else DangerRed
                            val gainPrefix = if (summary.totalGainLossUsd >= 0) "+" else ""
                            MetricStatBox(
                                title = "Unrealized P&L",
                                value = "$gainPrefix$${"%,.2f".format(summary.totalGainLossUsd)}",
                                subtitle = "$gainPrefix${"%.2f".format(summary.totalGainLossPct)}% Return",
                                valueColor = gainColor,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                title = "Total Cost Basis",
                                value = "$${"%,.2f".format(summary.totalCostBasisUsd)}",
                                subtitle = "${holdings.size} Active Positions",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Asset Allocation Breakdown Pills
        item {
            if (summary != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ASSET ALLOCATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AllocationPill(label = "US Equity", pct = summary.usEquityPct, color = PrimaryCyan, modifier = Modifier.weight(1f))
                        AllocationPill(label = "India Equity", pct = summary.indiaEquityPct, color = CommitteeGold, modifier = Modifier.weight(1f))
                        AllocationPill(label = "ETFs", pct = summary.etfPct, color = SuccessGreen, modifier = Modifier.weight(1f))
                        AllocationPill(label = "Gold", pct = summary.cashCommodityPct, color = WarningAmber, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 4. Concentration Warning Banner
        item {
            summary?.concentrationWarning?.let { warningText ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(WarningAmber.copy(alpha = 0.12f))
                        .border(1.dp, WarningAmber.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = WarningAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = warningText,
                        style = MaterialTheme.typography.bodySmall,
                        color = WarningAmber
                    )
                }
            }
        }

        // 5. ETF Look-Through Effective Exposure Card
        item {
            Text(
                text = "TOP EFFECTIVE EXPOSURES (DIRECT + ETF LOOK-THROUGH)",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        item {
            summary?.topEffectiveExposures?.let { exposures ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkNavySurface)
                        .border(1.dp, DarkNavyOutline, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    exposures.forEach { exposure ->
                        EffectiveExposureRow(exposure = exposure)
                    }
                }
            }
        }

        // 6. Direct Holdings List Header with Treemap Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "PORTFOLIO HOLDINGS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "(${holdings.size} Securities)",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan
                    )
                }

                // Switch to Treemap View
                Button(
                    onClick = { onSetViewMode(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkNavySurfaceVariant, contentColor = PrimaryCyan),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("switch_to_heatmap_btn")
                ) {
                    Text("Sector Heatmap", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Holdings Cards with Quick Pin
        items(holdings) { holding ->
            val isPinned = pinnedSymbols.contains(holding.symbol)
            HoldingCard(
                holding = holding,
                isPinned = isPinned,
                onTogglePin = { onTogglePin(holding) },
                onClick = { onHoldingClick(holding.symbol) }
            )
        }
    }
}

@Composable
private fun AllocationPill(
    label: String,
    pct: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkNavySurface)
            .border(0.5.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
        Text(text = "${"%.1f".format(pct)}%", style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EffectiveExposureRow(exposure: EffectiveExposure) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = exposure.symbol, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(text = exposure.name, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "$${"%,.0f".format(exposure.totalEffectiveValue)}", style = MaterialTheme.typography.titleMedium, color = PrimaryCyan, fontWeight = FontWeight.SemiBold)
                Text(text = "${"%.1f".format(exposure.effectiveWeightPct)}% of total", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
        if (exposure.contributingHoldings.isNotEmpty()) {
            Text(
                text = "Sources: " + exposure.contributingHoldings.joinToString(" + "),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun HoldingCard(
    holding: PortfolioHolding,
    isPinned: Boolean,
    onTogglePin: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("holding_card_${holding.symbol}"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = holding.symbol, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(DarkNavySurfaceVariant)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(text = holding.assetClass.displayName, style = MaterialTheme.typography.labelSmall, color = PrimaryCyan, fontSize = 9.sp)
                    }
                }
                Text(text = holding.securityName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                val currSym = if (holding.currency == Currency.INR) "₹" else "$"
                Text(
                    text = "${holding.sharesQuantity} shares @ avg $currSym${"%,.2f".format(holding.averageCost)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    val currSym = if (holding.currency == Currency.INR) "₹" else "$"
                    Text(
                        text = "$currSym${"%,.2f".format(holding.currentMarketValue)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    val gainColor = if (holding.unrealizedGainLoss >= 0) SuccessGreen else DangerRed
                    val gainPrefix = if (holding.unrealizedGainLoss >= 0) "+" else ""
                    Text(
                        text = "$gainPrefix$currSym${"%,.2f".format(holding.unrealizedGainLoss)} ($gainPrefix${"%.2f".format(holding.unrealizedGainLossPct)}%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = gainColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Quick Watchlist Pin Button
                IconButton(
                    onClick = onTogglePin,
                    modifier = Modifier.testTag("pin_holding_${holding.symbol}")
                ) {
                    Icon(
                        imageVector = if (isPinned) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Pin to Watchlist",
                        tint = if (isPinned) PrimaryCyan else TextMuted
                    )
                }
            }
        }
    }
}
