package repositories

import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import utils.Common
import java.lang.Exception

class TransferRepository(authEmail: String) {

    val listOfTransfers = arrayListOf<String>()
    private val docRefExpenditures = Common.collRef.document(authEmail).collection("FINANCE").document("TRANSFERS")
    private val _stateFlowMsg = MutableStateFlow("")
    val stateFlow = _stateFlowMsg.asStateFlow()

    suspend fun getTransferData()
    {
        try {
            val result = docRefExpenditures.get().await()
            for (expense in result.data!!.values)
                listOfTransfers.add(expense.toString())
        }
        catch (e: FirebaseFirestoreException) {
            _stateFlowMsg.value = e.message.toString()
        }
        catch (e: Exception)
        {
            _stateFlowMsg.value = "No Transfer data available yet"
        }
    }
}