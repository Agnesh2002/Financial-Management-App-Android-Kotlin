package repositories

import com.google.firebase.firestore.FirebaseFirestoreException
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import utils.Common
import java.lang.Exception


class ExpenditureRepository(val authEmail: String) {

    val listOfExpenses = arrayListOf<String>()
    private val docRefExpenditures = Common.collRef.document(authEmail).collection("FINANCE").document("EXPENDITURES")
    private val docRefData = Common.collRef.document(authEmail).collection("FINANCE").document("DATA")
    private val docRefStatistics = Common.collRef.document(authEmail).collection("FINANCE").document("STATISTICS")
    private val collRefMonthlyStatistics = Common.collRef.document(authEmail).collection("FINANCE").document("STATISTICS").collection("MONTHLY")

    private val _stateFlowMsg = MutableStateFlow("")
    val stateFlow = _stateFlowMsg.asStateFlow()

    suspend fun logExpense(purpose: String, payee: String, paymentMode: String, date: String, amount: String)
    {
        val data = HashMap<String,Any>()
        val fieldName = authEmail.replace(".","_")+"-${Common.currentTime()}-$date".lowercase()
        val expenditureArray = arrayListOf(fieldName, date, paymentMode, payee, purpose, amount)
        data[fieldName] = expenditureArray

        if(!docRefExpenditures.get().await().exists())
        {
            docRefExpenditures.set(data).await()
        }
        else{
            docRefExpenditures.update(data)
        }
    }

    suspend fun getExpenseData()
    {
        try {
            val result = docRefExpenditures.get().await()
            for (expense in result.data!!.values)
                listOfExpenses.add(expense.toString())
        }
        catch (e: FirebaseFirestoreException) {
            _stateFlowMsg.value = e.message.toString()
        }
        catch (e: Exception)
        {
            _stateFlowMsg.value = "No expenditure data available yet"
        }
    }

    suspend fun deductFromBank(amount: String)
    {
        val amountValue = amount.toDouble()
        val balInBank = docRefData.get().await().get("in_bank")
        val newBankVal = balInBank!!.toString().toDouble() - amountValue
        docRefData.update("in_bank",newBankVal.toString())
    }

    suspend fun deductFromHand(amount: String)
    {
        val amountValue = amount.toDouble()
        val balInWallet = docRefData.get().await().get("in_wallet")
        val newBalInWallet = balInWallet!!.toString().toDouble() - amountValue
        docRefData.update("in_wallet",newBalInWallet.toString())
    }

    suspend fun deductFromDigitalWallet(amount: String)
    {
        val amountValue = amount.toDouble()
        val balInDigitalWallet = docRefData.get().await().get("in_digital_wallet")
        val newBalInDigitalWallet = balInDigitalWallet!!.toString().toDouble() - amountValue
        docRefData.update("in_digital_wallet",newBalInDigitalWallet.toString())
    }

    suspend fun addToCreditCardExpense(amount: String)
    {
        val amountValue = amount.toDouble()
        val expenditureByCreditCard = docRefData.get().await().get("credit_card_expenditure")
        val newExpenditureByCreditCard = expenditureByCreditCard!!.toString().toDouble() + amountValue
        docRefData.update("credit_card_expenditure",newExpenditureByCreditCard.toString())
    }

    suspend fun updateExpenseCount(newAmount: String) {
        var expenditureCount = docRefStatistics.get().await().getLong("expenditure_count")!!
        expenditureCount += 1
        docRefStatistics.update("expenditure_count", expenditureCount)

        val amountValue = docRefStatistics.get().await().getLong("total_expenditure_amount")!!
        val expenditureAmount = amountValue + newAmount.toLong()
        docRefStatistics.update("total_expenditure_amount", expenditureAmount)
    }

    suspend fun updateMonthlyStatistics(date: String, newAmount: String)
    {
        if(!collRefMonthlyStatistics.document(date).get().await().exists())
        {
            val data = HashMap<String,Any>()
            data["total_expenditure_amount"] = newAmount.toDouble()
            data["expenditure_count"] = 1
            data["total_income_amount"] = 0
            data["income_count"] = 0
            collRefMonthlyStatistics.document(date).set(data).await()
        }
        else{
            var monthlyExpenditureCount = collRefMonthlyStatistics.document(date).get().await().getLong("expenditure_count")!!
            monthlyExpenditureCount += 1
            collRefMonthlyStatistics.document(date).update("expenditure_count",monthlyExpenditureCount)

            val monthlyAmountValue = collRefMonthlyStatistics.document(date).get().await().getLong("total_expenditure_amount")!!
            val monthlyExpenditureAmount = monthlyAmountValue +  newAmount.toLong()
            collRefMonthlyStatistics.document(date).update("total_expenditure_amount",monthlyExpenditureAmount)
        }
    }

}