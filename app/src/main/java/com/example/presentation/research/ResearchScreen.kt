package com.example.presentation.research

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
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
import com.example.domain.model.*
import com.example.presentation.components.DecisionBadge
import com.example.presentation.components.FreshnessBadge
import com.example.presentation.components.MetricStatBox
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResearchScreen(
    currentReport: ResearchReport?,
    isResearching: Boolean,
    agentProgress: List<AgentStepProgress>,
    onTriggerResearch: (String) -> Unit,
    initialQuery: String = "",
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf(initialQuery.ifBlank { "NVDA" }) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Universal Research Box
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "UNIVERSAL RESEARCH DESK",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Enter symbol (e.g. NVDA, QQQM, RELIANCE.NS, VOO)...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryCyan) },
                    trailingIcon = {
                        Button(
                            onClick = { onTriggerResearch(searchQuery) },
                            enabled = !isResearching && searchQuery.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = DarkNavyBackground),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .testTag("run_research_button")
                        ) {
                            Text("Analyze", fontWeight = FontWeight.Bold)
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkNavySurface,
                        unfocusedContainerColor = DarkNavySurface,
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = DarkNavyOutline,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("universal_research_input")
                )

                // Quick Picks
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val presets = listOf("NVDA", "QQQM", "RELIANCE.NS", "VOO", "MSFT", "GLD")
                    items(presets) { preset ->
                        AssistChip(
                            onClick = {
                                searchQuery = preset
                                onTriggerResearch(preset)
                            },
                            label = { Text(preset, color = TextPrimary, fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = DarkNavySurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkNavyOutline),
                            modifier = Modifier.testTag("quick_pick_$preset")
                        )
                    }
                }
            }
        }

        // 2. Multi-Agent Live Execution Progress Visualizer
        if (isResearching || agentProgress.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("agent_pipeline_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MULTI-AGENT RESEARCH PIPELINE",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyan,
                                fontWeight = FontWeight.Bold
                            )
                            if (isResearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = PrimaryCyan,
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        agentProgress.forEach { step ->
                            AgentProgressItem(step = step)
                        }
                    }
                }
            }
        }

        // 3. Final Institutional Research Report
        if (currentReport != null && !isResearching) {
            // Header
            item {
                ReportHeaderCard(report = currentReport)
            }

            // Phase 5: Investment Committee Final Verdict (Layer 34 & 35)
            if (currentReport.committeeVerdict != null) {
                item {
                    InvestmentCommitteeCard(verdict = currentReport.committeeVerdict)
                }
            }

            // Phase 5: Valuation Scenarios & Models (Layer 26)
            if (currentReport.valuationAssessment != null) {
                item {
                    ValuationScenariosCard(valuation = currentReport.valuationAssessment)
                }
            } else {
                item {
                    ValuationStatsSection(report = currentReport)
                }
            }

            // Phase 5: Risk Decomposition & Position Sizing (Layers 27 & 29)
            if (currentReport.riskDecomposition != null && currentReport.positionSizing != null) {
                item {
                    PortfolioRiskAndSizingCard(
                        risk = currentReport.riskDecomposition,
                        sizing = currentReport.positionSizing
                    )
                }
            }

            // Core Investment Thesis
            item {
                SectionCard(title = "CORE INVESTMENT THESIS") {
                    Text(
                        text = currentReport.thesis,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }

            // Catalysts & Risks
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionCard(title = "UPCOMING CATALYSTS", modifier = Modifier.weight(1f)) {
                        currentReport.catalysts.forEach { catalyst ->
                            Text(text = "• $catalyst", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                    SectionCard(title = "PRIMARY RISKS", modifier = Modifier.weight(1f)) {
                        currentReport.risks.forEach { risk ->
                            Text(text = "• $risk", style = MaterialTheme.typography.bodySmall, color = DangerRed)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            // Phase 5: Conflict Resolution Engine (Layer 31)
            if (currentReport.conflictResolutions.isNotEmpty()) {
                item {
                    ConflictResolutionCard(conflicts = currentReport.conflictResolutions)
                }
            }

            // Phase 5: Devil's Advocate & Thesis Falsification (Layer 32)
            if (currentReport.devilsAdvocate != null) {
                item {
                    DevilsAdvocateCard(assessment = currentReport.devilsAdvocate)
                }
            } else {
                item {
                    SectionCard(title = "DEVIL'S ADVOCATE / BEAR CASE STRESS-TEST") {
                        Text(
                            text = currentReport.bearCase,
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarningAmber,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Technical & Macro Context
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionCard(title = "TECHNICAL REGIME", modifier = Modifier.weight(1f)) {
                        Text(text = currentReport.technicalView, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    }
                    SectionCard(title = "MACRO CONTEXT", modifier = Modifier.weight(1f)) {
                        Text(text = currentReport.macroView, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    }
                }
            }

            // Portfolio Fit
            item {
                SectionCard(title = "PORTFOLIO FIT & OVERLAP IMPACT") {
                    Text(
                        text = currentReport.portfolioFitNote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }

            // Phase 5: Research Auditor Pre-Decision Stamp (Layer 33)
            if (currentReport.auditVerdict != null) {
                item {
                    ResearchAuditorCard(verdict = currentReport.auditVerdict)
                }
            }

            // Evidence Audit Trail
            item {
                SectionCard(title = "AUDIT TRAIL & FACTUAL EVIDENCE") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        currentReport.evidenceList.forEach { ev ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkNavySurfaceVariant)
                                    .padding(8.dp)
                            ) {
                                Text(text = ev.claim, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Source: ${ev.source}", style = MaterialTheme.typography.labelSmall, color = PrimaryCyan)
                                    Text(text = "Data: ${ev.supportingData}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Phase 5 UI Components
// -------------------------------------------------------------

@Composable
private fun InvestmentCommitteeCard(verdict: InvestmentCommitteeVerdict) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("investment_committee_card"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CommitteeGold)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header with badge & conviction
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INVESTMENT COMMITTEE VERDICT",
                        style = MaterialTheme.typography.labelSmall,
                        color = CommitteeGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Horizon: ${verdict.investmentHorizon}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                DecisionBadge(decision = verdict.decision, confidencePct = verdict.aggregateConvictionScore)
            }

            // Conviction progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Aggregate Institutional Conviction",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "${verdict.aggregateConvictionScore}% / 100%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (verdict.aggregateConvictionScore >= 80) SuccessGreen else PrimaryCyan
                    )
                }
                LinearProgressIndicator(
                    progress = { (verdict.aggregateConvictionScore / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (verdict.aggregateConvictionScore >= 80) SuccessGreen else PrimaryCyan,
                    trackColor = DarkNavySurfaceVariant,
                )
            }

            // Executive Summary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkNavySurfaceVariant)
                    .padding(10.dp)
            ) {
                Text(
                    text = verdict.executiveSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }

            // Factor Votes Table
            Text(
                text = "COMMITTEE FACTOR WEIGHTING",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                verdict.factorVotes.forEach { vote ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkNavyBackground)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${vote.factorName} (${(vote.weightPct * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = vote.summary,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = "${vote.convictionScore}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                vote.convictionScore >= 80 -> SuccessGreen
                                vote.convictionScore >= 60 -> CommitteeGold
                                else -> DangerRed
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Action Plan
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .background(PrimaryCyan.copy(alpha = 0.08f))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "TACTICAL ACTION PLAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = verdict.actionPlan,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ValuationScenariosCard(valuation: ValuationAssessment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("valuation_scenarios_card"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header with Valuation Classification
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VALUATION SCENARIOS & MODELS",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(valuation.classification.colorHex).copy(alpha = 0.15f))
                        .border(0.5.dp, Color(valuation.classification.colorHex), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = valuation.classification.label,
                        color = Color(valuation.classification.colorHex),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3-Scenario Range: Bear / Base / Bull
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatBox(
                    title = "Bear Scenario",
                    value = "$${"%,.0f".format(valuation.bearValue)}",
                    valueColor = DangerRed,
                    modifier = Modifier.weight(1f)
                )
                MetricStatBox(
                    title = "Base Fair Value",
                    value = "$${"%,.0f".format(valuation.baseValue)}",
                    valueColor = CommitteeGold,
                    modifier = Modifier.weight(1f)
                )
                MetricStatBox(
                    title = "Bull Scenario",
                    value = "$${"%,.0f".format(valuation.bullValue)}",
                    valueColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            // Entry Zone & Margin of Safety
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkNavySurfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "DISCIPLINED ENTRY ZONE", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        text = "$${"%,.0f".format(valuation.entryZoneLow)} – $${"%,.0f".format(valuation.entryZoneHigh)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "MARGIN OF SAFETY", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        text = "${"%.1f".format(valuation.marginOfSafetyPct)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (valuation.marginOfSafetyPct >= 0) SuccessGreen else DangerRed
                    )
                }
            }

            // Model Breakdown: DCF vs Multiples
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // DCF Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkNavyBackground)
                        .padding(8.dp)
                ) {
                    Text(text = "MULTI-STAGE DCF (60%)", style = MaterialTheme.typography.labelSmall, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Implied: $${"%,.2f".format(valuation.dcfModel.impliedPerShareValue)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(text = "WACC: ${valuation.dcfModel.waccPct}% | Terminal: ${valuation.dcfModel.terminalGrowthRatePct}%", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = "Growth: ${valuation.dcfModel.stage1GrowthRatePct}% / ${valuation.dcfModel.stage2GrowthRatePct}%", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }

                // Multiples Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkNavyBackground)
                        .padding(8.dp)
                ) {
                    Text(text = "MULTIPLES BLEND (40%)", style = MaterialTheme.typography.labelSmall, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Implied: $${"%,.2f".format(valuation.multiplesModel.impliedMultiplePrice)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(text = "Fwd P/E: ${"%.1f".format(valuation.multiplesModel.forwardPe)}x | PEG: ${valuation.multiplesModel.pegRatio}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = "Peer Avg P/E: ${valuation.multiplesModel.peerAveragePe}x", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun PortfolioRiskAndSizingCard(
    risk: RiskDecomposition,
    sizing: PositionSizingRecommendation
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("risk_and_sizing_card"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (risk.isConcentrationBreached) DangerRed else DarkNavyOutline
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PORTFOLIO RISK & TRANCHE SIZING",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
                if (risk.isConcentrationBreached) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DangerRed.copy(alpha = 0.15f))
                            .border(0.5.dp, DangerRed, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "15% CEILING REACHED", color = DangerRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Concentration Bars
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Single-Stock Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Single-Stock Concentration (Max 15%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "${"%.1f".format(risk.singleStockConcentrationPct)}% / ${risk.concentrationPolicyLimitPct}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (risk.isConcentrationBreached) DangerRed else TextPrimary
                    )
                }
                LinearProgressIndicator(
                    progress = { (risk.singleStockConcentrationPct / risk.concentrationPolicyLimitPct).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (risk.isConcentrationBreached) DangerRed else PrimaryCyan,
                    trackColor = DarkNavySurfaceVariant
                )

                // Sector Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sector Exposure (Max 35%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "${"%.1f".format(risk.sectorWeightPct)}% / ${risk.sectorLimitPct}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (risk.isSectorLimitBreached) WarningAmber else TextPrimary
                    )
                }
                LinearProgressIndicator(
                    progress = { (risk.sectorWeightPct / risk.sectorLimitPct).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (risk.isSectorLimitBreached) WarningAmber else TechPurple,
                    trackColor = DarkNavySurfaceVariant
                )
            }

            // Risk Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatBox(
                    title = "Beta vs S&P",
                    value = "${"%.2f".format(risk.betaVsBenchmark)}x",
                    modifier = Modifier.weight(1f)
                )
                MetricStatBox(
                    title = "Max Drawdown",
                    value = "${"%.1f".format(risk.maxHistoricalDrawdownPct)}%",
                    valueColor = DangerRed,
                    modifier = Modifier.weight(1f)
                )
                MetricStatBox(
                    title = "Annual Volatility",
                    value = "${"%.1f".format(risk.annualizedVolatilityPct)}%",
                    modifier = Modifier.weight(1f)
                )
            }

            // Position Sizing Recommendation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkNavySurfaceVariant)
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Approved Tranche Sizing",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan
                        )
                        Text(
                            text = if (sizing.suggestedIncrementalAllocationPct > 0)
                                "+${"%.1f".format(sizing.suggestedIncrementalAllocationPct)}% (~$${"%,.0f".format(sizing.dollarTrancheEstimate)})"
                            else "0.0% (Accumulation Frozen)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (sizing.suggestedIncrementalAllocationPct > 0) SuccessGreen else DangerRed
                        )
                    }
                    Text(
                        text = sizing.sizingRationale,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ConflictResolutionCard(conflicts: List<ConflictResolution>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("conflict_engine_card"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "CONFLICT RESOLUTION ENGINE (${conflicts.size} DIVERGENCES ANALYZED)",
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber,
                fontWeight = FontWeight.Bold
            )

            conflicts.forEach { conflict ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkNavySurfaceVariant)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "• ${conflict.conflictType.label}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Bullish vs Bearish signals
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Bullish Forces", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                            conflict.bullishSignals.forEach { s ->
                                Text(text = "+ $s", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Bearish Constraints", style = MaterialTheme.typography.labelSmall, color = DangerRed)
                            conflict.bearishSignals.forEach { s ->
                                Text(text = "- $s", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                    }

                    // Resolution Strategy
                    Text(
                        text = conflict.resolutionStrategy,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun DevilsAdvocateCard(assessment: DevilsAdvocateAssessment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("devils_advocate_card"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(WarningAmber.copy(alpha = 0.5f))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DEVIL'S ADVOCATE & FALSIFICATION ENGINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = assessment.probabilityRating,
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bear Case Scenario
            Text(
                text = assessment.bearCaseScenario,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                lineHeight = 18.sp
            )

            // Falsification Triggers Table
            Text(
                text = "THESIS FALSIFICATION TRIGGERS ('What Makes This Thesis Wrong?')",
                style = MaterialTheme.typography.labelSmall,
                color = DangerRed,
                fontWeight = FontWeight.Bold
            )

            assessment.thesisFalsificationTriggers.forEach { trigger ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkNavyBackground)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = trigger.riskCategory,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )
                        Text(
                            text = "-${"%.0f".format(trigger.estimatedDrawdownImpactPct)}% Drawdown",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )
                    }
                    Text(
                        text = "Trigger: ${trigger.triggerCondition}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Early Warning: ${trigger.earlyWarningMetric}",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningAmber
                    )
                }
            }

            // Fatal Flaw Verdict
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkNavySurfaceVariant)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Advocate Verdict: ${assessment.fatalFlawVerdict}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ResearchAuditorCard(verdict: ResearchAuditVerdict) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("research_auditor_card"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (verdict.isAuditPassed) SuccessGreen else DangerRed
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RESEARCH AUDITOR GATING STAMP",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (verdict.isAuditPassed) SuccessGreen else DangerRed,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (verdict.isAuditPassed) SuccessGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (verdict.isAuditPassed) "PASSED (7/7)" else "FAILED",
                        color = if (verdict.isAuditPassed) SuccessGreen else DangerRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = verdict.overallStatus,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (verdict.isAuditPassed) SuccessGreen else DangerRed
            )

            // 7-Point Checklist
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                verdict.checklist.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkNavyBackground)
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isPassed) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = if (item.isPassed) "Passed" else "Failed",
                            tint = if (item.isPassed) SuccessGreen else DangerRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = item.checkName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = item.detail,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgentProgressItem(step: AgentStepProgress) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (step.status) {
                AgentStatus.COMPLETED -> Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                AgentStatus.RUNNING -> CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = PrimaryCyan)
                AgentStatus.WAITING -> Box(modifier = Modifier.size(14.dp).clip(RoundedCornerShape(7.dp)).background(DarkNavyOutline))
                else -> Icon(Icons.Default.Refresh, contentDescription = "Active", tint = WarningAmber, modifier = Modifier.size(16.dp))
            }
            Text(
                text = step.agentName,
                style = MaterialTheme.typography.bodySmall,
                color = if (step.status == AgentStatus.COMPLETED) TextPrimary else TextSecondary,
                fontWeight = if (step.status == AgentStatus.RUNNING) FontWeight.Bold else FontWeight.Normal
            )
        }
        Text(
            text = step.detail,
            style = MaterialTheme.typography.labelSmall,
            color = if (step.status == AgentStatus.COMPLETED) SuccessGreen else TextMuted
        )
    }
}

@Composable
private fun ReportHeaderCard(report: ResearchReport) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("report_header_card"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = report.symbol, style = MaterialTheme.typography.headlineLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(text = report.securityName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                FreshnessBadge(state = report.currentDataStatus)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DecisionBadge(decision = report.decision, confidencePct = report.confidencePct)
                Text(
                    text = "Horizon: ${report.investmentHorizon}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun ValuationStatsSection(report: ResearchReport) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "VALUATION & TARGET ZONES",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricStatBox(
                title = "Current Price",
                value = "$${"%,.2f".format(report.currentPrice)}",
                modifier = Modifier.weight(1f)
            )
            MetricStatBox(
                title = "Fair Value Range",
                value = "$${"%,.0f".format(report.fairValueLow)} - $${"%,.0f".format(report.fairValueHigh)}",
                valueColor = CommitteeGold,
                modifier = Modifier.weight(1f)
            )
            MetricStatBox(
                title = "Entry Zone",
                value = "$${"%,.0f".format(report.entryZoneLow)} - $${"%,.0f".format(report.entryZoneHigh)}",
                valueColor = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkNavyOutline))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryCyan,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
