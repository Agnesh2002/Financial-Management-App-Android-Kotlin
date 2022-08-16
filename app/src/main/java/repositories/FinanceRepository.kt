package repositories

import kotlinx.coroutines.tasks.await
import utils.Common

class FinanceRepository {

    var amountInWallet = 0.0
    var amountInDigitalWallet = 0.0
    var amountInBank = 0.0
    var amountUsingCreditCard = 0.0

    suspend fun getInHandBalance()
    {
        val balInWallet = Common.docRefData.get().await().get("in_wallet")
        amountInWallet = balInWallet!!.toString().toDouble()

        val balInDigitalWallet = Common.docRefData.get().await().get("in_digital_wallet")
        amountInDigitalWallet = balInDigitalWallet!!.toString().toDouble()
    }

    suspend fun getInBankBalance()
    {
        val balInBank = Common.docRefData.get().await().get("in_bank")
        amountInBank = balInBank!!.toString().toDouble()

        val creditCardExpenditure = Common.docRefData.get().await().get("credit_card_expenditure")
        amountUsingCreditCard = creditCardExpenditure!!.toString().toDouble()

    }

    fun logIncome(date: String, amount: String, source: String)
    {
        val data = HashMap<String,Any>()
        val fieldName = Common.authEmail.replace(".","_")+"-income-${Common.currentTime()}-$date".lowercase()
        val incomeArray = arrayListOf(fieldName, date, amount, source)
        data[fieldName] = incomeArray
        Common.docRefIncomes.update(data)
    }

    fun updateBankBalance(date: String, amount: String, source: String, creditCardExpenseUpdate: String)
    {
        if(!(amount.isEmpty() || amount == "")) {
            amountInBank += amount.toDouble()
            Common.docRefData.update("in_bank",amountInBank.toString())
            logIncome(date, amount, source)
        }
        if(!(creditCardExpenseUpdate.isEmpty() || creditCardExpenseUpdate=="")) {
            amountUsingCreditCard += creditCardExpenseUpdate.toDouble()
            Common.docRefData.update("credit_card_expenditure", amountUsingCreditCard.toString())
        }
    }

    fun updateBalanceCashInHand(date: String, amount: String)
    {
        val data = HashMap<String,Any>()
        val fieldName = Common.authEmail.replace(".","_")+"-withdraw-${Common.currentTime()}-$date".lowercase()
        val withdrawArray = arrayListOf(fieldName, date, amount)
        data[fieldName] = withdrawArray
        Common.docRefWithdraws.update(data)

        amountInWallet += amount.toDouble()
        Common.docRefData.update("in_wallet",amountInWallet.toString())
    }

    fun updateBalanceInDigitalWallet(date: String, amount: String)
    {
        val data = HashMap<String,Any>()
        val fieldName = Common.authEmail.replace(".","_")+"-transfer-${Common.currentTime()}-$date".lowercase()
        val transferArray = arrayListOf(fieldName, date, amount)
        data[fieldName] = transferArray
        Common.docRefTransfers.update(data)

        amountInDigitalWallet += amount.toDouble()
        Common.docRefData.update("in_digital_wallet",amountInDigitalWallet.toString())
    }

}