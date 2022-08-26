package finance

import android.app.Application
import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import repositories.ExpenditureRepository
import repositories.FinanceRepository
import utils.Common.auth
import utils.Common.toastShort
import utils.FinanceData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val financeRepository = FinanceRepository(auth.currentUser?.email!!)
    private val expenditureRepository = ExpenditureRepository(auth.currentUser?.email!!)
    private var inBankData = ArrayList<String>()
    private var inHandData = ArrayList<String>()
    private var inTotal = ""
    private val incomeModeList = arrayListOf("Received as cash","Received to digital wallet","Received to bank")
    val spinnerAdapter = ArrayAdapter(getApplication(),android.R.layout.simple_list_item_1,incomeModeList)
    var itemPosition = 0
    var dateText = MutableLiveData("Select Date")
    var pBarVisibility = MutableStateFlow(true)

    private val _stateFlow = MutableStateFlow(FinanceData(inBankData,inHandData,inTotal))
    val stateFlow = _stateFlow.asStateFlow()

    private val _liveMsg = MutableStateFlow("")
    val liveMsg = _liveMsg.asStateFlow()


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
        pBarVisibility.value = false
    }

    fun updateBankBalance(amount: String, source: String, creditCardExpenseUpdate: String)
    {
        pBarVisibility.value = true
        if(dateText.value == "Select Date")
        {
            toastShort(getApplication(), "Please select a valid date")
            pBarVisibility.value = false
            return
        }
        if(!(amount.isEmpty() || amount == ""))
        {
            val job = viewModelScope.async {
                val task1 = async { financeRepository.getInBankBalance() }
                val task2 = async { financeRepository.updateBankBalance(dateText.value.toString().trim(), amount.trim(), source.trim(), creditCardExpenseUpdate.trim()) }
                val task3 = async { updateIncomeCount(amount) }
                val task4 = async { logIncomeStatistics(amount) }
                task1.await()
                task2.await()
                task3.await()
                task4.await()
                "${amount.trim()} has been added to bank balance. Bank balance has been updated"
            }
            viewModelScope.launch {
                _liveMsg.emit(job.await())
                pBarVisibility.value = false
            }
        }
        else
        {
            toastShort(getApplication(), "Please make sure that you have filled in appropriate details to update the bank record")
            pBarVisibility.value = false
            return
        }
    }

    fun updateBalanceInHand(amountWithdrawnFromBank: String, amountAddedToDigitalWallet: String, amountFromOtherSource: String, incomeOtherSource: String)
    {
        pBarVisibility.value = true
        if(dateText.value == "Select Date")
        {
            toastShort(getApplication(), "Please select a valid date")
            pBarVisibility.value = false
            return
        }
        if(!(amountWithdrawnFromBank.isEmpty() || amountWithdrawnFromBank == ""))
        {
            viewModelScope.launch(Dispatchers.IO) {
                expenditureRepository.deductFromBank(amountWithdrawnFromBank)
            }
            viewModelScope.launch {
                expenditureRepository.stateFlow.collectLatest {
                    if(it == "")
                    {
                        return@collectLatest
                    }
                    if(it.contains("Insufficient"))
                    {
                        _liveMsg.emit(it)
                        pBarVisibility.value = false
                    }
                    else
                    {
                        changeBalanceInCash(amountWithdrawnFromBank)
                    }
                }
            }
        }
        if(!(amountAddedToDigitalWallet.isEmpty() || amountAddedToDigitalWallet == ""))
        {
            viewModelScope.launch(Dispatchers.IO) {
                expenditureRepository.deductFromBank(amountAddedToDigitalWallet)
            }
            viewModelScope.launch {
                expenditureRepository.stateFlow.collectLatest {
                    if(it == "")
                    {
                        return@collectLatest
                    }
                    if(it.contains("Insufficient"))
                    {
                        _liveMsg.emit(it)
                        pBarVisibility.value = false
                    }
                    else
                    {
                        changeBalanceInDigitalWallet(amountAddedToDigitalWallet)
                    }
                }
            }
        }
        if(!(amountFromOtherSource.isEmpty() || amountFromOtherSource == ""))
        {
            val job = viewModelScope.async {
                val task1 = async { financeRepository.getInBankBalance() }
                val task2 = async { financeRepository.updateOtherSourceIncome(dateText.value.toString(), amountFromOtherSource, incomeOtherSource, incomeModeList[itemPosition]) }
                val task3 = async { updateIncomeCount(amountFromOtherSource) }
                val task4 = async { logIncomeStatistics(amountFromOtherSource) }
                task1.await()
                task2.await()
                task3.await()
                task4.await()
                "${amountFromOtherSource.trim()} has been added to bank balance. Bank balance has been updated"
            }
            viewModelScope.launch {
                _liveMsg.emit(job.await())
                pBarVisibility.value = false
            }
        }
    }

    private fun changeBalanceInCash(amountWithdrawnFromBank: String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            financeRepository.getInHandBalance()
            financeRepository.updateBalanceCashInHand(dateText.value.toString(),amountWithdrawnFromBank)
            _liveMsg.emit("${amountWithdrawnFromBank.trim()} withdrawal from bank has been noted. In Hand balance has been updated")
            pBarVisibility.value = false
        }
    }

    private fun changeBalanceInDigitalWallet(amountAddedToDigitalWallet: String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            financeRepository.getInHandBalance()
            financeRepository.updateBalanceInDigitalWallet(dateText.value.toString(), amountAddedToDigitalWallet)
            _liveMsg.emit("${amountAddedToDigitalWallet.trim()} addition to your digital wallet has been noted. Balance has been updated")
            pBarVisibility.value = false
        }
    }

    private suspend fun updateIncomeCount(amount: String)
    {
        financeRepository.updateIncomeCount(amount)
    }

    private suspend fun logIncomeStatistics(amount: String)
    {
        val dateToMonthAndYear = dateText.value.toString().split("-")
        val monthAndYear = dateToMonthAndYear[1].trim()+"-"+dateToMonthAndYear[2].trim()
        financeRepository.updateMonthlyIncomeStatistics(monthAndYear,amount)
    }

}