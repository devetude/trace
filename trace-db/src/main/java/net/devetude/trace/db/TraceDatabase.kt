package net.devetude.trace.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.devetude.trace.common.annotation.PositiveInt
import net.devetude.trace.db.TraceDatabase.Companion.LATEST_VERSION
import net.devetude.trace.db.converter.DateConverter
import net.devetude.trace.db.dao.CarDao
import net.devetude.trace.db.dao.HistoryDao
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.History

@Database(entities = [Car::class, History::class], version = LATEST_VERSION)
@TypeConverters(value = [DateConverter::class])
abstract class TraceDatabase : RoomDatabase() {
    abstract fun getCarDao(): CarDao

    abstract fun getHistoryDao(): HistoryDao

    companion object {
        private const val DATABASE_NAME = "trace"

        private const val V_1_0_0 = 1

        @PositiveInt
        internal const val LATEST_VERSION = V_1_0_0

        fun create(application: Application): TraceDatabase =
            Room.databaseBuilder(application, TraceDatabase::class.java, DATABASE_NAME).build()
    }
}
