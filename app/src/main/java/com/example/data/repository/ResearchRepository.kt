package com.example.data.repository

import com.example.data.local.db.OmniVestDatabase
import com.example.data.local.entity.ResearchReportEntity
import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ResearchRepository(
    private val database: OmniVestDatabase
) {

    fun getLatestReportForSymbol(symbol: String): Flow<ResearchReport?> {
        return database.researchDao().getLatestReportForSymbol(symbol).map { entity ->
            entity?.let { mapEntityToDomain(it) }
        }
    }

    fun getAllHistoricalReports(): Flow<List<ResearchReport>> {
        return database.researchDao().getAllReports().map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    suspend fun saveReport(report: ResearchReport) {
        val entity = ResearchReportEntity(
            reportId = report.reportId,
            symbol = report.symbol,
            securityName = report.securityName,
            decision = report.decision.name,
            confidencePct = report.confidencePct,
            investmentHorizon = report.investmentHorizon,
            thesis = report.thesis,
            currentDataStatus = report.currentDataStatus.name,
            fairValueLow = report.fairValueLow,
            fairValueHigh = report.fairValueHigh,
            currentPrice = report.currentPrice,
            entryZoneLow = report.entryZoneLow,
            entryZoneHigh = report.entryZoneHigh,
            expectedUpsidePct = report.expectedUpsidePct,
            expectedDownsidePct = report.expectedDownsidePct,
            bearCase = report.bearCase,
            technicalView = report.technicalView,
            macroView = report.macroView,
            portfolioFitNote = report.portfolioFitNote,
            whatWouldChangeThesis = report.whatWouldChangeThesis,
            catalystsJson = report.catalysts.joinToString("||"),
            risksJson = report.risks.joinToString("||"),
            evidenceJson = report.evidenceList.joinToString("||") { "${it.claim};;${it.source};;${it.supportingData};;${it.reliability.name}" },
            conflictsJson = report.conflicts.joinToString("||") { "${it.type.name};;${it.description};;${it.resolutionNote}" },
            generatedAt = report.generatedAt
        )
        database.researchDao().insertReport(entity)
    }

    private fun mapEntityToDomain(entity: ResearchReportEntity): ResearchReport {
        val catalysts = if (entity.catalystsJson.isNotBlank()) entity.catalystsJson.split("||") else emptyList()
        val risks = if (entity.risksJson.isNotBlank()) entity.risksJson.split("||") else emptyList()

        val evidenceList = if (entity.evidenceJson.isNotBlank()) {
            entity.evidenceJson.split("||").mapNotNull { line ->
                val parts = line.split(";;")
                if (parts.size >= 4) {
                    EvidenceItem(
                        claim = parts[0],
                        source = parts[1],
                        sourceType = SourceType.PRIMARY_EXCHANGE,
                        supportingData = parts[2],
                        freshnessStatus = FreshnessState.LIVE,
                        reliability = runCatching { ReliabilityScore.valueOf(parts[3]) }.getOrDefault(ReliabilityScore.HIGH),
                        timestamp = entity.generatedAt
                    )
                } else null
            }
        } else emptyList()

        val conflicts = if (entity.conflictsJson.isNotBlank()) {
            entity.conflictsJson.split("||").mapNotNull { line ->
                val parts = line.split(";;")
                if (parts.size >= 3) {
                    ConflictItem(
                        type = runCatching { ConflictType.valueOf(parts[0]) }.getOrDefault(ConflictType.VALUATION_VS_MOMENTUM),
                        description = parts[1],
                        agentSignals = emptyMap(),
                        resolutionNote = parts[2]
                    )
                } else null
            }
        } else emptyList()

        return ResearchReport(
            reportId = entity.reportId,
            symbol = entity.symbol,
            securityName = entity.securityName,
            decision = runCatching { ResearchDecision.valueOf(entity.decision) }.getOrDefault(ResearchDecision.HOLD),
            confidencePct = entity.confidencePct,
            investmentHorizon = entity.investmentHorizon,
            thesis = entity.thesis,
            currentDataStatus = runCatching { FreshnessState.valueOf(entity.currentDataStatus) }.getOrDefault(FreshnessState.LIVE),
            fairValueLow = entity.fairValueLow,
            fairValueHigh = entity.fairValueHigh,
            currentPrice = entity.currentPrice,
            entryZoneLow = entity.entryZoneLow,
            entryZoneHigh = entity.entryZoneHigh,
            expectedUpsidePct = entity.expectedUpsidePct,
            expectedDownsidePct = entity.expectedDownsidePct,
            catalysts = catalysts,
            risks = risks,
            bearCase = entity.bearCase,
            technicalView = entity.technicalView,
            macroView = entity.macroView,
            portfolioFitNote = entity.portfolioFitNote,
            whatWouldChangeThesis = entity.whatWouldChangeThesis,
            evidenceList = evidenceList,
            conflicts = conflicts,
            generatedAt = entity.generatedAt
        )
    }
}
