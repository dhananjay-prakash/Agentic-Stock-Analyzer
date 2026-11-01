package com.example.presentation.news

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Newspaper
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
import com.example.ui.theme.*

data class PortfolioNewsArticle(
    val id: String,
    val symbol: String,
    val securityName: String,
    val title: String,
    val summary: String,
    val publisher: String,
    val publishedAt: Long,
    val sentiment: String, // BULLISH, NEUTRAL, BEARISH
    val impactMagnitude: String // HIGH, MEDIUM, LOW
)

@Composable
fun MarketNewsFeed(
    onInvestigateSymbol: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSymbolFilter by remember { mutableStateOf("ALL") }
    var selectedSentimentFilter by remember { mutableStateOf("ALL") }

    val now = remember { System.currentTimeMillis() }

    val mockArticles = remember {
        listOf(
            PortfolioNewsArticle(
                id = "N1",
                symbol = "NVDA",
                securityName = "NVIDIA Corporation",
                title = "Hyperscale Cloud Capex Revisions Boost Advanced GPU Data Center Allocations",
                summary = "Major global cloud providers raised aggregate quarterly AI infrastructure investment guidance by 18%, citing sustained multi-year generative model training and inferencing workloads.",
                publisher = "Dow Jones Institutional Wire",
                publishedAt = now - 900000, // 15m ago
                sentiment = "BULLISH",
                impactMagnitude = "HIGH"
            ),
            PortfolioNewsArticle(
                id = "N2",
                symbol = "QQQM",
                securityName = "Invesco NASDAQ 100 ETF",
                title = "Nasdaq 100 Reconstitution: Quarterly Weight Capping Implemented to Limit Mega-Cap Distortion",
                summary = "Index governance committee announces routine rebalancing to prevent top 5 constituents from exceeding 40% aggregate index weight, ensuring broader technology market diversification.",
                publisher = "Exchange Regulatory Disclosure",
                publishedAt = now - 2400000, // 40m ago
                sentiment = "NEUTRAL",
                impactMagnitude = "MEDIUM"
            ),
            PortfolioNewsArticle(
                id = "N3",
                symbol = "RELIANCE.NS",
                securityName = "Reliance Industries Ltd",
                title = "Jio Infocomm Unveils Enterprise AI Cloud Platform in Collaboration with Domestic Data Centers",
                summary = "Telecom and digital services subsidiary announces enterprise cloud compute expansion across tier-1 Indian metros, accelerating high-margin 5G ARPU monetization.",
                publisher = "BSE Corporate Announcements",
                publishedAt = now - 4500000, // 1h 15m ago
                sentiment = "BULLISH",
                impactMagnitude = "HIGH"
            ),
            PortfolioNewsArticle(
                id = "N4",
                symbol = "VOO",
                securityName = "Vanguard S&P 500 ETF",
                title = "Federal Reserve Holds Policy Stance as Core PCE Deflator Moderates to 2.4%",
                summary = "FOMC minutes indicate gradual rate normalization trajectory; broad equity earnings multiples remain supported across large-cap US benchmark index constituents.",
                publisher = "Federal Reserve Statistical Release",
                publishedAt = now - 9000000, // 2h 30m ago
                sentiment = "BULLISH",
                impactMagnitude = "MEDIUM"
            ),
            PortfolioNewsArticle(
                id = "N5",
                symbol = "MSFT",
                securityName = "Microsoft Corporation",
                title = "Commercial Cloud Revenue Growth Outpaces Consensus as Copilot Seat Expansion Doubles",
                summary = "Office 365 commercial average revenue per user grew 15% YoY with rapid adoption across enterprise Fortune 500 accounts; Free Cash Flow generation reached $19.3B.",
                publisher = "SEC Form 10-Q Filing",
                publishedAt = now - 18000000, // 5h ago
                sentiment = "BULLISH",
                impactMagnitude = "HIGH"
            ),
            PortfolioNewsArticle(
                id = "N6",
                symbol = "GLD",
                securityName = "SPDR Gold Shares",
                title = "Global Central Bank Gold Accumulation Reaches Third-Quarter High Amid FX Reserve Diversification",
                summary = "Official institutions added 186 tonnes of physical gold in aggregate during Q3; institutional physical ETF holdings noted moderate net inflows.",
                publisher = "World Gold Council Statistics",
                publishedAt = now - 36000000, // 10h ago
                sentiment = "BULLISH",
                impactMagnitude = "MEDIUM"
            )
        )
    }

    val filteredArticles = remember(selectedSymbolFilter, selectedSentimentFilter) {
        mockArticles.filter { art ->
            val matchSymbol = selectedSymbolFilter == "ALL" || art.symbol == selectedSymbolFilter
            val matchSentiment = selectedSentimentFilter == "ALL" || art.sentiment == selectedSentimentFilter
            matchSymbol && matchSentiment
        }
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
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PORTFOLIO INTELLIGENCE WIRE",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Market News Feed",
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
                            text = "MOCK NEWS SERVICE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber
                        )
                    }
                }
                Text(
                    text = "Real-time regulatory disclosures and corporate wire headlines for assets in your active portfolio.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // Symbol Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "FILTER BY PORTFOLIO ASSET", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val symbols = listOf("ALL", "NVDA", "QQQM", "RELIANCE.NS", "VOO", "MSFT", "GLD")
                    items(symbols) { sym ->
                        FilterChip(
                            selected = selectedSymbolFilter == sym,
                            onClick = { selectedSymbolFilter = sym },
                            label = { Text(sym, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan,
                                selectedLabelColor = DarkNavyBackground,
                                containerColor = DarkNavySurfaceVariant,
                                labelColor = TextPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedSymbolFilter == sym,
                                borderColor = DarkNavyOutline
                            ),
                            modifier = Modifier.testTag("news_filter_symbol_$sym")
                        )
                    }
                }
            }
        }

        // News Cards List
        items(filteredArticles, key = { it.id }) { article ->
            NewsArticleCard(
                article = article,
                onInvestigate = { onInvestigateSymbol(article.symbol) }
            )
        }
    }
}

@Composable
private fun NewsArticleCard(
    article: PortfolioNewsArticle,
    onInvestigate: () -> Unit
) {
    val (sentimentColor, sentimentText) = when (article.sentiment) {
        "BULLISH" -> SuccessGreen to "BULLISH"
        "BEARISH" -> DangerRed to "BEARISH"
        else -> TextSecondary to "NEUTRAL"
    }

    val (impactColor, impactText) = when (article.impactMagnitude) {
        "HIGH" -> DangerRed to "HIGH IMPACT"
        "MEDIUM" -> WarningAmber to "MEDIUM"
        else -> TextMuted to "LOW"
    }

    val timeAgo = remember(article.publishedAt) {
        val diffMs = System.currentTimeMillis() - article.publishedAt
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
            .testTag("news_card_${article.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(DarkNavyOutline))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header Row: Symbol Tag, Sentiment Badge, Publisher, Time ago
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkNavySurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = article.symbol, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(sentimentColor.copy(alpha = 0.15f))
                            .border(0.5.dp, sentimentColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = sentimentText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = sentimentColor)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkNavyOutline.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = impactText, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = impactColor)
                    }
                }

                Text(text = timeAgo, fontSize = 11.sp, color = TextMuted)
            }

            // Headline
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )

            // Summary
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            // Publisher wire & Investigate Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${article.publisher}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )

                TextButton(
                    onClick = onInvestigate,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("investigate_news_${article.symbol}")
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Analyze Impact", fontSize = 11.sp, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
