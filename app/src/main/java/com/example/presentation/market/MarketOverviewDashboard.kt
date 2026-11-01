package com.example.presentation.market

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.MetricStatBox
import com.example.presentation.macro.MacroSectorScreen
import com.example.ui.theme.*
import kotlin.math.roundToInt

data class IndexDataPoint(
    val timestamp: String,
    val value: Double
)

data class MarketIndexInfo(
    val symbol: String,
    val name: String,
    val currentValue: Double,
    val changeAmount: Double,
    val changePct: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val fiftyTwoHigh: Double,
    val fiftyTwoLow: Double,
    val currency: String,
    val points: List<IndexDataPoint>
)

@Composable
fun MarketOverviewDashboard(
    onIndexSelectedForResearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMarketSubTab by remember { mutableIntStateOf(0) } // 0: Global Indices, 1: Macro & Sectors
    var selectedSymbol by remember { mutableStateOf("^GSPC") }
    var selectedHorizon by remember { mutableStateOf("1M") }

    // Mock Financial Data for Key Global Indices (Explicitly Labeled per Layer 52)
    val indices = remember {
        listOf(
            generateMockIndex("^GSPC", "S&P 500", 5864.67, 34.20, 0.59, 5878.50, 5832.10, 5885.00, 4103.78, "$"),
            generateMockIndex("^NDX", "NASDAQ 100", 20452.12, 185.40, 0.92, 20510.30, 20280.40, 20690.97, 14058.30, "$"),
            generateMockIndex("^NSEI", "NIFTY 50", 24201.35, 128.75, 0.53, 24310.00, 24120.40, 26277.35, 18837.85, "₹"),
            generateMockIndex("^BSESN", "BSE SENSEX", 79724.12, 385.20, 0.49, 80110.00, 79450.00, 85978.25, 63583.07, "₹"),
            generateMockIndex("XAU/USD", "Spot Gold", 2748.50, 14.80, 0.54, 2758.40, 2732.10, 2790.15, 1973.20, "$"),
            generateMockIndex("^TNX", "US 10-Yr Yield", 4.28, -0.04, -0.93, 4.34, 4.25, 5.02, 3.60, "%")
        )
    }

    val currentIndex = indices.find { it.symbol == selectedSymbol } ?: indices.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        // Market Sub-navigation Tab Row
        TabRow(
            selectedTabIndex = currentMarketSubTab,
            containerColor = DarkNavySurface,
            contentColor = PrimaryCyan,
            divider = {}
        ) {
            Tab(
                selected = currentMarketSubTab == 0,
                onClick = { currentMarketSubTab = 0 },
                text = { Text("Global Indices", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = currentMarketSubTab == 1,
                onClick = { currentMarketSubTab = 1 },
                text = { Text("Macro & Sectors", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        if (currentMarketSubTab == 1) {
            MacroSectorScreen(
                onInvestigateSymbol = onIndexSelectedForResearch
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title & Mandatory Mock Data Transparency Badge
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                    Column {
                        Text(
                            text = "INSTITUTIONAL PERFORMANCE INDICES",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Market Overview Dashboard",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(WarningAmber.copy(alpha = 0.15f))
                            .border(0.5.dp, WarningAmber.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MOCK FINANCIAL DATA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber
                        )
                    }
                }
            }
        }

        // Index Selector Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(indices) { idx ->
                    FilterChip(
                        selected = idx.symbol == selectedSymbol,
                        onClick = { selectedSymbol = idx.symbol },
                        label = {
                            Text(
                                text = "${idx.name} (${if (idx.changePct >= 0) "+" else ""}${"%.2f".format(idx.changePct)}%)",
                                fontSize = 12.sp,
                                fontWeight = if (idx.symbol == selectedSymbol) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCyan,
                            selectedLabelColor = DarkNavyBackground,
                            containerColor = DarkNavySurfaceVariant,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = idx.symbol == selectedSymbol,
                            borderColor = DarkNavyOutline,
                            selectedBorderColor = PrimaryCyan
                        ),
                        modifier = Modifier.testTag("index_chip_${idx.symbol}")
                    )
                }
            }
        }

        // Current Index Value & Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("index_header_card"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(DarkNavyOutline))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "${currentIndex.name} (${currentIndex.symbol})",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "${currentIndex.currency}${"%,.2f".format(currentIndex.currentValue)}",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                val isPositive = currentIndex.changePct >= 0
                                val color = if (isPositive) SuccessGreen else DangerRed
                                val prefix = if (isPositive) "+" else ""
                                Text(
                                    text = "$prefix${"%,.2f".format(currentIndex.changeAmount)} ($prefix${"%.2f".format(currentIndex.changePct)}%)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = color,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { onIndexSelectedForResearch(currentIndex.name) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkNavySurfaceVariant, contentColor = PrimaryCyan),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(DarkNavyOutline)),
                            modifier = Modifier.testTag("research_index_btn")
                        ) {
                            Text("Analyze", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Horizon Tabs (1D, 1W, 1M, 1Y, 5Y)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("1D", "1W", "1M", "1Y", "5Y").forEach { horizon ->
                            val isSelected = horizon == selectedHorizon
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) PrimaryCyan else DarkNavySurfaceVariant)
                                    .border(0.5.dp, if (isSelected) PrimaryCyan else DarkNavyOutline, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                                    .testTag("horizon_tab_$horizon"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = horizon,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) DarkNavyBackground else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recharts-like Interactive Compose Chart
                    RechartsAreaChart(
                        dataPoints = currentIndex.points,
                        lineColor = if (currentIndex.changePct >= 0) SuccessGreen else DangerRed,
                        currency = currentIndex.currency,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .testTag("recharts_area_chart")
                    )
                }
            }
        }

        // Performance Range & Session Metrics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(DarkNavyOutline))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "52-WEEK SPREAD & SESSION BOUNDS",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )

                    // 52W Range Progress bar
                    val range = currentIndex.fiftyTwoHigh - currentIndex.fiftyTwoLow
                    val fraction = if (range > 0) ((currentIndex.currentValue - currentIndex.fiftyTwoLow) / range).toFloat().coerceIn(0f, 1f) else 0.5f

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("52W Low: ${currentIndex.currency}${"%,.2f".format(currentIndex.fiftyTwoLow)}", fontSize = 11.sp, color = TextMuted)
                            Text("Current Position: ${(fraction * 100).roundToInt()}%", fontSize = 11.sp, color = CommitteeGold, fontWeight = FontWeight.Bold)
                            Text("52W High: ${currentIndex.currency}${"%,.2f".format(currentIndex.fiftyTwoHigh)}", fontSize = 11.sp, color = TextMuted)
                        }
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PrimaryCyan,
                            trackColor = DarkNavyOutline
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricStatBox(
                            title = "Day Range",
                            value = "${currentIndex.currency}${"%,.0f".format(currentIndex.dayLow)} - ${currentIndex.currency}${"%,.0f".format(currentIndex.dayHigh)}",
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatBox(
                            title = "Volatility Index",
                            value = "14.2% Beta",
                            valueColor = SuccessGreen,
                            subtitle = "Low Risk Regime",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Sector Breadth Heatmap
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(DarkNavyOutline))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "GLOBAL SECTOR BREADTH (MOCK HEATMAP)",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )

                    val sectors = listOf(
                        "Technology" to 1.84,
                        "Consumer Cyclical" to 0.92,
                        "Industrials" to 0.65,
                        "Financials" to 0.38,
                        "Healthcare" to 0.15,
                        "Energy" to -0.52
                    )

                    sectors.chunked(2).forEach { rowSectors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowSectors.forEach { (secName, secChg) ->
                                val secColor = if (secChg >= 0) SuccessGreen else DangerRed
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkNavySurfaceVariant)
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = secName, fontSize = 11.sp, color = TextPrimary)
                                    Text(
                                        text = "${if (secChg >= 0) "+" else ""}${"%.2f".format(secChg)}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = secColor
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
}



/**
 * High-performance Recharts-style Canvas chart:
 * - Cubic Bezier smooth spline curve
 * - Area gradient fill with vertical fade
 * - Reference grid lines
 * - Interactive pointer drag / touch hover crosshair with dynamic value tooltip
 */
@Composable
fun RechartsAreaChart(
    dataPoints: List<IndexDataPoint>,
    lineColor: Color,
    currency: String,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    var touchNormalizedX by remember { mutableFloatStateOf(-1f) }

    val minValue = dataPoints.minOf { it.value }
    val maxValue = dataPoints.maxOf { it.value }
    val valueRange = if (maxValue > minValue) maxValue - minValue else 1.0

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(dataPoints) {
                    detectDragGestures(
                        onDragStart = { offset -> touchNormalizedX = (offset.x / size.width).coerceIn(0f, 1f) },
                        onDragEnd = { touchNormalizedX = -1f },
                        onDragCancel = { touchNormalizedX = -1f },
                        onDrag = { change, _ ->
                            touchNormalizedX = (change.position.x / size.width).coerceIn(0f, 1f)
                        }
                    )
                }
                .pointerInput(dataPoints) {
                    detectTapGestures(
                        onPress = { offset ->
                            touchNormalizedX = (offset.x / size.width).coerceIn(0f, 1f)
                            tryAwaitRelease()
                            touchNormalizedX = -1f
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw Recharts Horizontal Reference Grid Lines (4 levels)
            val gridColor = Color(0xFF243250)
            for (i in 0..3) {
                val y = height * (i / 3f)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )
            }

            // 2. Build Cubic Bezier Spline Path
            val points = dataPoints.mapIndexed { index, dp ->
                val x = (index.toFloat() / (dataPoints.size - 1)) * width
                val y = height - (((dp.value - minValue) / valueRange).toFloat() * (height * 0.85f)) - (height * 0.05f)
                Offset(x, y)
            }

            val strokePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val cur = points[i]
                        val cx = (prev.x + cur.x) / 2f
                        cubicTo(cx, prev.y, cx, cur.y, cur.x, cur.y)
                    }
                }
            }

            // 3. Draw Area Gradient Fill (Below Line)
            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.35f),
                        lineColor.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )

            // 4. Draw Main Line Stroke
            drawPath(
                path = strokePath,
                color = lineColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 5. Interactive Touch Hover Crosshair & Marker Pin
            if (touchNormalizedX in 0f..1f && points.isNotEmpty()) {
                val hoverIndex = ((dataPoints.size - 1) * touchNormalizedX).roundToInt().coerceIn(0, dataPoints.size - 1)
                val hoverPoint = points[hoverIndex]

                // Draw Vertical Dashed Crosshair Line
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(hoverPoint.x, 0f),
                    end = Offset(hoverPoint.x, height),
                    strokeWidth = 1.2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // Outer Glowing Circle
                drawCircle(
                    color = lineColor.copy(alpha = 0.4f),
                    radius = 8.dp.toPx(),
                    center = hoverPoint
                )
                // Inner Solid Dot
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = hoverPoint
                )
            }
        }

        // 6. Interactive Floating Tooltip Bubble
        if (touchNormalizedX in 0f..1f && dataPoints.isNotEmpty()) {
            val hoverIndex = ((dataPoints.size - 1) * touchNormalizedX).roundToInt().coerceIn(0, dataPoints.size - 1)
            val hoverData = dataPoints[hoverIndex]

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkNavySurfaceVariant)
                    .border(1.dp, PrimaryCyan, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = hoverData.timestamp, fontSize = 10.sp, color = TextSecondary)
                    Text(
                        text = "$currency${"%,.2f".format(hoverData.value)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                }
            }
        }
    }
}

