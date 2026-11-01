package com.example.data.local.db

import com.example.data.local.entity.*
import com.example.domain.model.DataClass
import com.example.domain.model.FreshnessState
import com.example.domain.model.ReliabilityScore
import com.example.domain.model.SourceType

object DatabaseSeeder {

    suspend fun seedInitialData(database: OmniVestDatabase) {
        val now = System.currentTimeMillis()

        // 1. Initial Securities Catalog
        val initialSecurities = listOf(
            SecurityEntity("NVDA", "NVIDIA Corporation", "EQUITY_US", "NASDAQ", "USD", "Technology", "Semiconductors"),
            SecurityEntity("MSFT", "Microsoft Corporation", "EQUITY_US", "NASDAQ", "USD", "Technology", "Software Infrastructure"),
            SecurityEntity("GOOGL", "Alphabet Inc.", "EQUITY_US", "NASDAQ", "USD", "Technology", "Internet Content & Info"),
            SecurityEntity("AMZN", "Amazon.com Inc.", "EQUITY_US", "NASDAQ", "USD", "Consumer Cyclical", "Internet Retail"),
            SecurityEntity("AAPL", "Apple Inc.", "EQUITY_US", "NASDAQ", "USD", "Technology", "Consumer Electronics"),
            SecurityEntity("QQQM", "Invesco NASDAQ 100 ETF", "ETF", "NASDAQ", "USD", "Technology", "Index ETF"),
            SecurityEntity("VOO", "Vanguard S&P 500 ETF", "ETF", "NYSE", "USD", "Blended Large Cap", "Index ETF"),
            SecurityEntity("SCHG", "Schwab U.S. Large-Cap Growth ETF", "ETF", "NYSE", "USD", "Large Cap Growth", "Growth ETF"),
            SecurityEntity("SMH", "VanEck Semiconductor ETF", "ETF", "NASDAQ", "USD", "Technology", "Sector ETF"),
            SecurityEntity("RELIANCE.NS", "Reliance Industries Limited", "EQUITY_INDIA", "NSE", "INR", "Energy & Conglomerate", "Oil, Gas & Telecom"),
            SecurityEntity("TCS.NS", "Tata Consultancy Services", "EQUITY_INDIA", "NSE", "INR", "Technology", "IT Services"),
            SecurityEntity("HDFCBANK.NS", "HDFC Bank Limited", "EQUITY_INDIA", "NSE", "INR", "Financial Services", "Private Banking"),
            SecurityEntity("INFY.NS", "Infosys Limited", "EQUITY_INDIA", "NSE", "INR", "Technology", "IT Services"),
            SecurityEntity("GLD", "SPDR Gold Shares", "COMMODITY", "NYSE", "USD", "Commodities", "Physical Gold Trust")
        )
        database.securityDao().insertSecurities(initialSecurities)

        // 2. Initial Live/Fresh Quotes
        val initialQuotes = listOf(
            QuoteEntity(
                symbol = "NVDA", price = 142.50, changeAmount = 3.80, changePercent = 2.74,
                dayOpen = 139.10, dayHigh = 143.20, dayLow = 138.80, volume = 48500000,
                fiftyTwoWeekHigh = 144.42, fiftyTwoWeekLow = 45.10, peRatio = 54.2, marketCap = 3500.0,
                source = "Market Tape (NASDAQ)", sourceType = SourceType.PRIMARY_EXCHANGE.name,
                retrievedAt = now, publishedAt = now - 60000, effectiveAt = now,
                freshnessStatus = FreshnessState.LIVE.name, reliability = ReliabilityScore.HIGH.name, confidence = 0.98f
            ),
            QuoteEntity(
                symbol = "MSFT", price = 432.10, changeAmount = -1.20, changePercent = -0.28,
                dayOpen = 434.00, dayHigh = 435.50, dayLow = 430.80, volume = 19200000,
                fiftyTwoWeekHigh = 468.35, fiftyTwoWeekLow = 366.50, peRatio = 36.8, marketCap = 3210.0,
                source = "Market Tape (NASDAQ)", sourceType = SourceType.PRIMARY_EXCHANGE.name,
                retrievedAt = now, publishedAt = now - 60000, effectiveAt = now,
                freshnessStatus = FreshnessState.LIVE.name, reliability = ReliabilityScore.HIGH.name, confidence = 0.98f
            ),
            QuoteEntity(
                symbol = "GOOGL", price = 178.60, changeAmount = 1.95, changePercent = 1.10,
                dayOpen = 177.00, dayHigh = 179.20, dayLow = 176.40, volume = 22100000,
                fiftyTwoWeekHigh = 191.75, fiftyTwoWeekLow = 129.40, peRatio = 24.5, marketCap = 2210.0,
                source = "Market Tape (NASDAQ)", sourceType = SourceType.PRIMARY_EXCHANGE.name,
                retrievedAt = now, publishedAt = now - 60000, effectiveAt = now,
                freshnessStatus = FreshnessState.LIVE.name, reliability = ReliabilityScore.HIGH.name, confidence = 0.98f
            ),
            QuoteEntity(
                symbol = "QQQM", price = 212.40, changeAmount = 2.10, changePercent = 1.00,
                dayOpen = 210.80, dayHigh = 213.10, dayLow = 210.20, volume = 4200000,
                fiftyTwoWeekHigh = 214.50, fiftyTwoWeekLow = 162.30, peRatio = 31.4, marketCap = 31500.0,
                source = "Market Tape (NASDAQ)", sourceType = SourceType.PRIMARY_EXCHANGE.name,
                retrievedAt = now, publishedAt = now - 60000, effectiveAt = now,
                freshnessStatus = FreshnessState.LIVE.name, reliability = ReliabilityScore.HIGH.name, confidence = 0.99f
            ),
            QuoteEntity(
                symbol = "VOO", price = 538.90, changeAmount = 3.40, changePercent = 0.63,
                dayOpen = 536.20, dayHigh = 539.80, dayLow = 535.90, volume = 5800000,
                fiftyTwoWeekHigh = 542.10, fiftyTwoWeekLow = 420.50, peRatio = 27.2, marketCap = 512000.0,
                source = "Market Tape (NYSE)", sourceType = SourceType.PRIMARY_EXCHANGE.name,
                retrievedAt = now, publishedAt = now - 60000, effectiveAt = now,
                freshnessStatus = FreshnessState.LIVE.name, reliability = ReliabilityScore.HIGH.name, confidence = 0.99f
            ),
            QuoteEntity(
                symbol = "RELIANCE.NS", price = 2745.00, changeAmount = 32.50, changePercent = 1.20,
                dayOpen = 2720.00, dayHigh = 2755.00, dayLow = 2715.00, volume = 6400000,
                fiftyTwoWeekHigh = 3217.90, fiftyTwoWeekLow = 2221.05, peRatio = 26.8, marketCap = 18500.0,
                source = "National Stock Exchange (NSE)", sourceType = SourceType.PRIMARY_EXCHANGE.name,
                retrievedAt = now, publishedAt = now - 60000, effectiveAt = now,
                freshnessStatus = FreshnessState.LIVE.name, reliability = ReliabilityScore.HIGH.name, confidence = 0.98f
            ),
            QuoteEntity(
                symbol = "TCS.NS", price = 4050.00, changeAmount = -18.00, changePercent = -0.44,
                dayOpen = 4070.00, dayHigh = 4085.00, dayLow = 4030.00, volume = 1800000,
                fiftyTwoWeekHigh = 4585.90, fiftyTwoWeekLow = 3311.00, peRatio = 29.5, marketCap = 14600.0,
                source = "National Stock Exchange (NSE)", sourceType = SourceType.PRIMARY_EXCHANGE.name,
                retrievedAt = now, publishedAt = now - 60000, effectiveAt = now,
                freshnessStatus = FreshnessState.LIVE.name, reliability = ReliabilityScore.HIGH.name, confidence = 0.98f
            ),
            QuoteEntity(
                symbol = "GLD", price = 254.20, changeAmount = 1.15, changePercent = 0.45,
                dayOpen = 253.50, dayHigh = 255.10, dayLow = 253.20, volume = 6200000,
                fiftyTwoWeekHigh = 258.40, fiftyTwoWeekLow = 182.50, peRatio = null, marketCap = 72000.0,
                source = "Market Tape (NYSE Arca)", sourceType = SourceType.PRIMARY_EXCHANGE.name,
                retrievedAt = now, publishedAt = now - 60000, effectiveAt = now,
                freshnessStatus = FreshnessState.LIVE.name, reliability = ReliabilityScore.HIGH.name, confidence = 0.98f
            )
        )
        database.quoteDao().insertQuotes(initialQuotes)

        // 3. ETF Constituents for Look-Through Calculations
        val etfConstituents = listOf(
            // QQQM Constituents
            ETFConstituentEntity("QQQM", "AAPL", "Apple Inc.", 8.75, "Technology"),
            ETFConstituentEntity("QQQM", "MSFT", "Microsoft Corporation", 8.40, "Technology"),
            ETFConstituentEntity("QQQM", "NVDA", "NVIDIA Corporation", 7.85, "Technology"),
            ETFConstituentEntity("QQQM", "AMZN", "Amazon.com Inc.", 5.30, "Consumer Cyclical"),
            ETFConstituentEntity("QQQM", "GOOGL", "Alphabet Inc. (Class A)", 2.85, "Technology"),
            ETFConstituentEntity("QQQM", "META", "Meta Platforms Inc.", 4.90, "Technology"),
            ETFConstituentEntity("QQQM", "TSLA", "Tesla Inc.", 3.10, "Consumer Cyclical"),

            // VOO Constituents
            ETFConstituentEntity("VOO", "MSFT", "Microsoft Corporation", 7.10, "Technology"),
            ETFConstituentEntity("VOO", "AAPL", "Apple Inc.", 6.95, "Technology"),
            ETFConstituentEntity("VOO", "NVDA", "NVIDIA Corporation", 6.45, "Technology"),
            ETFConstituentEntity("VOO", "AMZN", "Amazon.com Inc.", 3.75, "Consumer Cyclical"),
            ETFConstituentEntity("VOO", "GOOGL", "Alphabet Inc.", 2.25, "Technology"),
            ETFConstituentEntity("VOO", "META", "Meta Platforms Inc.", 2.50, "Technology"),
            ETFConstituentEntity("VOO", "BRK.B", "Berkshire Hathaway Inc.", 1.70, "Financial Services"),
            ETFConstituentEntity("VOO", "JPM", "JPMorgan Chase & Co.", 1.35, "Financial Services"),

            // SCHG Constituents
            ETFConstituentEntity("SCHG", "MSFT", "Microsoft Corporation", 12.10, "Technology"),
            ETFConstituentEntity("SCHG", "AAPL", "Apple Inc.", 11.80, "Technology"),
            ETFConstituentEntity("SCHG", "NVDA", "NVIDIA Corporation", 10.95, "Technology"),
            ETFConstituentEntity("SCHG", "AMZN", "Amazon.com Inc.", 6.20, "Consumer Cyclical"),
            ETFConstituentEntity("SCHG", "GOOGL", "Alphabet Inc.", 4.10, "Technology"),

            // SMH Constituents
            ETFConstituentEntity("SMH", "NVDA", "NVIDIA Corporation", 20.40, "Technology"),
            ETFConstituentEntity("SMH", "TSM", "Taiwan Semiconductor", 13.20, "Technology"),
            ETFConstituentEntity("SMH", "AVGO", "Broadcom Inc.", 7.80, "Technology"),
            ETFConstituentEntity("SMH", "ASML", "ASML Holding NV", 4.90, "Technology")
        )
        database.etfDao().insertConstituents(etfConstituents)

        // 4. User's Real Portfolio Holdings (Demonstrating Cross-Asset: US Equity, India Equity, US ETF)
        val initialPortfolio = listOf(
            PortfolioHoldingEntity(
                symbol = "QQQM", securityName = "Invesco NASDAQ 100 ETF", assetClass = "ETF",
                exchange = "NASDAQ", currency = "USD", sharesQuantity = 100.0, averageCost = 185.00, sector = "Technology"
            ),
            PortfolioHoldingEntity(
                symbol = "VOO", securityName = "Vanguard S&P 500 ETF", assetClass = "ETF",
                exchange = "NYSE", currency = "USD", sharesQuantity = 50.0, averageCost = 480.00, sector = "Blended Large Cap"
            ),
            PortfolioHoldingEntity(
                symbol = "NVDA", securityName = "NVIDIA Corporation", assetClass = "EQUITY_US",
                exchange = "NASDAQ", currency = "USD", sharesQuantity = 80.0, averageCost = 112.50, sector = "Technology"
            ),
            PortfolioHoldingEntity(
                symbol = "MSFT", securityName = "Microsoft Corporation", assetClass = "EQUITY_US",
                exchange = "NASDAQ", currency = "USD", sharesQuantity = 30.0, averageCost = 390.00, sector = "Technology"
            ),
            PortfolioHoldingEntity(
                symbol = "RELIANCE.NS", securityName = "Reliance Industries Ltd", assetClass = "EQUITY_INDIA",
                exchange = "NSE", currency = "INR", sharesQuantity = 150.0, averageCost = 2520.00, sector = "Energy & Conglomerate"
            )
        )
        database.portfolioDao().insertHoldings(initialPortfolio)

        // 5. Data Health Dashboard Initial Feeds
        val healthRecords = listOf(
            DataHealthEntity("US Market Tape", "Direct Exchange Stream (NASDAQ/NYSE)", now, FreshnessState.LIVE.name, ReliabilityScore.HIGH.name, 5, 0),
            DataHealthEntity("Indian Market Tape", "National Stock Exchange of India (NSE)", now, FreshnessState.LIVE.name, ReliabilityScore.HIGH.name, 2, 0),
            DataHealthEntity("ETF Constituents", "Fund Issuer Transparency Disclosures", now - 3600000, FreshnessState.FRESH.name, ReliabilityScore.HIGH.name, 4, 0),
            DataHealthEntity("Company Fundamentals", "SEC EDGAR / MCA Corporate Filings", now - 86400000, FreshnessState.LATEST_AVAILABLE.name, ReliabilityScore.HIGH.name, 4, 0),
            DataHealthEntity("Macroeconomics", "Federal Reserve & Reserve Bank of India", now - 172800000, FreshnessState.LATEST_AVAILABLE.name, ReliabilityScore.HIGH.name, 6, 0),
            DataHealthEntity("Financial News & Filings", "Institutional News Feed & Dow Jones", now - 1800000, FreshnessState.FRESH.name, ReliabilityScore.HIGH.name, 8, 0)
        )
        database.dataHealthDao().insertHealthRecords(healthRecords)

        // 6. Portfolio Alerts & Threshold Breaches Feed
        val initialAlerts = listOf(
            PortfolioAlertEntity(
                symbol = "NVDA",
                title = "Concentration Threshold Exceeded",
                message = "Effective look-through exposure reached 17.8% of aggregate portfolio. Max single-security allocation policy limit is 15.0%. Direct equity holding plus QQQM & VOO constituent weight triggers rebalance alert.",
                severity = "CRITICAL",
                category = "CONCENTRATION",
                triggerMetric = "Effective Weight: 17.8% (Limit: 15.0%)",
                timestamp = now - 900000, // 15m ago
                isRead = false,
                actionableSymbol = "NVDA"
            ),
            PortfolioAlertEntity(
                symbol = "VOO",
                title = "Valuation Entry Zone Reached",
                message = "Vanguard S&P 500 ETF traded at $538.90, entering the Investment Committee approved accumulation range ($520.00 – $542.00). Margin of safety verified by Valuation Agent.",
                severity = "OPPORTUNITY",
                category = "VALUATION",
                triggerMetric = "Price: $538.90 (Entry Ceiling: $542.00)",
                timestamp = now - 3600000, // 1h ago
                isRead = false,
                actionableSymbol = "VOO"
            ),
            PortfolioAlertEntity(
                symbol = "RELIANCE.NS",
                title = "Technical Support Test & Momentum Warning",
                message = "14-day RSI declined to 38.2 while price tested the 50-day EMA support at ₹2,715. Technical Agent flagged deceleration in buying volume.",
                severity = "WARNING",
                category = "TECHNICAL",
                triggerMetric = "RSI: 38.2 (50 EMA: ₹2,715)",
                timestamp = now - 7200000, // 2h ago
                isRead = false,
                actionableSymbol = "RELIANCE.NS"
            ),
            PortfolioAlertEntity(
                symbol = "QQQM",
                title = "ETF Look-Through Overlap Alert",
                message = "Holding both QQQM and VOO creates a 46.2% constituent overlap. Top 5 mega-cap tech holdings are duplicated across both funds.",
                severity = "WARNING",
                category = "CONCENTRATION",
                triggerMetric = "Overlap Coefficient: 46.2%",
                timestamp = now - 14400000, // 4h ago
                isRead = true,
                actionableSymbol = "QQQM"
            ),
            PortfolioAlertEntity(
                symbol = "MSFT",
                title = "Quarterly FCF & Capex Disclosure",
                message = "SEC 10-Q filing audited by Fundamental Agent: Free Cash Flow reached $19.3B (+16% YoY). Net Cash position remains intact.",
                severity = "INFO",
                category = "THESIS",
                triggerMetric = "FCF: $19.3B | Net Cash Positive",
                timestamp = now - 28800000, // 8h ago
                isRead = true,
                actionableSymbol = "MSFT"
            )
        )
        database.alertDao().insertAlerts(initialAlerts)

        // 7. Initial Pinned Quick Watchlist
        val initialWatchlist = listOf(
            WatchlistEntity("NVDA", "NVIDIA Corporation", "EQUITY_US", "NASDAQ", "USD", "Technology", now),
            WatchlistEntity("QQQM", "Invesco NASDAQ 100 ETF", "ETF", "NASDAQ", "USD", "Technology", now),
            WatchlistEntity("RELIANCE.NS", "Reliance Industries Ltd", "EQUITY_INDIA", "NSE", "INR", "Energy & Conglomerate", now),
            WatchlistEntity("GLD", "SPDR Gold Shares", "COMMODITY", "NYSE", "USD", "Commodities", now)
        )
        initialWatchlist.forEach { database.watchlistDao().pinSecurity(it) }
    }
}
