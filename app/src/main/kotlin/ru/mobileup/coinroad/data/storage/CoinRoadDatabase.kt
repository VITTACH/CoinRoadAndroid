package ru.mobileup.coinroad.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.mobileup.coinroad.data.storage.account.AccountDao
import ru.mobileup.coinroad.data.storage.account.entity.AccountDb
import ru.mobileup.coinroad.data.storage.alert.AlertDao
import ru.mobileup.coinroad.data.storage.alert.entity.AlertDb
import ru.mobileup.coinroad.data.storage.db.Migrations
import ru.mobileup.coinroad.data.storage.graph.GraphDao
import ru.mobileup.coinroad.data.storage.graph.entity.GraphDb
import ru.mobileup.coinroad.data.storage.wallet.WalletDao
import ru.mobileup.coinroad.data.storage.wallet.entity.WalletDb
import ru.mobileup.coinroad.data.storage.widget.WidgetDao
import ru.mobileup.coinroad.data.storage.widget.entity.WidgetDb

@Database(
    entities = [GraphDb::class, AlertDb::class, WidgetDb::class, WalletDb::class, AccountDb::class],
    version = 7,
    exportSchema = false
)
abstract class CoinRoadDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "dictionaries-database"

        fun create(context: Context) = Room
            .databaseBuilder(context, CoinRoadDatabase::class.java, DATABASE_NAME)
            .addMigrations(Migrations.migration_1_2)
            .addMigrations(Migrations.migration_2_3)
            .addMigrations(Migrations.migration_3_4)
            .addMigrations(Migrations.migration_4_5)
            .addMigrations(Migrations.migration_5_7)
            .build()
    }

    abstract fun getGraphDao(): GraphDao
    abstract fun getWidgetDao(): WidgetDao
    abstract fun getAlertDao(): AlertDao
    abstract fun getAccountDao(): AccountDao
    abstract fun getWalletDao(): WalletDao
}