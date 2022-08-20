package repositories

import utils.Common
import utils.Common.collRef

class SetupRepository {

    var authEmail = ""
    val docRefData = collRef.document(authEmail).collection("FINANCE").document("DATA")

    fun resetBankBalance(amount: String)
    {
        docRefData.update("in_bank", amount)
    }

    fun resetCashInWallet(amount: String)
    {
        docRefData.update("in_wallet", amount)
    }

    fun resetAmountInDigitalWallet(amount: String)
    {
        docRefData.update("in_digital_wallet", amount)
    }

    fun resetCreditCardExpenditure(amount: String)
    {
        docRefData.update("credit_card_expenditure", amount)
    }

}