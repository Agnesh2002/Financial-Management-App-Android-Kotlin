package setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repositories.SetupRepository
import utils.Common.auth

class SetupViewModel(application: Application) : AndroidViewModel(application) {

    var bankBalance = ""
    var cashInWallet = ""
    var amountInDigitalWallet = ""
    var creditCardExpenditure = ""
    val errorMsg = "This field is required"
    private val setupRepository = SetupRepository(auth.currentUser?.email!!)
    private val _stateFlow = MutableStateFlow(0)
    val stateFlowMsg = _stateFlow.asStateFlow()

    fun setBankBalance()
    {
        if(bankBalance.isEmpty() || bankBalance == "")
        {
            _stateFlow.value = 1
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            setupRepository.resetBankBalance(bankBalance)
        }
    }

    fun setCashInWallet()
    {
        if(cashInWallet.isEmpty() || cashInWallet == "")
        {
            _stateFlow.value = 2
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            setupRepository.resetCashInWallet(cashInWallet)
        }
    }

    fun setAmountInDigitalWallet()
    {
        if(amountInDigitalWallet.isEmpty() || amountInDigitalWallet == "")
        {
            _stateFlow.value = 3
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            setupRepository.resetAmountInDigitalWallet(amountInDigitalWallet)
        }
    }

    fun setCreditCardExpenditure()
    {
        if(creditCardExpenditure.isEmpty() || creditCardExpenditure == "")
        {
            _stateFlow.value = 4
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            setupRepository.resetCreditCardExpenditure(creditCardExpenditure)
        }
    }

}