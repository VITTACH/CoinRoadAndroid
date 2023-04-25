package ru.mobileup.coinroad.data.storage.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migrations {

    companion object {
        val migration_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE graphs ADD COLUMN isVisible INTEGER NOT NULL DEFAULT 1")
            }
        }

        val migration_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE graphs ADD COLUMN deepLink_packageName TEXT")
                database.execSQL("ALTER TABLE graphs ADD COLUMN deepLink_id TEXT")
            }
        }

        val migration_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE graphs ADD COLUMN updateTime INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE graphs ADD COLUMN isMinMaxVisible INTEGER NOT NULL DEFAULT 1")
            }
        }

        val migration_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE graphs ADD COLUMN chartType INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE graphs ADD COLUMN isTickerVisible INTEGER NOT NULL DEFAULT 1")
                database.execSQL(
                    "CREATE TABLE alerts (id TEXT NOT NULL PRIMARY KEY, " +
                            "exchange_id TEXT NOT NULL, " +
                            "exchange_name TEXT NOT NULL, " +
                            "currencyPair_id TEXT NOT NULL, " +
                            "currencyPair_baseCurrency_id TEXT NOT NULL, " +
                            "currencyPair_baseCurrency_name TEXT NOT NULL, " +
                            "currencyPair_quoteCurrency_id TEXT NOT NULL, " +
                            "currencyPair_quoteCurrency_name TEXT NOT NULL, " +
                            "time INTEGER NOT NULL, " +
                            "price TEXT NOT NULL, " +
                            "precision INTEGER NOT NULL, " +
                            "color INTEGER NOT NULL, " +
                            "status INTEGER NOT NULL, " +
                            "message TEXT NOT NULL, " +
                            "isActive INTEGER NOT NULL DEFAULT 1)"
                )
            }
        }

        val migration_5_7: Migration = object : Migration(5, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE wallets (id TEXT NOT NULL PRIMARY KEY)")
                database.execSQL("ALTER TABLE graphs ADD COLUMN isAlertsVisible INTEGER NOT NULL DEFAULT 1")
                database.execSQL(
                    "CREATE TABLE widgets (id INTEGER NOT NULL PRIMARY KEY, " +
                            "exchange_id TEXT NOT NULL, " +
                            "exchange_name TEXT NOT NULL, " +
                            "currencyPair_id TEXT NOT NULL, " +
                            "currencyPair_baseCurrency_id TEXT NOT NULL, " +
                            "currencyPair_baseCurrency_name TEXT NOT NULL, " +
                            "currencyPair_quoteCurrency_id TEXT NOT NULL, " +
                            "currencyPair_quoteCurrency_name TEXT NOT NULL)"
                )
                database.execSQL(
                    "CREATE TABLE accounts (id TEXT NOT NULL PRIMARY KEY, " +
                            "exchange_id TEXT NOT NULL, " +
                            "exchange_name TEXT NOT NULL, " +
                            "time INTEGER NOT NULL, " +
                            "isEnabled INTEGER NOT NULL DEFAULT 1)"
                )
            }
        }
    }
}