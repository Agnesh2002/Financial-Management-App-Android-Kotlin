package finance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repositories.FinanceRepository
import utils.FinanceData

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val financeRepository = FinanceRepository()
    private var inBankData = ArrayList<String>()
    private var inHandData = ArrayList<String>()
    private var inTotal = ""

    private val _stateFlow = MutableStateFlow(FinanceData(inBankData,inHandData,inTotal))
    val stateFlow = _stateFlow.asStateFlow()


    suspend fun loadFinanceData()
    {

        val job1 = viewModelScope.launch(Dispatchers.IO) {
            financeRepository.getInBankBalance()
            financeRepository.getInHandBalance()

            inBankData.add("₹ ${financeRepository.amountInBank}")
            inBankData.add("₹ ${financeRepository.amountUsingCreditCard}")

            inHandData.add("₹ ${financeRepository.amountInWallet + financeRepository.amountInDigitalWallet}")
            inHandData.add("₹ ${financeRepository.amountInWallet}")
            inHandData.add("₹ ${financeRepository.amountInDigitalWallet}")

            inTotal = (financeRepository.amountInBank+financeRepository.amountInWallet+financeRepository.amountInDigitalWallet).toString()

        }
        job1.join()

        Logger.e(inBankData.toString() + inHandData.toString())
        val obj = FinanceData(inBankData,inHandData,inTotal)
        _stateFlow.emit(obj)

    }


}