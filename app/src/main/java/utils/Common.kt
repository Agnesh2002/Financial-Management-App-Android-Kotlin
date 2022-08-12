package utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

object Common {

    val auth = FirebaseAuth.getInstance()
    @SuppressLint("StaticFieldLeak")
    private val firestore = FirebaseFirestore.getInstance()
    val authEmail = auth.currentUser!!.email!!
    val collRef = firestore.collection("USERS")
    val collRefFinance = collRef.document(auth.currentUser!!.email!!).collection("FINANCE")
    val docRefExpenditures = collRefFinance.document("EXPENDITURES")
    val docRefData = collRefFinance.document("DATA")

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