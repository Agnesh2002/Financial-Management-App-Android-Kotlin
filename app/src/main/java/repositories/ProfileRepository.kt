package repositories

import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import utils.Common.auth
import utils.Common.collRef
import java.lang.Exception

class ProfileRepository(private val authEmail: String) {

    val stateFlowMsg = MutableStateFlow("")

    suspend fun updateUsername(username: String)
    {
        try {
            collRef.document(authEmail).update("username",username).await()
            stateFlowMsg.value = "Username updated successfully"
        }
        catch (e: Exception)
        {
            stateFlowMsg.value = "Some error occurred"
        }
    }

    suspend fun updatePassword(password: String)
    {
        try {
            auth.currentUser?.updatePassword(password)?.await()
            stateFlowMsg.value = "Password changed successfully"
        }
        catch (e: FirebaseAuthException)
        {
            stateFlowMsg.value = e.message.toString()
        }
        catch (e: Exception)
        {
            stateFlowMsg.value = "Some error occured"
        }

    }
}