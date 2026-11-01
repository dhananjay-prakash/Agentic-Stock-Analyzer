package com.example.presentation.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PinnedWatchlistItem
import com.example.domain.model.Currency
import com.example.ui.theme.*

@Composable
fun WatchlistSideDrawer(
    pinnedItems: List<PinnedWatchlistItem>,
    onUnpin: (String) -> Unit,
    onInvestigate: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .background(DarkNavyBackground)
            .padding(16.dp)
            .testTag("watchlist_side_drawer")
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
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Watchlist",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "QUICK WATCHLIST",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkNavySurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${pinnedItems.size} PINNED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                        }
                    }
                    Text(
                        text = "Persistent rapid monitoring panel",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            IconButton(onClick = onClose, modifier = Modifier.testTag("close_watchlist_btn")) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (pinnedItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Watchlist is Empty",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap the bookmark icon on any asset in your portfolio, market indices, or search results to pin it here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(pinnedItems, key = { it.symbol }) { item ->
                    PinnedAssetCard(
                        item = item,
                        onUnpin = { onUnpin(item.symbol) },
                        onInvestigate = { onInvestigate(item.symbol) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PinnedAssetCard(
    item: PinnedWatchlistItem,
    onUnpin: () -> Unit,
    onInvestigate: () -> Unit
) {
    val currSym = if (item.currency == Currency.INR) "₹" else "$"
    val isPositive = item.changePercent >= 0
    val perfColor = if (isPositive) SuccessGreen else DangerRed
    val prefix = if (isPositive) "+" else ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pinned_card_${item.symbol}"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(DarkNavyOutline))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = item.symbol,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkNavySurfaceVariant)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.exchange.code,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                        }
                    }
                    Text(
                        text = item.securityName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }

                IconButton(onClick = onUnpin, modifier = Modifier.testTag("unpin_${item.symbol}")) {
                    Icon(
                        imageVector = Icons.Default.BookmarkRemove,
                        contentDescription = "Unpin",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "LIVE PRICE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "$currSym${"%,.2f".format(item.currentPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "DAY CHANGE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "$prefix${"%.2f".format(item.changePercent)}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = perfColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onInvestigate,
                colors = ButtonDefaults.buttonColors(containerColor = DarkNavySurfaceVariant, contentColor = PrimaryCyan),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(PrimaryCyan.copy(alpha = 0.5f))),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("investigate_pinned_${item.symbol}")
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Analyze ${item.symbol}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
