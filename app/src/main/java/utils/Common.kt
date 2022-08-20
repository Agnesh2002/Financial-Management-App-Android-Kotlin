package utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
object Common {

    val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    val collRef = firestore.collection("USERS")
    var headerUname = ""
    var headerEmail = ""

    fun currentTime(): String
    {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return current.format(formatter)
    }

    fun setUpLogger()
    {
        Logger.addLogAdapter(AndroidLogAdapter())
    }
    fun removeLogger()
    {
        Logger.clearLogAdapters()
    }

    fun toastShort(context: Context, message: String)
    {
        Toast.makeText(context.applicationContext,message,Toast.LENGTH_SHORT).show()
    }
    fun toastLong(context: Context, message: String)
    {
        Toast.makeText(context.applicationContext,message,Toast.LENGTH_LONG).show()
    }

}