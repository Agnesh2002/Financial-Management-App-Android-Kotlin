package utils

import android.content.Context
import android.widget.Toast
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

object Common {

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