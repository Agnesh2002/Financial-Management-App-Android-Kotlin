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

}