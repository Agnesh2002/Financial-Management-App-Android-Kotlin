package utils.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RoomDao {

    @Insert
    fun insertData(loginData: LoginData)

    @Query("select status from auth_table where id=1")
    fun getData(): Boolean

    @Query("delete from auth_table")
    fun deleteData()

}