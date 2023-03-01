package com.snw.samllnewweather.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.snw.samllnewweather.model.DayInfo
import com.snw.samllnewweather.model.HourInfo
import com.snw.samllnewweather.screen.WeatherInfo

val DB_NAME = "sn_data_base.db"

@Database(entities = [(WeatherInfo::class), (HourInfo::class), (DayInfo::class)], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherInfoDao

    companion object {
        fun create(context: Context): AppDatabase {

            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            ).build()
        }
    }
}