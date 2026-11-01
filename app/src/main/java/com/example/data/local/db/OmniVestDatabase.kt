package com.example.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SecurityEntity::class,
        QuoteEntity::class,
        FinancialReportEntity::class,
        ETFConstituentEntity::class,
        PortfolioHoldingEntity::class,
        ResearchReportEntity::class,
        DataHealthEntity::class,
        PortfolioAlertEntity::class,
        WatchlistEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class OmniVestDatabase : RoomDatabase() {

    abstract fun securityDao(): SecurityDao
    abstract fun quoteDao(): QuoteDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun etfDao(): ETFDao
    abstract fun researchDao(): ResearchDao
    abstract fun dataHealthDao(): DataHealthDao
    abstract fun alertDao(): AlertDao
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        @Volatile
        private var INSTANCE: OmniVestDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): OmniVestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OmniVestDatabase::class.java,
                    "omnivest_local.db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        DatabaseSeeder.seedInitialData(database)
                    }
                }
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        DatabaseSeeder.seedInitialData(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.securityDao().getSecurityBySymbol("NVDA") == null) {
                            DatabaseSeeder.seedInitialData(database)
                        }
                    }
                }
            }
        }
    }
}