private fun generateMockIndex(
    symbol: String,
    name: String,
    current: Double,
    change: Double,
    pct: Double,
    dayHigh: Double,
    dayLow: Double,
    fiftyTwoHigh: Double,
    fiftyTwoLow: Double,
    currency: String
): MarketIndexInfo {
    val points = mutableListOf<IndexDataPoint>()
    val base = current - (change * 1.5)
    val count = 24
    for (i in 0 until count) {
        val progress = i.toDouble() / count
        val drift = base + (current - base) * progress
        val noise = Math.sin(i.toDouble() * 0.8) * (current * 0.004)
        val timeLabel = when {
            i < 8 -> "09:${30 + i * 5}"
            i < 16 -> "12:${(i - 8) * 7}"
            else -> "15:${(i - 16) * 5}"
        }
        points.add(IndexDataPoint(timeLabel, drift + noise))
    }
    points.add(IndexDataPoint("Now", current))

    return MarketIndexInfo(
        symbol = symbol,
        name = name,
        currentValue = current,
        changeAmount = change,
        changePct = pct,
        dayHigh = dayHigh,
        dayLow = dayLow,
        fiftyTwoHigh = fiftyTwoHigh,
        fiftyTwoLow = fiftyTwoLow,
        currency = currency,
        points = points
    )
}
