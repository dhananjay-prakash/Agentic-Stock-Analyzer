package com.example.presentation.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Currency
import com.example.domain.model.PortfolioHolding
import com.example.ui.theme.*

@Composable
fun PortfolioHeatmap(
    holdings: List<PortfolioHolding>,
    pinnedSymbols: List<String>,
    onHoldingSelected: (String) -> Unit,
    onTogglePin: (PortfolioHolding) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSymbol by remember { mutableStateOf<String?>(holdings.firstOrNull()?.symbol) }

    val totalMarketValue = remember(holdings) {
        holdings.sumOf { h ->
            val mult = if (h.currency == Currency.INR) 1.0 / 84.0 else 1.0
            h.currentMarketValue * mult
        }
    }

    // Group holdings by sector
    val sectorGroups = remember(holdings) {
        holdings.groupBy { if (it.sector.isNotBlank()) it.sector else "Other" }
    }

    val selectedHolding = remember(selectedSymbol, holdings) {
        holdings.find { it.symbol == selectedSymbol } ?: holdings.firstOrNull()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "SECTOR ASSET DISTRIBUTION",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Portfolio Treemap Heatmap",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Tile sizing reflects portfolio allocation weight. Colors reflect today's % performance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // Heatmap Legend
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkNavySurface)
                    .border(1.dp, DarkNavyOutline, RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "<-2% Bearish", fontSize = 10.sp, color = DangerRed, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(16.dp, 10.dp).background(Color(0xFFD50000)))
                    Box(modifier = Modifier.size(16.dp, 10.dp).background(Color(0xFFE53935)))
                    Box(modifier = Modifier.size(16.dp, 10.dp).background(Color(0xFF1E293B)))
                    Box(modifier = Modifier.size(16.dp, 10.dp).background(Color(0xFF00C853)))
                    Box(modifier = Modifier.size(16.dp, 10.dp).background(Color(0xFF00E676)))
                }
                Text(text = ">+2% Bullish", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
            }
        }

        // Treemap Sectors
        items(sectorGroups.entries.toList()) { (sector, sectorHoldings) ->
            SectorTreemapBlock(
                sector = sector,
                holdings = sectorHoldings,
                totalPortfolioValueUsd = totalMarketValue,
                selectedSymbol = selectedSymbol,
                onSelectSymbol = { selectedSymbol = it }
            )
        }

        // Detail Card for Selected Asset
        item {
            selectedHolding?.let { h ->
                val isPinned = pinnedSymbols.contains(h.symbol)
                val currSym = if (h.currency == Currency.INR) "₹" else "$"
                val isPositive = h.dayChangePct >= 0
                val perfColor = if (isPositive) SuccessGreen else DangerRed

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("heatmap_selected_asset_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(PrimaryCyan.copy(alpha = 0.5f)))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = h.symbol, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DarkNavySurfaceVariant)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = h.sector, fontSize = 10.sp, color = PrimaryCyan, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Text(text = h.securityName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }

                            IconButton(
                                onClick = { onTogglePin(h) },
                                modifier = Modifier.testTag("pin_holding_${h.symbol}")
                            ) {
                                Icon(
                                    imageVector = if (isPinned) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Pin to Watchlist",
                                    tint = if (isPinned) PrimaryCyan else TextMuted
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "CURRENT VALUE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(text = "$currSym${"%,.2f".format(h.currentMarketValue)}", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "TODAY'S MOVE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(
                                    text = "${if (isPositive) "+" else ""}${"%.2f".format(h.dayChangePct)}%",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = perfColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = { onHoldingSelected(h.symbol) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = DarkNavyBackground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("heatmap_analyze_btn")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Analyze ${h.symbol} in Universal Research Desk", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectorTreemapBlock(
    sector: String,
    holdings: List<PortfolioHolding>,
    totalPortfolioValueUsd: Double,
    selectedSymbol: String?,
    onSelectSymbol: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(DarkNavyOutline))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Sector Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sector.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
                val sectorTotalUsd = holdings.sumOf {
                    val mult = if (it.currency == Currency.INR) 1.0 / 84.0 else 1.0
                    it.currentMarketValue * mult
                }
                val sectorWeightPct = if (totalPortfolioValueUsd > 0) (sectorTotalUsd / totalPortfolioValueUsd) * 100.0 else 0.0
                Text(
                    text = "$${"%,.0f".format(sectorTotalUsd)} (${"%.1f".format(sectorWeightPct)}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            // Treemap Grid Rows
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                holdings.forEach { holding ->
                    val mult = if (holding.currency == Currency.INR) 1.0 / 84.0 else 1.0
                    val holdingValUsd = holding.currentMarketValue * mult
                    val weight = (holdingValUsd / (totalPortfolioValueUsd.coerceAtLeast(1.0))).toFloat()
                    val flexWeight = (weight * 100).coerceAtLeast(1f)

                    TreemapTile(
                        holding = holding,
                        isSelected = holding.symbol == selectedSymbol,
                        onClick = { onSelectSymbol(holding.symbol) },
                        modifier = Modifier
                            .weight(flexWeight)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun TreemapTile(
    holding: PortfolioHolding,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chg = holding.dayChangePct
    val tileColor = when {
        chg >= 2.0 -> Color(0xFF00E676)
        chg >= 0.3 -> Color(0xFF00C853)
        chg > -0.3 -> Color(0xFF1E293B)
        chg >= -2.0 -> Color(0xFFE53935)
        else -> Color(0xFFD50000)
    }

    val textColor = if (chg > -0.3 && chg < 0.3) TextPrimary else DarkNavyBackground

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(tileColor)
            .border(
                width = if (isSelected) 2.dp else 0.5.dp,
                color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = holding.symbol,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = textColor,
                maxLines = 1
            )
            val prefix = if (chg >= 0) "+" else ""
            Text(
                text = "$prefix${"%.2f".format(chg)}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1
            )
        }
    }
}
