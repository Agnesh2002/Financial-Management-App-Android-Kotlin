package repositories

import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import utils.Common
import java.lang.Exception

class WithdrawRepository(authEmail: String) {

    val listOfWithdraws = arrayListOf<String>()
    private val docRefExpenditures = Common.collRef.document(authEmail).collection("FINANCE").document("WITHDRAWS")
    private val _stateFlowMsg = MutableStateFlow("")
    val stateFlow = _stateFlowMsg.asStateFlow()

    suspend fun getWithdrawData()
    {
        try {
            val result = docRefExpenditures.get().await()
            for (expense in result.data!!.values)
                listOfWithdraws.add(expense.toString())
        }
        catch (e: FirebaseFirestoreException) {
            _stateFlowMsg.value = e.message.toString()
        }
        catch (e: Exception)
        {
            _stateFlowMsg.value = "No withdrawal data available yet"
        }
    }
}