package repositories

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import utils.Common
import utils.UserData
import utils.database.Database

class AuthenticationRepository {

    private val auth = Common.auth
    private val collRef = Common.collRef
    private val _stateFlow = MutableStateFlow("")
    val stateFlow = _stateFlow.asStateFlow()

    suspend fun registerUser(username: String, email: String, password: String)
    {
        val obj = UserData(username,"",email,"","")

        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            collRef.document(email).set(obj).await()
            val data = HashMap<String,Any>()
            data["credit_card_expenditure"] = "0"
            data["in_bank"] = "0"
            data["in_digital_wallet"] = "0"
            data["in_wallet"] = "0"

            val statData = HashMap<String,Any>()
            statData["expenditure_count"] = 0
            statData["income_count"] = 0
            statData["total_expenditure_amount"] = 0
            statData["total_income_amount"] = 0

            collRef.document(email).collection("FINANCE").document("DATA").set(data).await()
            collRef.document(email).collection("FINANCE").document("EXPENDITURES")
            collRef.document(email).collection("FINANCE").document("INCOMES")
            collRef.document(email).collection("FINANCE").document("STATISTICS").set(statData).await()
            collRef.document(email).collection("FINANCE").document("TRANSFERS")
            collRef.document(email).collection("FINANCE").document("WITHDRAWS")
            _stateFlow.value = "User has been registered successfully"

        }
        catch (e: FirebaseAuthException) {
            _stateFlow.value = e.message.toString()
            Logger.e(e.message.toString())
        }
        catch (e: FirebaseFirestoreException) {
            _stateFlow.value = e.message.toString()
            Logger.e(e.message.toString())
        }
    }

    suspend fun loginUser(email: String, password: String)
    {
        try {
            val loginValue = auth.signInWithEmailAndPassword(email, password).await()
            val uName = collRef.document(loginValue.user?.email.toString()).get().await().getString("username").toString()
            _stateFlow.value = "Welcome $uName"
        }
        catch (e: FirebaseAuthException) {
            _stateFlow.value = e.message.toString()
            Logger.e(e.message.toString())
        }
    }

    suspend fun getUserInfo()
    {
        Common.headerEmail = auth.currentUser?.email!!
        Common.headerUname = collRef.document(Common.headerEmail).get().await().getString("username").toString()
        _stateFlow.value = "Hi, ${Common.headerUname}"
    }

    fun logoutUser(application: Application) {
        auth.signOut()
        val db = Room.databaseBuilder(application.applicationContext,Database::class.java,"userdb").build()
        db.accessDao().deleteData()
        _stateFlow.value = "Logout successful"
    }

    suspend fun resetPassword(etEmail: String)
    {
        try {
            auth.sendPasswordResetEmail(etEmail).await()
            _stateFlow.value = "Password reset link has been sent to your registered e-mail id"
        }
        catch (e: FirebaseAuthException) {
            _stateFlow.value = e.message.toString()
        }

    }

}