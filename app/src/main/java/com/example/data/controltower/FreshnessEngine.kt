package com.example.data.controltower

import com.example.domain.model.DataClass
import com.example.domain.model.FreshnessMetadata
import com.example.domain.model.FreshnessState
import com.example.domain.model.ReliabilityScore
import com.example.domain.model.SourceType

object FreshnessEngine {

    /**
     * Evaluates data freshness based on specific DataClass policies:
     * - Intraday prices: < 15m LIVE, < 60m FRESH, < 24h AGING, >= 24h STALE
     * - Daily close: latest session (up to 72h on weekends/holidays) FRESH, else STALE
     * - Financial reports: up to 120 days LATEST_AVAILABLE, > 150 days AGING
     * - Macro releases: up to 45 days LATEST_AVAILABLE
     * - News: < 6h FRESH, < 24h AGING, >= 24h STALE
     */
    fun evaluateFreshness(
        dataClass: DataClass,
        publishedAt: Long,
        retrievedAt: Long,
        currentTime: Long = System.currentTimeMillis()
    ): FreshnessState {
        val ageMs = currentTime - publishedAt
        val ageMinutes = ageMs / (1000 * 60)

        return when (dataClass) {
            DataClass.INTRADAY_PRICE -> {
                when {
                    ageMinutes <= 15 -> FreshnessState.LIVE
                    ageMinutes <= 60 -> FreshnessState.FRESH
                    ageMinutes <= 1440 -> FreshnessState.AGING
                    else -> FreshnessState.STALE
                }
            }
            DataClass.DAILY_CLOSE -> {
                when {
                    ageMinutes <= 1440 -> FreshnessState.FRESH
                    ageMinutes <= 4320 -> FreshnessState.FRESH // Weekend buffer (72 hours)
                    else -> FreshnessState.STALE
                }
            }
            DataClass.NEWS_EVENT -> {
                when {
                    ageMinutes <= 360 -> FreshnessState.FRESH // 6 hours
                    ageMinutes <= 1440 -> FreshnessState.AGING
                    else -> FreshnessState.STALE
                }
            }
            DataClass.FINANCIAL_STATEMENT -> {
                val ageDays = ageMinutes / (60 * 24)
                when {
                    ageDays <= 120 -> FreshnessState.LATEST_AVAILABLE
                    ageDays <= 180 -> FreshnessState.AGING
                    else -> FreshnessState.STALE
                }
            }
            DataClass.MACRO_INDICATOR -> {
                val ageDays = ageMinutes / (60 * 24)
                when {
                    ageDays <= 45 -> FreshnessState.LATEST_AVAILABLE
                    ageDays <= 90 -> FreshnessState.AGING
                    else -> FreshnessState.STALE
                }
            }
            DataClass.ETF_CONSTITUENTS -> {
                val ageDays = ageMinutes / (60 * 24)
                when {
                    ageDays <= 7 -> FreshnessState.FRESH
                    ageDays <= 30 -> FreshnessState.AGING
                    else -> FreshnessState.STALE
                }
            }
            DataClass.ETF_NAV, DataClass.MUTUAL_FUND_NAV -> {
                when {
                    ageMinutes <= 1440 -> FreshnessState.FRESH
                    ageMinutes <= 4320 -> FreshnessState.LATEST_AVAILABLE // Weekend buffer
                    else -> FreshnessState.STALE
                }
            }
            DataClass.MACRO_CENTRAL_BANK -> {
                val ageDays = ageMinutes / (60 * 24)
                when {
                    ageDays <= 60 -> FreshnessState.LATEST_AVAILABLE
                    ageDays <= 90 -> FreshnessState.AGING
                    else -> FreshnessState.STALE
                }
            }
            DataClass.TREASURY_YIELD -> {
                when {
                    ageMinutes <= 15 -> FreshnessState.LIVE
                    ageMinutes <= 60 -> FreshnessState.FRESH
                    ageMinutes <= 1440 -> FreshnessState.AGING
                    else -> FreshnessState.STALE
                }
            }
            DataClass.COMPANY_NEWS -> {
                when {
                    ageMinutes <= 360 -> FreshnessState.FRESH // 6 hours
                    ageMinutes <= 1440 -> FreshnessState.AGING
                    else -> FreshnessState.STALE
                }
            }
        }
    }

    /**
     * Gating check: returns true if the data item is legally allowed to be fed into
     * an investment committee decision under the 24-hour freshness rule.
     */
    fun isAdmissibleForDecision(metadata: FreshnessMetadata): Boolean {
        return metadata.freshnessStatus.isPermittedForDecisions &&
                metadata.reliability != ReliabilityScore.UNVERIFIED
    }
}
