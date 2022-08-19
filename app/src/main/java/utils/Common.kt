package utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
object Common {

    val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    var headerUname = ""
    var headerEmail = ""
    var firebaseUser: FirebaseUser? = auth.currentUser
    var authEmail: String? = firebaseUser?.email

    val collRef = firestore.collection("USERS")

    val docRefExpenditures: DocumentReference by lazy { collRef.document(authEmail.toString()).collection("FINANCE").document("EXPENDITURES") }
    val docRefIncomes: DocumentReference by lazy { collRef.document(authEmail.toString()).collection("FINANCE").document("INCOMES") }
    val docRefWithdraws: DocumentReference by lazy { collRef.document(authEmail.toString()).collection("FINANCE").document("WITHDRAWS") }
    val docRefTransfers: DocumentReference by lazy { collRef.document(authEmail.toString()).collection("FINANCE").document("TRANSFERS") }
    val docRefData: DocumentReference by lazy { collRef.document(authEmail.toString()).collection("FINANCE").document("DATA") }
    val docRefStatistics: DocumentReference by lazy { collRef.document(authEmail.toString()).collection("FINANCE").document("STATISTICS") }
    val collRefMonthlyStatistics:CollectionReference by lazy { collRef.document(authEmail.toString()).collection("FINANCE").document("STATISTICS").collection("MONTHLY") }

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