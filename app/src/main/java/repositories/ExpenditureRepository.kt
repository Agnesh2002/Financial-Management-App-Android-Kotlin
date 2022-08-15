package repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestoreException
import com.orhanobut.logger.Logger
import kotlinx.coroutines.tasks.await
import utils.Common
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ExpenditureRepository {

    val listOfExpensesFromRepo = arrayListOf<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun logExpense(purpose: String, payee: String, paymentMode: String, date: String, amount: String)
    {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formatted = current.format(formatter)

        val data = HashMap<String,Any>()
        val fieldName = Common.authEmail.replace(".","_")+"$formatted-$date".lowercase()
        val expenditureArray = arrayListOf(fieldName, date, paymentMode, payee, purpose, amount)
        data[fieldName] = expenditureArray
        Common.docRefExpenditures.update(data)
    }

    suspend fun getExpenseData()
    {
        try {
            val result = Common.docRefExpenditures.get().await()
            for (expense in result.data!!.values)
                listOfExpensesFromRepo.add(expense.toString())
        }
        catch (e: FirebaseFirestoreException) {
            Logger.e(e.message.toString())
        }

    }

    suspend fun deductFromBank(amount: String)
    {
        val amountValue = amount.toDouble()
        val balInBank = Common.docRefData.get().await().get("in_bank")
        val newBankVal = balInBank!!.toString().toDouble() - amountValue
        Common.docRefData.update("in_bank",newBankVal.toString())
    }

    suspend fun deductFromHand(amount: String)
    {
        val amountValue = amount.toDouble()
        val balInWallet = Common.docRefData.get().await().get("in_wallet")
        val newBalInWallet = balInWallet!!.toString().toDouble() - amountValue
        Common.docRefData.update("in_wallet",newBalInWallet.toString())
    }

    suspend fun deductFromDigitalWallet(amount: String)
    {
        val amountValue = amount.toDouble()
        val balInDigitalWallet = Common.docRefData.get().await().get("in_digital_wallet")
        val newBalInDigitalWallet = balInDigitalWallet!!.toString().toDouble() - amountValue
        Common.docRefData.update("in_digital_wallet",newBalInDigitalWallet.toString())
    }

    suspend fun addToCreditCardExpense(amount: String)
    {
        val amountValue = amount.toDouble()
        val expenditureByCreditCard = Common.docRefData.get().await().get("credit_card_expenditure")
        val newExpenditureByCreditCard = expenditureByCreditCard!!.toString().toDouble() + amountValue
        Common.docRefData.update("credit_card_expenditure",newExpenditureByCreditCard.toString())
    }

}