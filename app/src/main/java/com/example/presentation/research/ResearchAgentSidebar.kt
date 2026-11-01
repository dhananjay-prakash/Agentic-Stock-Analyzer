package com.example.presentation.research

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
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
import com.example.ui.theme.*

data class AgentTelemetry(
    val id: String,
    val name: String,
    val category: String, // MARKET, FUNDAMENTAL, RISK, GOVERNANCE
    val status: String,   // SCANNING, AUDITING, READY
    val currentTask: String,
    val itemsProcessed: Int,
    val confidencePct: Int,
    val latencyMs: Long
)

@Composable
fun ResearchAgentSidebar(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }

    val agents = remember {
        listOf(
            AgentTelemetry("1", "Market Data Agent", "MARKET", "SCANNING", "Validating NASDAQ/NSE consolidated tape & bid-ask spread", 1420, 99, 42),
            AgentTelemetry("2", "Company Fundamental Agent", "FUNDAMENTAL", "AUDITING", "Cross-verifying SEC 10-Q & MCA filings for FCF & ROIC", 86, 98, 120),
            AgentTelemetry("3", "News & Event Wire Agent", "MARKET", "SCANNING", "Classifying regulatory filings & breaking news sentiment", 342, 94, 65),
            AgentTelemetry("4", "Technical Regime Agent", "MARKET", "SCANNING", "Tracking 50/200 EMA & 14-period RSI momentum boundaries", 820, 96, 38),
            AgentTelemetry("5", "Valuation DCF Agent", "FUNDAMENTAL", "READY", "Computing multi-stage intrinsic growth & peer multiples", 64, 95, 110),
            AgentTelemetry("6", "Risk & Exposure Agent", "RISK", "SCANNING", "Deconstructing ETF look-through & single-stock 15% limit", 215, 99, 54),
            AgentTelemetry("7", "Conflict Resolution Engine", "GOVERNANCE", "AUDITING", "Resolving valuation multiple vs momentum divergences", 48, 92, 85),
            AgentTelemetry("8", "Devil's Advocate Stress Agent", "GOVERNANCE", "SCANNING", "Formulating bear cases & hyperscaler capex risks", 32, 91, 140),
            AgentTelemetry("9", "Research Auditor Agent", "GOVERNANCE", "READY", "Enforcing strict 24-hour freshness & source provenance", 540, 100, 28),
            AgentTelemetry("10", "Investment Committee Agent", "GOVERNANCE", "READY", "Synthesizing institutional multi-factor conviction score", 28, 96, 95)
        )
    }

    val filteredAgents = remember(selectedCategory) {
        if (selectedCategory == "ALL") agents else agents.filter { it.category == selectedCategory }
    }

    // Infinite breathing glow animation for active scanning status
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .background(DarkNavyBackground)
            .padding(16.dp)
            .testTag("research_agent_sidebar")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Memory, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(
                        text = "AI AGENT ORCHESTRATOR",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "10 Autonomous Agents Live Scanning",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen
                    )
                }
            }

            IconButton(onClick = onClose, modifier = Modifier.testTag("close_agent_sidebar_btn")) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // System Diagnostic Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "ORCHESTRATION PIPELINE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = "Deterministic Multi-Agent", style = MaterialTheme.typography.bodyMedium, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .border(0.5.dp, SuccessGreen, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "CONCURRENCY: 3", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter categories
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val categories = listOf("ALL", "MARKET", "FUNDAMENTAL", "RISK", "GOVERNANCE")
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryCyan,
                        selectedLabelColor = DarkNavyBackground,
                        containerColor = DarkNavySurfaceVariant,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategory == cat,
                        borderColor = DarkNavyOutline
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Agent Telemetry List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredAgents, key = { it.id }) { agent ->
                AgentTelemetryCard(agent = agent, pulseAlpha = pulseAlpha)
            }
        }
    }
}

@Composable
private fun AgentTelemetryCard(agent: AgentTelemetry, pulseAlpha: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("agent_card_${agent.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Pulsing Status Dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (agent.status) {
                                    "SCANNING" -> PrimaryCyan.copy(alpha = pulseAlpha)
                                    "AUDITING" -> CommitteeGold
                                    else -> SuccessGreen
                                }
                            )
                    )
                    Text(
                        text = agent.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(DarkNavySurfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = agent.status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (agent.status) {
                            "SCANNING" -> PrimaryCyan
                            "AUDITING" -> CommitteeGold
                            else -> SuccessGreen
                        }
                    )
                }
            }

            Text(
                text = agent.currentTask,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${agent.itemsProcessed} processed",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = "${agent.latencyMs}ms latency",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = "${agent.confidencePct}% Conviction",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
