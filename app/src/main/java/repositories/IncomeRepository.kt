package repositories

import com.google.firebase.firestore.FirebaseFirestoreException
import com.orhanobut.logger.Logger
import kotlinx.coroutines.tasks.await
import utils.Common

class IncomeRepository {

    var listOfIncomes = arrayListOf<String>()

    suspend fun getIncomeData()
    {
        try {
            val result = Common.docRefIncomes.get().await()
            for (expense in result.data!!.values)
                listOfIncomes.add(expense.toString())
        }
        catch (e: FirebaseFirestoreException)
        {
            Logger.e(e.message.toString())
        }
    }

}