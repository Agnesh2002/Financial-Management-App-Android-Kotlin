package income

import android.annotation.SuppressLint
import android.app.Application
import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import repositories.FinanceRepository
import java.text.SimpleDateFormat
import java.util.*

class IncomeHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val arrayList = arrayListOf("Make a selection", "Latest first", "Oldest first", "Big amount first", "Small amount first")
    val spinnerAdapter = ArrayAdapter(getApplication(), android.R.layout.simple_list_item_1, arrayList)
    private val financeRepository = FinanceRepository()

    private val incomeListFromRepo = arrayListOf<String>()
    private val dataAsObjectList = arrayListOf<IncomeData>()

    private var sortedList = listOf<IncomeData>()
    private var finalDataList = arrayListOf<IncomeData>()
    var adapter = CustomIncomeHistoryAdapter(finalDataList)

    var dateText = MutableLiveData("Select Date")
    var radioGroupVisibility = MutableStateFlow(false)
    var cal: Calendar = Calendar.getInstance()
    @SuppressLint("NewApi")
    val dateSetListener = DatePickerDialog.OnDateSetListener { callBack, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        radioGroupVisibility.value = true
        updateDateInView()
        callBack.setOnDateChangedListener { _, _, _, _ ->
            clearView()
        }
    }

    private var dateFormatChanged = false

    private fun updateDateInView() {
        val myFormat = "dd-MMM-yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateText.value = sdf.format(cal.time)
    }

    fun getData() {
        viewModelScope.launch {
            financeRepository.getIncomeData()
            incomeListFromRepo.clear()
            for (expenseList in financeRepository.listOfIncomes) {
                val separated = expenseList.split("],")
                for (expense in separated)
                    incomeListFromRepo.add(expense)
            }
            convertToListOfObjects()
        }
    }

    private fun convertToListOfObjects() {
        viewModelScope.launch(Dispatchers.Default) {
            for (position in 0 until incomeListFromRepo.size) {
                val fields = incomeListFromRepo[position].replace("[", "").replace("]", "").split(",")
                val id = fields[0].trim()
                val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                val date = sdf.parse(fields[1].trim())
                val sdf2 = SimpleDateFormat("MMM-yyyy", Locale.US)
                val dateToMonthAndYear = fields[1].split("-")
                val monthAndYear = sdf2.parse(dateToMonthAndYear[1].trim()+"-"+dateToMonthAndYear[2].trim())
                val amount = fields[2].trim().toDouble()
                val source = fields[3].trim()
                val modeOfIncome = fields[4].trim()
                dataAsObjectList.add(IncomeData(id, date!!, amount, source, modeOfIncome, monthAndYear))
            }
        }
    }

    fun sortDataBasedOnLatestFirst() {
        finalDataList.clear()
        sortedList = dataAsObjectList.sortedByDescending { it.date }
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun sortDataBasedOnOldestFirst() {
        finalDataList.clear()
        sortedList = dataAsObjectList.sortedBy { it.date }
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun sortDataBasedOnAmountDescending() {
        finalDataList.clear()
        sortedList = dataAsObjectList.sortedWith(compareBy { it.amount })
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun sortDataBasedOnAmountAscending() {
        finalDataList.clear()
        sortedList = dataAsObjectList.sortedWith(compareByDescending { it.amount })
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun filterByExactDate()
    {
        var sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
        if(dateFormatChanged)
        {
            sdf = SimpleDateFormat("dd-MMM-yyyy" , Locale.US)
            dateText.value = sdf.format(cal.time)
            dateFormatChanged = false
        }
        val date = sdf.parse(dateText.value)
        finalDataList.clear()
        sortedList = dataAsObjectList.filter {
            it.date == date
        }
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun filterByMonth()
    {
        val sdf = SimpleDateFormat("MMM-yyyy" , Locale.US)
        dateText.value = sdf.format(cal.time)
        dateFormatChanged = true

        val date = sdf.parse(dateText.value)
        finalDataList.clear()
        sortedList = dataAsObjectList.filter {
            it.monthAndYear == date
        }
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun clearView()
    {
        finalDataList.clear()
        adapter.notifyDataSetChanged()
    }

}