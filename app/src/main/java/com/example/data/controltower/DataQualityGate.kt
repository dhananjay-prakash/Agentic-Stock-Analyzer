package com.example.data.controltower

import com.example.domain.model.CompanyFinancials
import com.example.domain.model.ReliabilityScore
import com.example.domain.model.SecurityQuote

sealed class QualityValidationResult {
    data object Valid : QualityValidationResult()
    data class Rejected(val reason: String) : QualityValidationResult()
}

object DataQualityGate {

    /**
     * Validates that incoming market quotes meet strict institutional numerical standards.
     * Prevents NaN, negative prices, future timestamps, or unverified hallucinations.
     */
    fun validateQuote(quote: SecurityQuote): QualityValidationResult {
        if (quote.price <= 0.0 || quote.price.isNaN() || quote.price.isInfinite()) {
            return QualityValidationResult.Rejected("Invalid price metric: ${quote.price}")
        }

        if (quote.volume < 0) {
            return QualityValidationResult.Rejected("Negative volume reported: ${quote.volume}")
        }

        if (quote.dayLow > quote.dayHigh && quote.dayHigh > 0.0) {
            return QualityValidationResult.Rejected("Inverted session bounds: DayLow (${quote.dayLow}) > DayHigh (${quote.dayHigh})")
        }

        if (quote.changePercent < -99.9 || quote.changePercent > 5000.0) {
            return QualityValidationResult.Rejected("Anomalous percentage change detected: ${quote.changePercent}%")
        }

        val now = System.currentTimeMillis()
        if (quote.freshness.publishedAt > now + 60000) {
            return QualityValidationResult.Rejected("Future timestamp detected: published at ${quote.freshness.publishedAt} vs now $now")
        }

        if (quote.freshness.reliability == ReliabilityScore.UNVERIFIED) {
            return QualityValidationResult.Rejected("Unverified data source rejected by Quality Gate.")
        }

        return QualityValidationResult.Valid
    }

    /**
     * Validates corporate financial statements.
     */
    fun validateFinancials(financials: CompanyFinancials): QualityValidationResult {
        if (financials.revenueBillion.isNaN() || financials.netIncomeBillion.isNaN()) {
            return QualityValidationResult.Rejected("Corrupted numerical fields in financial statement.")
        }

        if (financials.totalCashBillion < 0.0 || financials.totalDebtBillion < 0.0) {
            return QualityValidationResult.Rejected("Negative cash or debt balance is mathematically impossible.")
        }

        val now = System.currentTimeMillis()
        if (financials.filingDate > now + 86400000) {
            return QualityValidationResult.Rejected("Future filing date: ${financials.filingDate}")
        }

        return QualityValidationResult.Valid
    }
}
