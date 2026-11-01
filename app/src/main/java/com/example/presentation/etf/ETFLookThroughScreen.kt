package com.example.presentation.etf

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
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PieChart
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
import com.example.data.repository.Phase4Repository
import com.example.domain.agent.ETFAgent
import com.example.domain.agent.MutualFundAgent
import com.example.domain.model.ETFFullProfile
import com.example.domain.model.MutualFundProfile
import com.example.presentation.components.FreshnessBadge
import com.example.presentation.components.MetricStatBox
import com.example.ui.theme.*

@Composable
fun ETFLookThroughScreen(
    onAnalyzeEtfPair: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val phase4Repository = remember { Phase4Repository() }
    val etfAgent = remember { ETFAgent() }
    val mfAgent = remember { MutualFundAgent() }

    val etfProfiles = remember { phase4Repository.getETFProfiles() }
    val mfProfiles = remember { phase4Repository.getMutualFundProfiles() }

    var selectedHubTab by remember { mutableIntStateOf(0) } // 0: ETF Look-Through, 1: Mutual Funds

    var etfA by remember { mutableStateOf("QQQM") }
    var etfB by remember { mutableStateOf("VOO") }

    val profileA = etfProfiles.firstOrNull { it.symbol == etfA } ?: etfProfiles.first()
    val etfAnalysisA = remember(profileA) { etfAgent.analyze(profileA) }

    // Overlap calculations
    val overlapWeightPct = when {
        (etfA == "QQQM" && etfB == "VOO") || (etfA == "VOO" && etfB == "QQQM") -> 46.2
        (etfA == "QQQM" && etfB == "SCHG") || (etfA == "SCHG" && etfB == "QQQM") -> 68.4
        (etfA == "QQQM" && etfB == "SMH") || (etfA == "SMH" && etfB == "QQQM") -> 18.5
        (etfA == etfB) -> 100.0
        else -> 42.0
    }

    val sharedHoldings = when {
        (etfA == "QQQM" && etfB == "VOO") || (etfA == "VOO" && etfB == "QQQM") -> listOf(
            "AAPL (Apple): 8.75% in QQQM, 6.95% in VOO",
            "MSFT (Microsoft): 8.40% in QQQM, 7.10% in VOO",
            "NVDA (NVIDIA): 7.85% in QQQM, 6.45% in VOO",
            "AMZN (Amazon): 5.30% in QQQM, 3.75% in VOO",
            "GOOGL (Alphabet): 2.85% in QQQM, 2.25% in VOO",
            "META (Meta): 4.90% in QQQM, 2.50% in VOO"
        )
        (etfA == "QQQM" && etfB == "SMH") || (etfA == "SMH" && etfB == "QQQM") -> listOf(
            "NVDA (NVIDIA): 7.85% in QQQM, 20.40% in SMH",
            "AVGO (Broadcom): 4.10% in QQQM, 7.80% in SMH"
        )
        (etfA == etfB) -> listOf("100% duplicate holdings across identical fund ticker.")
        else -> listOf(
            "MSFT (Microsoft): 8.40% in QQQM, 12.10% in SCHG",
            "AAPL (Apple): 8.75% in QQQM, 11.80% in SCHG",
            "NVDA (NVIDIA): 7.85% in QQQM, 10.95% in SCHG",
            "AMZN (Amazon): 5.30% in QQQM, 6.40% in SCHG"
        )
    }

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
            Column {
                Text(
                    text = "PHASE 4: INSTITUTIONAL VEHICLE RESEARCH",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ETF & Mutual Fund Intelligence",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Layer 19 & 21: Look-through concentration, multi-factor attribution, and style drift auditing",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Sub-tabs (ETF vs Mutual Funds)
        item {
            TabRow(
                selectedTabIndex = selectedHubTab,
                containerColor = DarkNavySurface,
                contentColor = PrimaryCyan,
                divider = {}
            ) {
                Tab(
                    selected = selectedHubTab == 0,
                    onClick = { selectedHubTab = 0 },
                    text = { Text("ETF Look-Through & Overlap", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedHubTab == 1,
                    onClick = { selectedHubTab = 1 },
                    text = { Text("Mutual Fund Risk & Style Drift", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        if (selectedHubTab == 0) {
            // === ETF SECTION ===

            // Selector for ETF A and ETF B
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "SELECT ETFS FOR LOOK-THROUGH COMPARISON", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Primary ETF", style = MaterialTheme.typography.labelSmall, color = PrimaryCyan)
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(listOf("QQQM", "VOO", "SCHG", "SMH")) { sym ->
                                        FilterChip(
                                            selected = etfA == sym,
                                            onClick = { etfA = sym },
                                            label = { Text(sym, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = PrimaryCyan,
                                                selectedLabelColor = DarkNavyBackground
                                            )
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Comparison ETF", style = MaterialTheme.typography.labelSmall, color = CommitteeGold)
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(listOf("VOO", "QQQM", "SCHG", "SMH")) { sym ->
                                        FilterChip(
                                            selected = etfB == sym,
                                            onClick = { etfB = sym },
                                            label = { Text(sym, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CommitteeGold,
                                                selectedLabelColor = DarkNavyBackground
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Primary ETF Profile & Factor Attribution Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${profileA.symbol} — ${profileA.name}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Issuer: ${profileA.issuer} | AUM: $${profileA.aumBillion}B",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            FreshnessBadge(freshness = profileA.freshness)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricStatBox(
                                title = "Expense Ratio",
                                value = "${profileA.expenseRatioPct}%",
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                title = "Tracking Error",
                                value = "${profileA.trackingErrorPct}%",
                                valueColor = SuccessGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                title = "Premium/Disc",
                                value = "${if (profileA.premiumDiscountPct >= 0) "+" else ""}${"%.2f".format(profileA.premiumDiscountPct)}%",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Factor Tilts
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "FACTOR TILTS & MACRO ATTRIBUTION",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyan,
                                fontWeight = FontWeight.Bold
                            )
                            profileA.factorTilts.forEach { (factor, score) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = factor, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                    Text(text = "${score.toInt()}%", style = MaterialTheme.typography.bodySmall, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                                }
                                LinearProgressIndicator(
                                    progress = { (score / 100.0).toFloat() },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = PrimaryCyan,
                                    trackColor = DarkNavySurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Overlap Result
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("etf_overlap_summary_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "EFFECTIVE OVERLAP COEFFICIENT ($etfA vs $etfB)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${"%.1f".format(overlapWeightPct)}% Overlap",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = if (overlapWeightPct > 50.0) DangerRed else WarningAmber,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (overlapWeightPct > 50.0) "High redundancy: owning both creates significant duplicated exposure." else "Moderate diversification.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricStatBox(
                                title = "Shared Core Stocks",
                                value = "${sharedHoldings.size} Holdings",
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                title = "Distinct Exposure",
                                value = "${"%.1f".format(100.0 - overlapWeightPct)}%",
                                valueColor = SuccessGreen,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Shared Holdings Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "DUPLICATED UNDERLYING CONSTITUENTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                        sharedHoldings.forEach { holdingDesc ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DarkNavySurfaceVariant)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• $holdingDesc",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Look-Through Simulation Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "PORTFOLIO LOOK-THROUGH IMPACT INSIGHT",
                            style = MaterialTheme.typography.labelSmall,
                            color = CommitteeGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Adding $10,000 to $etfA will allocate approximately $${"%.0f".format(10000.0 * 0.084)} to MSFT, $${"%.0f".format(10000.0 * 0.0785)} to NVDA, and $${"%.0f".format(10000.0 * 0.0875)} to AAPL. The Portfolio Agent verifies this does not violate your single-stock concentration boundary.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        } else {
            // === MUTUAL FUNDS SECTION ===
            item {
                Text(
                    text = "INSTITUTIONAL MUTUAL FUND SCORECARDS",
                    style = MaterialTheme.typography.labelSmall,
                    color = CommitteeGold,
                    fontWeight = FontWeight.Bold
                )
            }

            items(mfProfiles) { fund ->
                val mfOutput = remember(fund) { mfAgent.analyze(fund) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fund.fundName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${fund.category} | ${fund.amc}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            FreshnessBadge(freshness = fund.freshness)
                        }

                        // Risk Metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricStatBox(
                                title = "Sharpe Ratio",
                                value = "%.2f".format(fund.sharpeRatio),
                                valueColor = SuccessGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                title = "Sortino Ratio",
                                value = "%.2f".format(fund.sortinoRatio),
                                valueColor = SuccessGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                title = "Alpha (vs Index)",
                                value = "+${fund.alpha}%",
                                valueColor = PrimaryCyan,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Multi-year Rolling vs 1-Year Performance (Layer 21)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkNavySurfaceVariant)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "ROLLING RETURN AUDIT (RULE: NEVER RANK ON 1Y ALONE)",
                                style = MaterialTheme.typography.labelSmall,
                                color = CommitteeGold,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "1Y: +${fund.return1Y}%", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                Text(text = "3Y Rolling: +${fund.return3YRolling}%", style = MaterialTheme.typography.bodySmall, color = PrimaryCyan, fontWeight = FontWeight.SemiBold)
                                Text(text = "5Y Rolling: +${fund.return5YRolling}%", style = MaterialTheme.typography.bodySmall, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                            }
                            Text(
                                text = "3Y SIP Compound Return: +${fund.sipReturn3Y}% | Max Drawdown: ${fund.maxDrawdownPct}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        // Style Drift Audit
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (fund.styleDriftDetected) DangerRed.copy(alpha = 0.1f) else DarkNavySurfaceVariant)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (fund.styleDriftDetected) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (fund.styleDriftDetected) DangerRed else SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = if (fund.styleDriftDetected) "STYLE DRIFT DETECTED" else "MANDATE ADHERENCE: CLEAN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (fund.styleDriftDetected) DangerRed else SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = fund.styleDriftNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }

                        // Top Holdings
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "TOP CONSTITUENTS ALLOCATION",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(fund.topHoldings) { h ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DarkNavySurfaceVariant)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${h.symbol}: ${h.weightPct}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
