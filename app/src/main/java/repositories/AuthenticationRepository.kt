package repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.tasks.await
import utils.Common
import utils.UserData

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
            joinAll()
            val uname = collRef.document(email).get().await().getString("username").toString()
            _stateFlow.value = "Welcome $uname"
        }
        catch (e: FirebaseAuthException) {
            _stateFlow.value = e.message.toString()
            Logger.e(e.message.toString())
        }
    }

}