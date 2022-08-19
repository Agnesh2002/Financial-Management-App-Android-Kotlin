package repositories

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.orhanobut.logger.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import utils.Common
import utils.Common.authEmail
import utils.Common.firebaseUser
import utils.Common.headerEmail
import utils.Common.headerUname
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
            collRef.document(email).collection("FINANCE").document("DATA").set(data)
            collRef.document(email).collection("FINANCE").document("EXPENDITURES")
            collRef.document(email).collection("FINANCE").document("INCOMES")
            collRef.document(email).collection("FINANCE").document("STATISTICS")
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
            auth.signInWithEmailAndPassword(email, password).await()
            headerUname = collRef.document(email).get().await().getString("username").toString()
            headerEmail = collRef.document(email).get().await().getString("email").toString()
            checkFirebaseUser(headerUname)
        }
        catch (e: FirebaseAuthException) {
            _stateFlow.value = e.message.toString()
            Logger.e(e.message.toString())
        }
    }

    private suspend fun checkFirebaseUser(uname: String)
    {
        if(auth.currentUser == null) {
            delay(1000)
            checkFirebaseUser(uname)
        }
        else
        {
            firebaseUser = auth.currentUser
            authEmail = firebaseUser!!.email
            getDisplayInfo()
        }
    }

     fun logoutUser(application: Application) {
        auth.signOut()
        val db = Room.databaseBuilder(application.applicationContext,Database::class.java,"userdb").build()
        db.accessDao().deleteData()
        _stateFlow.value = "Logout successful"
    }

    suspend fun getDisplayInfo()
    {
        headerUname = collRef.document(authEmail.toString()).get().await().getString("username").toString()
        headerEmail = collRef.document(authEmail.toString()).get().await().getString("email").toString()
        _stateFlow.value = "Welcome $headerUname"
    }

}