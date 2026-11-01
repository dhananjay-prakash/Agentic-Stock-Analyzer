package com.example.presentation.datahealth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
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
import com.example.data.local.entity.DataHealthEntity
import com.example.data.provider.failover.FailoverRecord
import com.example.domain.model.FreshnessState
import com.example.presentation.components.FreshnessBadge
import com.example.ui.theme.*

@Composable
fun DataHealthScreen(
    healthRecords: List<DataHealthEntity>,
    failoverEvents: List<FailoverRecord>,
    isSimulatedFailureEnabled: Boolean,
    isRefreshing: Boolean,
    onToggleSimulatedFailure: (Boolean) -> Unit,
    onRefreshFeeds: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allGreen = healthRecords.all { it.failureCount == 0 && it.freshnessStatus != FreshnessState.CURRENT_DATA_UNAVAILABLE.name }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DATA CONTROL TOWER",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Feed Health & Failover",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                }
                IconButton(
                    onClick = onRefreshFeeds,
                    enabled = !isRefreshing,
                    modifier = Modifier.testTag("refresh_feeds_button")
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = PrimaryCyan)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Feeds", tint = PrimaryCyan)
                    }
                }
            }
        }

        // Overall System Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("system_health_card"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (allGreen) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = "Status",
                        tint = if (allGreen) SuccessGreen else WarningAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = if (allGreen) "SYSTEM HEALTH: GREEN" else "SYSTEM HEALTH: YELLOW (FAILOVER ACTIVE)",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (allGreen) SuccessGreen else WarningAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (allGreen) "All primary financial feeds verified within required freshness boundaries." else "Provider failover engaged. Using validated secondary liquidity feed.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Layer 15 Live Simulation Test Controls
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("failover_simulation_card"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "PROVIDER FAILOVER TESTING (LAYER 15 & LAYER 53)",
                        style = MaterialTheme.typography.labelSmall,
                        color = CommitteeGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Simulate primary exchange tape downtime (HTTP 503 / Network Timeout) to verify that the Data Control Tower automatically switches to the secondary institutional feed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSimulatedFailureEnabled) "Primary Tape: SIMULATED DOWN (Failover Active)" else "Primary Tape: NORMAL OPERATION",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSimulatedFailureEnabled) WarningAmber else SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = isSimulatedFailureEnabled,
                            onCheckedChange = onToggleSimulatedFailure,
                            modifier = Modifier.testTag("failover_toggle_switch")
                        )
                    }
                }
            }
        }

        // Failover Audit Log (Layer 53: Failure Transparency)
        if (failoverEvents.isNotEmpty()) {
            item {
                Text(
                    text = "TRANSPARENT FAILOVER AUDIT LOG",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber
                )
            }

            items(failoverEvents.takeLast(4).reversed()) { ev ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WarningAmber.copy(alpha = 0.4f)))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "SYMBOL: ${ev.symbol}", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (ev.isSecondarySuccess) "SECONDARY: SUCCESS ✓" else "FAILED ✗",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (ev.isSecondarySuccess) SuccessGreen else DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = "Primary [${ev.primaryProvider}]: FAILED (${ev.failureReason})", style = MaterialTheme.typography.bodySmall, color = DangerRed)
                        Text(text = "Engaged Secondary: ${ev.secondaryProvider}", style = MaterialTheme.typography.bodySmall, color = PrimaryCyan)
                    }
                }
            }
        }

        item {
            Text(
                text = "PRIMARY AND SECONDARY FEED REGISTRY",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        items(healthRecords) { record ->
            FeedHealthCard(record = record)
        }
    }
}

@Composable
private fun FeedHealthCard(record: DataHealthEntity) {
    val freshnessState = runCatching { FreshnessState.valueOf(record.freshnessStatus) }.getOrDefault(FreshnessState.LIVE)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("feed_card_${record.feedName.replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (record.isFailoverActive) WarningAmber else DarkNavyOutline)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.feedName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                FreshnessBadge(state = freshnessState)
            }
            Text(
                text = "Active Provider: ${record.providerName}",
                style = MaterialTheme.typography.bodySmall,
                color = if (record.isFailoverActive) WarningAmber else TextSecondary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Latency: ${record.latencyMs} ms",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan
                )
                Text(
                    text = "Reliability: ${record.reliability}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = if (record.isFailoverActive) "FAILOVER ENGAGED" else "PRIMARY ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (record.isFailoverActive) WarningAmber else SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            if (record.lastError != null) {
                Text(
                    text = "Audit Note: ${record.lastError}",
                    style = MaterialTheme.typography.labelSmall,
                    color = DangerRed
                )
            }
        }
    }
}
