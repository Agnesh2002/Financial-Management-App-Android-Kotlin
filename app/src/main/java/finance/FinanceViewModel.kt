package finance

import android.app.Application
import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repositories.ExpenditureRepository
import repositories.FinanceRepository
import utils.Common.toastShort
import utils.FinanceData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val financeRepository = FinanceRepository()
    private val expenditureRepository = ExpenditureRepository()
    private var inBankData = ArrayList<String>()
    private var inHandData = ArrayList<String>()
    private var inTotal = ""
    private val incomeModeList = arrayListOf("Received as cash","Received to digital wallet","Received to bank")
    val spinnerAdapter = ArrayAdapter(getApplication(),android.R.layout.simple_list_item_1,incomeModeList)
    var itemPosition = 0
    var dateText = MutableLiveData("Select Date")

    private val _stateFlow = MutableStateFlow(FinanceData(inBankData,inHandData,inTotal))
    val stateFlow = _stateFlow.asStateFlow()

    var cal: Calendar = Calendar.getInstance()
    val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateInView()
    }

    private fun updateDateInView() {
        val myFormat = "dd-MMM-yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateText.value = sdf.format(cal.time)
    }


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

    fun updateBankBalance(amount: String, source: String, creditCardExpenseUpdate: String)
    {
        if(dateText.value == "Select Date")
        {
            toastShort(getApplication(), "Please select a valid date")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            financeRepository.getInBankBalance()
            financeRepository.updateBankBalance(dateText.value.toString().trim(), amount.trim(), source.trim(), creditCardExpenseUpdate.trim())
        }
    }

    fun updateBalanceInHand(amountWithdrawnFromBank: String, amountAddedToDigitalWallet: String, amountFromOtherSource: String, incomeOtherSource: String)
    {
        if(dateText.value == "Select Date")
        {
            toastShort(getApplication(), "Please select a valid date")
            return
        }
        if(!(amountWithdrawnFromBank.isEmpty() || amountWithdrawnFromBank == ""))
        {
            viewModelScope.launch(Dispatchers.IO) {
                expenditureRepository.deductFromBank(amountWithdrawnFromBank)
                financeRepository.getInHandBalance()
                financeRepository.updateBalanceCashInHand(dateText.value.toString(),amountWithdrawnFromBank)
            }
        }
        if(!(amountAddedToDigitalWallet.isEmpty() || amountAddedToDigitalWallet == ""))
        {
            viewModelScope.launch(Dispatchers.IO) {
                expenditureRepository.deductFromBank(amountAddedToDigitalWallet)
                financeRepository.getInHandBalance()
                financeRepository.updateBalanceInDigitalWallet(dateText.value.toString(), amountAddedToDigitalWallet)
            }
        }
        if(!(amountFromOtherSource.isEmpty() || amountFromOtherSource == ""))
        {
            viewModelScope.launch(Dispatchers.IO) {
                financeRepository.getInHandBalance()
                financeRepository.updateOtherSourceIncome(dateText.value.toString(), amountFromOtherSource, incomeOtherSource, incomeModeList[itemPosition])
            }
        }
    }

}