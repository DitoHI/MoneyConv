package com.hafizhnotes.currencyconversion.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hafizhnotes.currencyconversion.data.constant.DBConstant
import com.hafizhnotes.currencyconversion.data.helper.TimestampConverter
import com.hafizhnotes.currencyconversion.room.dao.CurrencyListRoomDao
import com.hafizhnotes.currencyconversion.room.dao.CurrencyLiveRoomDao
import com.hafizhnotes.currencyconversion.room.vo.CurrencyListRoomResponse
import com.hafizhnotes.currencyconversion.room.vo.CurrencyLiveRoomResponse
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [CurrencyLiveRoomResponse::class, CurrencyListRoomResponse::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimestampConverter::class)
abstract class CurrencyLayerDatabase : RoomDatabase() {

    abstract fun currencyLiveRoomDao(): CurrencyLiveRoomDao

    abstract fun currencyListRoomDao(): CurrencyListRoomDao

    companion object {
        @Volatile
        private var INSTANCE: CurrencyLayerDatabase? = null

        fun getDatabase(context: Context): CurrencyLayerDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyLayerDatabase::class.java,
                    DBConstant.CURRENCY_LAYER_DB
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}