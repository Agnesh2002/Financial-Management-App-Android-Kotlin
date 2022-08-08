package repositories

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import utils.Common.toastShort
import utils.UserData

class AuthenticationRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val collRef = firestore.collection("USERS")

    suspend fun registerUser(application: Application, username: String, email: String, password: String)
    {
        val obj = UserData(username,"",email,"","")

        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            collRef.document(email).set(obj).await()
            withContext(Dispatchers.Main) {
                toastShort(application, "User has been registered successfully")
            }
        }
        catch (e: FirebaseAuthException) {
            withContext(Dispatchers.Main){
                toastShort(application,e.message.toString())
            }
            Logger.e(e.message.toString())
        }
        catch (e: FirebaseFirestoreException) {
            withContext(Dispatchers.Main){
                toastShort(application,e.message.toString())
            }
            Logger.e(e.message.toString())
        }
    }

    suspend fun loginUser(application: Application, email: String, password: String)
    {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            withContext(Dispatchers.Main) {
                toastShort(application, "Welcome")
            }
        }
        catch (e: FirebaseAuthException) {
            withContext(Dispatchers.Main){
                toastShort(application,e.message.toString())
            }
            Logger.e(e.message.toString())
        }
    }

}