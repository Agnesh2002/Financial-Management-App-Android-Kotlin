package utils.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_table")
data class LoginData(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val status: Boolean
)
