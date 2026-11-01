package com.example.presentation.macro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ShowChart
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
import com.example.domain.agent.GeopoliticalAgent
import com.example.domain.agent.MacroEconomicAgent
import com.example.domain.agent.SectorRotationAgent
import com.example.domain.model.ThreatSeverity
import com.example.presentation.components.FreshnessBadge
import com.example.presentation.components.MetricStatBox
import com.example.ui.theme.*

@Composable
fun MacroSectorScreen(
    onInvestigateSymbol: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val phase4Repository = remember { Phase4Repository() }
    val macroAgent = remember { MacroEconomicAgent() }
    val geoAgent = remember { GeopoliticalAgent() }
    val sectorAgent = remember { SectorRotationAgent() }

    val policies = remember { phase4Repository.getCentralBankPolicies() }
    val yieldCurve = remember { phase4Repository.getYieldCurveMetrics() }
    val surprises = remember { phase4Repository.getMacroReleaseSurprises() }
    val theaters = remember { phase4Repository.getGeopoliticalTheaters() }
    val sectors = remember { phase4Repository.getGicsSectors() }

    val macroOutput = remember { macroAgent.analyze(policies, yieldCurve, surprises) }
    val geoOutput = remember { geoAgent.analyze(theaters) }
    val sectorOutput = remember { sectorAgent.analyze(sectors) }

    var selectedSection by remember { mutableIntStateOf(0) } // 0: Macro & Yield Curve, 1: Sector Rotation, 2: Geopolitics
    var selectedTimeframe by remember { mutableStateOf("1M") } // 1D, 1W, 1M, 3M, 6M, 1Y

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
                    text = "PHASE 4: MACRO, GLOBAL & SECTORS",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Macro & Rotation Intelligence",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Central bank transmission, 10Y-2Y curve regime, 11 GICS sector momentum, and geopolitical threat vectors",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Section Tabs
        item {
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = DarkNavySurface,
                contentColor = PrimaryCyan,
                divider = {}
            ) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = { Text("Macro & Curve", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = { Text("Sector Rotation", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    text = { Text("Geopolitics", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        when (selectedSection) {
            0 -> {
                // === MACRO & YIELD CURVE SECTION ===
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "MACROECONOMIC REGIME ASSESSMENT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = macroOutput.assessment.currentRegime.displayName,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = PrimaryCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SuccessGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "AUDIT PASS", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = macroOutput.assessment.currentRegime.assetImplication,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )

                            // Yield Curve Card
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkNavySurfaceVariant)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "10Y-2Y TREASURY YIELD CURVE REGIME",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CommitteeGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "10Y Yield", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                        Text(text = "${yieldCurve.yield10Y}%", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text(text = "2Y Yield", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                        Text(text = "${yieldCurve.yield2Y}%", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text(text = "Spread (10Y-2Y)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                        Text(
                                            text = "${if (yieldCurve.spread10Y2Y >= 0) "+" else ""}${"%.2f".format(yieldCurve.spread10Y2Y)}%",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (yieldCurve.isInverted) DangerRed else SuccessGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "Status: ${yieldCurve.curveRegime} | Real 10Y Rate: ${yieldCurve.realYield10Y}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                // Central Bank Tracker
                item {
                    Text(
                        text = "CENTRAL BANK POLICY & BALANCE SHEET TRACKER",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(policies) { policy ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = policy.bankName, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = policy.rateName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PrimaryCyan.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "${policy.policyRatePct}%", color = PrimaryCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(text = "Stance: ${policy.bias.label}", style = MaterialTheme.typography.bodySmall, color = CommitteeGold, fontWeight = FontWeight.SemiBold)
                            Text(text = "Action: ${policy.lastAction}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            Text(text = "Balance Sheet: ${policy.balanceSheetTrend}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                }

                // Macro Release Surprises
                item {
                    Text(
                        text = "KEY MACRO INDICATOR SURPRISES (ACTUAL VS CONSENSUS)",
                        style = MaterialTheme.typography.labelSmall,
                        color = CommitteeGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(surprises) { s ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${s.indicatorName} (${s.region})", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Surprise: ${if (s.surprisePct >= 0) "+" else ""}${"%.1f".format(s.surprisePct)}%",
                                    color = if (s.surprisePct >= 0) SuccessGreen else DangerRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "Actual: ${s.actualValue} ${s.unit}", style = MaterialTheme.typography.bodySmall, color = PrimaryCyan, fontWeight = FontWeight.SemiBold)
                                Text(text = "Consensus: ${s.consensusEstimate} ${s.unit}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text(text = "Prior: ${s.previousValue} ${s.unit}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                            Text(text = s.transmissionNote, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }
                }
            }

            1 -> {
                // === SECTOR ROTATION SECTION ===
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "BUSINESS CYCLE PHASE: ${sectorOutput.rotationAnalysis.cyclePhase.phaseName.uppercase()}",
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimaryCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = sectorOutput.actionableRotationTheme,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                            Text(
                                text = "Market Breadth: ${sectorOutput.rotationAnalysis.marketBreadthSummary}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CommitteeGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Timeframe Selector for Sector Returns
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "GICS SECTORS PERFORMANCE", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(listOf("1D", "1W", "1M", "3M", "6M", "1Y")) { tf ->
                                FilterChip(
                                    selected = selectedTimeframe == tf,
                                    onClick = { selectedTimeframe = tf },
                                    label = { Text(tf, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryCyan,
                                        selectedLabelColor = DarkNavyBackground
                                    )
                                )
                            }
                        }
                    }
                }

                // 11 GICS Sectors List
                items(sectors.sortedByDescending {
                    when (selectedTimeframe) {
                        "1D" -> it.return1D
                        "1W" -> it.return1W
                        "1M" -> it.return1M
                        "3M" -> it.return3M
                        "6M" -> it.return6M
                        else -> it.return1Y
                    }
                }) { sector ->
                    val ret = when (selectedTimeframe) {
                        "1D" -> sector.return1D
                        "1W" -> sector.return1W
                        "1M" -> sector.return1M
                        "3M" -> sector.return3M
                        "6M" -> sector.return6M
                        else -> sector.return1Y
                    }

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
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = sector.sectorName, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(DarkNavySurfaceVariant)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = sector.etfProxy, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    }
                                }
                                Text(
                                    text = "Rel. Strength: ${sector.relativeStrengthVsBenchmark}x | Breadth (>50 EMA): ${sector.pctAbove50Ema.toInt()}% | Flow: ${sector.institutionalFlow}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (ret >= 0) SuccessGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${if (ret >= 0) "+" else ""}${"%.2f".format(ret)}%",
                                    color = if (ret >= 0) SuccessGreen else DangerRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // === GEOPOLITICS SECTION ===
                item {
                    Text(
                        text = "GLOBAL GEOPOLITICAL RISK & TRANSMISSION PIPELINES",
                        style = MaterialTheme.typography.labelSmall,
                        color = DangerRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(theaters) { theater ->
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
                                        text = theater.headline,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Theater: ${theater.region}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (theater.severity == ThreatSeverity.CRITICAL) DangerRed.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = theater.severity.label,
                                        color = if (theater.severity == ThreatSeverity.CRITICAL) DangerRed else WarningAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(text = theater.summary, style = MaterialTheme.typography.bodySmall, color = TextPrimary)

                            // Transmission Pipeline Card (Layer 23)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkNavySurfaceVariant)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "TRANSMISSION MODEL: EVENT → SUPPLY CHAIN → EARNINGS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Primary Chokepoint: ${theater.transmission.primaryChokepoint}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CommitteeGold
                                )
                                Text(
                                    text = "Cost/Revenue Impact: ${theater.transmission.costRevenueTransmission}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Affected Portfolio Symbols: ${theater.transmission.affectedPortfolioSymbols.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DangerRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Est. Vulnerability: -${theater.transmission.estimatedEarningsVulnerabilityPct}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = WarningAmber
                                    )
                                }
                                Text(
                                    text = "Hedging: ${theater.transmission.hedgingMechanism}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
