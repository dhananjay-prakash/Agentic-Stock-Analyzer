package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.FreshnessMetadata
import com.example.domain.model.FreshnessState
import com.example.domain.model.ResearchDecision
import com.example.ui.theme.*

@Composable
fun FreshnessBadge(
    freshness: FreshnessMetadata,
    modifier: Modifier = Modifier
) {
    FreshnessBadge(state = freshness.freshnessStatus, modifier = modifier)
}

@Composable
fun FreshnessBadge(
    state: FreshnessState,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (state) {
        FreshnessState.LIVE -> Triple(SuccessGreen.copy(alpha = 0.15f), SuccessGreen, "🟢 LIVE")
        FreshnessState.FRESH -> Triple(PrimaryCyan.copy(alpha = 0.15f), PrimaryCyan, "🟢 FRESH")
        FreshnessState.LATEST_AVAILABLE -> Triple(CommitteeGold.copy(alpha = 0.15f), CommitteeGold, "🟡 LATEST OFFICIAL")
        FreshnessState.AGING -> Triple(WarningAmber.copy(alpha = 0.15f), WarningAmber, "🟠 AGING")
        FreshnessState.STALE -> Triple(DangerRed.copy(alpha = 0.15f), DangerRed, "🔴 STALE")
        FreshnessState.CURRENT_DATA_UNAVAILABLE -> Triple(DangerRed.copy(alpha = 0.25f), DangerRed, "⛔ CURRENT DATA UNAVAILABLE")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(0.5.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("freshness_badge_${state.name}")
        )
    }
}

@Composable
fun DecisionBadge(
    decision: ResearchDecision,
    confidencePct: Int,
    modifier: Modifier = Modifier
) {
    val color = Color(decision.actionColorHex)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.dp, color, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = decision.name,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            modifier = Modifier.testTag("decision_badge_${decision.name}")
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(14.dp)
                .background(color.copy(alpha = 0.4f))
        )
        Text(
            text = "$confidencePct% Conviction",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MetricStatBox(
    title: String,
    value: String,
    subtitle: String? = null,
    valueColor: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkNavySurface)
            .border(1.dp, DarkNavyOutline, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}
