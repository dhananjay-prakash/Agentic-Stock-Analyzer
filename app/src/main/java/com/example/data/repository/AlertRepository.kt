package com.example.data.repository

import com.example.data.local.db.OmniVestDatabase
import com.example.data.local.entity.PortfolioAlertEntity
import kotlinx.coroutines.flow.Flow

class AlertRepository(
    private val database: OmniVestDatabase
) {

    fun observeAllAlerts(): Flow<List<PortfolioAlertEntity>> {
        return database.alertDao().getAllAlerts()
    }

    fun observeUnreadCount(): Flow<Int> {
        return database.alertDao().getUnreadCount()
    }

    fun observeAlertsByCategory(category: String): Flow<List<PortfolioAlertEntity>> {
        return if (category == "ALL") {
            database.alertDao().getAllAlerts()
        } else {
            database.alertDao().getAlertsByCategory(category)
        }
    }

    suspend fun markAsRead(id: Long) {
        database.alertDao().markAsRead(id)
    }

    suspend fun markAllAsRead() {
        database.alertDao().markAllAsRead()
    }

    suspend fun dismissAlert(id: Long) {
        database.alertDao().deleteAlert(id)
    }

    suspend fun insertAlert(alert: PortfolioAlertEntity) {
        database.alertDao().insertAlert(alert)
    }
}
