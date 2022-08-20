package repositories

import com.google.firebase.firestore.FirebaseFirestoreException
import com.orhanobut.logger.Logger
import kotlinx.coroutines.tasks.await
import utils.Common


class ExpenditureRepository(val authEmail: String) {

    val listOfExpenses = arrayListOf<String>()
    val docRefExpenditures = Common.collRef.document(authEmail).collection("FINANCE").document("EXPENDITURES")
    val docRefData = Common.collRef.document(authEmail).collection("FINANCE").document("DATA")
    val docRefStatistics = Common.collRef.document(authEmail).collection("FINANCE").document("STATISTICS")
    val collRefMonthlyStatistics = Common.collRef.document(authEmail).collection("FINANCE").document("STATISTICS").collection("MONTHLY")

    suspend fun logExpense(purpose: String, payee: String, paymentMode: String, date: String, amount: String)
    {
        val data = HashMap<String,Any>()
        val fieldName = authEmail.replace(".","_")+"-${Common.currentTime()}-$date".lowercase()
        val expenditureArray = arrayListOf(fieldName, date, paymentMode, payee, purpose, amount)
        data[fieldName] = expenditureArray
        docRefExpenditures.update(data)

        val dateToMonthAndYear = date.split("-")
        val monthAndYear = dateToMonthAndYear[1].trim()+"-"+dateToMonthAndYear[2].trim()
        updateExpenseCount(monthAndYear, amount)
    }

    suspend fun getExpenseData()
    {
        try {
            val result = docRefExpenditures.get().await()
            for (expense in result.data!!.values)
                listOfExpenses.add(expense.toString())
        }
        catch (e: FirebaseFirestoreException) {
            Logger.e(e.message.toString())
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

    private suspend fun updateExpenseCount(date: String, newAmount: String)
    {
        var expenditureCount = docRefStatistics.get().await().getLong("expenditure_count")!!
        expenditureCount += 1
        docRefStatistics.update("expenditure_count",expenditureCount)

        val amountValue = docRefStatistics.get().await().getLong("total_expenditure_amount")!!
        val expenditureAmount = amountValue +  newAmount.toLong()
        docRefStatistics.update("total_expenditure_amount",expenditureAmount)

        if(!collRefMonthlyStatistics.document(date).get().await().exists())
        {
            val data = HashMap<String,Any>()
            data["total_expenditure_amount"] = 0
            data["expenditure_count"] = 0
            data["total_income_amount"] = 0
            data["income_count"] = 0
            collRefMonthlyStatistics.document(date).set(data).await()
            updateMonthlyExpenditureStatistics(date, newAmount)
        }
        else{
            updateMonthlyExpenditureStatistics(date, newAmount)
        }
    }

    private suspend fun updateMonthlyExpenditureStatistics(date: String, newAmount: String)
    {
        var monthlyExpenditureCount = collRefMonthlyStatistics.document(date).get().await().getLong("expenditure_count")!!
        monthlyExpenditureCount += 1
        collRefMonthlyStatistics.document(date).update("expenditure_count",monthlyExpenditureCount)

        val monthlyAmountValue = collRefMonthlyStatistics.document(date).get().await().getLong("total_expenditure_amount")!!
        val monthlyExpenditureAmount = monthlyAmountValue +  newAmount.toLong()
        collRefMonthlyStatistics.document(date).update("total_expenditure_amount",monthlyExpenditureAmount)
    }

}