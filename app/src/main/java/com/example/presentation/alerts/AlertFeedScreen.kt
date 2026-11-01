package com.example.presentation.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PortfolioAlertEntity
import com.example.ui.theme.*

@Composable
fun AlertFeedScreen(
    alerts: List<PortfolioAlertEntity>,
    unreadCount: Int,
    onMarkAsRead: (Long) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDismissAlert: (Long) -> Unit,
    onInvestigateSymbol: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }

    val filteredAlerts = remember(alerts, selectedCategory) {
        if (selectedCategory == "ALL") {
            alerts
        } else if (selectedCategory == "CRITICAL") {
            alerts.filter { it.severity == "CRITICAL" }
        } else {
            alerts.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DangerRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alerts",
                        tint = DangerRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "PORTFOLIO ALERTS FEED",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DangerRed)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "$unreadCount NEW",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = "Real-time risk thresholds and valuation breaches",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("close_alert_feed_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
        }

        // Action bar: Mark All Read & Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val categories = listOf("ALL", "CRITICAL", "CONCENTRATION", "VALUATION", "TECHNICAL", "THESIS")
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (cat == "CRITICAL") DangerRed else PrimaryCyan,
                            selectedLabelColor = DarkNavyBackground,
                            containerColor = DarkNavySurfaceVariant,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == cat,
                            borderColor = DarkNavyOutline
                        ),
                        modifier = Modifier.testTag("filter_alert_$cat")
                    )
                }
            }

            if (unreadCount > 0) {
                TextButton(
                    onClick = onMarkAllAsRead,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("mark_all_read_btn")
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Read All", fontSize = 11.sp, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Alerts Feed List
        if (filteredAlerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "All Thresholds Normal",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "No active concentration or valuation threshold breaches.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredAlerts, key = { it.id }) { alert ->
                    AlertFeedCard(
                        alert = alert,
                        onMarkAsRead = { onMarkAsRead(alert.id) },
                        onDismiss = { onDismissAlert(alert.id) },
                        onInvestigate = {
                            alert.actionableSymbol?.let { sym -> onInvestigateSymbol(sym) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertFeedCard(
    alert: PortfolioAlertEntity,
    onMarkAsRead: () -> Unit,
    onDismiss: () -> Unit,
    onInvestigate: () -> Unit
) {
    val (severityColor, severityLabel) = when (alert.severity) {
        "CRITICAL" -> DangerRed to "CRITICAL BREACH"
        "WARNING" -> WarningAmber to "RISK WARNING"
        "OPPORTUNITY" -> PrimaryCyan to "OPPORTUNITY"
        else -> SuccessGreen to "VERIFIED DISCLOSURE"
    }

    val timeAgo = remember(alert.timestamp) {
        val diffMs = System.currentTimeMillis() - alert.timestamp
        val mins = diffMs / (1000 * 60)
        when {
            mins < 60 -> "${mins}m ago"
            mins < 1440 -> "${mins / 60}h ago"
            else -> "${mins / 1440}d ago"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alert_card_${alert.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isRead) DarkNavySurface else DarkNavySurfaceVariant
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (!alert.isRead) severityColor.copy(alpha = 0.6f) else DarkNavyOutline
            )
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Severity Color Strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(severityColor)
            )

            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header Row (Symbol, Severity badge, Time ago)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkNavyOutline)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = alert.symbol,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(severityColor.copy(alpha = 0.15f))
                                .border(0.5.dp, severityColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = severityLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = severityColor
                            )
                        }
                    }

                    Text(
                        text = timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                // Alert Title & Message
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                // Trigger Metric Breach Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkNavyBackground)
                        .border(0.5.dp, severityColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRIGGER THRESHOLD",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = alert.triggerMetric,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = severityColor
                        )
                    }
                }

                // Actionable Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (alert.actionableSymbol != null) {
                        Button(
                            onClick = onInvestigate,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryCyan.copy(alpha = 0.15f),
                                contentColor = PrimaryCyan
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(PrimaryCyan)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("investigate_alert_${alert.id}")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Investigate ${alert.actionableSymbol}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (!alert.isRead) {
                            IconButton(
                                onClick = onMarkAsRead,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("read_alert_${alert.id}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Mark as read", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("dismiss_alert_${alert.id}")
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
