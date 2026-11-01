package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {
    @Query("SELECT * FROM securities ORDER BY symbol ASC")
    fun getAllSecurities(): Flow<List<SecurityEntity>>

    @Query("SELECT * FROM securities WHERE symbol = :symbol LIMIT 1")
    suspend fun getSecurityBySymbol(symbol: String): SecurityEntity?

    @Query("SELECT * FROM securities WHERE symbol LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchSecurities(query: String): Flow<List<SecurityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurities(securities: List<SecurityEntity>)
}

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes WHERE symbol = :symbol LIMIT 1")
    fun getQuoteForSymbol(symbol: String): Flow<QuoteEntity?>

    @Query("SELECT * FROM quotes WHERE symbol = :symbol LIMIT 1")
    suspend fun getQuoteSnapshot(symbol: String): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE symbol IN (:symbols)")
    fun getQuotesForSymbols(symbols: List<String>): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE symbol IN (:symbols)")
    suspend fun getQuotesSnapshot(symbols: List<String>): List<QuoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<QuoteEntity>)
}

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_holdings ORDER BY symbol ASC")
    fun getAllHoldings(): Flow<List<PortfolioHoldingEntity>>

    @Query("SELECT * FROM portfolio_holdings WHERE symbol = :symbol LIMIT 1")
    suspend fun getHoldingBySymbol(symbol: String): PortfolioHoldingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: PortfolioHoldingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoldings(holdings: List<PortfolioHoldingEntity>)

    @Update
    suspend fun updateHolding(holding: PortfolioHoldingEntity)

    @Delete
    suspend fun deleteHolding(holding: PortfolioHoldingEntity)

    @Query("DELETE FROM portfolio_holdings WHERE symbol = :symbol")
    suspend fun deleteHoldingBySymbol(symbol: String)
}

@Dao
interface ETFDao {
    @Query("SELECT * FROM etf_constituents WHERE etfSymbol = :etfSymbol ORDER BY weightPct DESC")
    fun getConstituentsForETF(etfSymbol: String): Flow<List<ETFConstituentEntity>>

    @Query("SELECT * FROM etf_constituents WHERE etfSymbol = :etfSymbol ORDER BY weightPct DESC")
    suspend fun getConstituentsSnapshot(etfSymbol: String): List<ETFConstituentEntity>

    @Query("SELECT DISTINCT etfSymbol FROM etf_constituents")
    suspend fun getAvailableETFs(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstituents(constituents: List<ETFConstituentEntity>)
}

@Dao
interface ResearchDao {
    @Query("SELECT * FROM research_reports WHERE symbol = :symbol ORDER BY generatedAt DESC LIMIT 1")
    fun getLatestReportForSymbol(symbol: String): Flow<ResearchReportEntity?>

    @Query("SELECT * FROM research_reports ORDER BY generatedAt DESC")
    fun getAllReports(): Flow<List<ResearchReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ResearchReportEntity)
}

@Dao
interface DataHealthDao {
    @Query("SELECT * FROM data_health_records ORDER BY feedName ASC")
    fun getAllHealthRecords(): Flow<List<DataHealthEntity>>

    @Query("SELECT * FROM data_health_records")
    suspend fun getHealthRecordsSnapshot(): List<DataHealthEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: DataHealthEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecords(records: List<DataHealthEntity>)
}

@Dao
interface AlertDao {
    @Query("SELECT * FROM portfolio_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<PortfolioAlertEntity>>

    @Query("SELECT COUNT(*) FROM portfolio_alerts WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Query("SELECT * FROM portfolio_alerts WHERE category = :category ORDER BY timestamp DESC")
    fun getAlertsByCategory(category: String): Flow<List<PortfolioAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<PortfolioAlertEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PortfolioAlertEntity)

    @Query("UPDATE portfolio_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE portfolio_alerts SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM portfolio_alerts WHERE id = :id")
    suspend fun deleteAlert(id: Long)

    @Query("DELETE FROM portfolio_alerts")
    suspend fun clearAllAlerts()
}

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY pinnedAt DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE symbol = :symbol)")
    fun isPinned(symbol: String): Flow<Boolean>

    @Query("SELECT symbol FROM watchlist")
    fun getAllPinnedSymbols(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun pinSecurity(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE symbol = :symbol")
    suspend fun unpinSecurity(symbol: String)
}
