package repositories

import utils.Common

class SetupRepository {

    fun resetBankBalance(amount: String)
    {
        Common.docRefData.update("in_bank", amount)
    }

    fun resetCashInWallet(amount: String)
    {
        Common.docRefData.update("in_wallet", amount)
    }

    fun resetAmountInDigitalWallet(amount: String)
    {
        Common.docRefData.update("in_digital_wallet", amount)
    }

    fun resetCreditCardExpenditure(amount: String)
    {
        Common.docRefData.update("credit_card_expenditure", amount)
    }

}