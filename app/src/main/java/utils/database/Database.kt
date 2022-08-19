package utils.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LoginData::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase(){
    abstract fun accessDao(): RoomDao
}