package org.netg.netgmovies.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.netg.netgmovies.data.db.dao.MovieDao
import org.netg.netgmovies.data.db.model.MovieEntity

const val MOVIE_DATABASE = "movie_db"

@Database(entities = [MovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}