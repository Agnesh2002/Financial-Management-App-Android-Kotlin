package repositories

import kotlinx.coroutines.tasks.await
import utils.Common

class FinanceRepository {

    var amountInWallet = 0.0
    var amountInDigitalWallet = 0.0

    suspend fun getInHandBalance()
    {
        val balInWallet = Common.docRefData.get().await().get("in_wallet")
        amountInWallet = balInWallet!!.toString().toDouble()

        val balInDigitalWallet = Common.docRefData.get().await().get("in_digital_wallet")
        amountInDigitalWallet = balInDigitalWallet!!.toString().toDouble()
    }

}