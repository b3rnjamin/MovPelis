package com.brdx.movpelis.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.brdx.movpelis.data.entity.MovieEntity

@Database(
    entities = [
        MovieEntity::class
    ],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        private var instance: AppDataBase? = null

        fun getDataBase(context: Context): AppDataBase {
            return instance.let {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "movie_database"
                ).build()
            }
        }

        fun destroyInstance() {
            instance = null
        }
    }
}