package expenditure

import android.app.Application
import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import repositories.ExpenditureRepository
import java.text.SimpleDateFormat
import java.util.*

class ExpenditureHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val arrayList = arrayListOf("Make a selection", "Latest first", "Oldest first", "Big amount first", "Small amount first")
    val spinnerAdapter = ArrayAdapter(getApplication(), android.R.layout.simple_list_item_1, arrayList)
    private val expenditureRepository = ExpenditureRepository()

    private var expenseListFromRepo = arrayListOf<String>()
    private val dataAsObjectList = arrayListOf<ExpenseData>()

    private var sortedList = listOf<ExpenseData>()
    val finalDataList = arrayListOf<ExpenseData>()
    var adapter = CustomExpenditureAdapter(finalDataList)

    var dateText = MutableLiveData("Select Date")
    var radioGroupVisibility = MutableStateFlow(false)
    var cal: Calendar = Calendar.getInstance()
    val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        radioGroupVisibility.value = true
        updateDateInView()
    }
    var dateFormatChanged = false

    private fun updateDateInView() {
        val myFormat = "dd-MMM-yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateText.value = sdf.format(cal.time)
    }

    fun getData()
    {
        viewModelScope.launch(Dispatchers.IO){

            expenditureRepository.getExpenseData()
            expenseListFromRepo.clear()
            for(expenseList in expenditureRepository.listOfExpenses)
            {
                val separated = expenseList.split("],")
                for (expense in separated)
                    expenseListFromRepo.add(expense)
            }
            convertToListOfObjects()
        }

    }

    private fun convertToListOfObjects()
    {
        viewModelScope.launch(Dispatchers.Default) {
            for(position in 0 until expenseListFromRepo.size)
            {
                val fields = expenseListFromRepo[position].replace("[","").replace("]","").split(",")
                val id = fields[0].trim()
                val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                val date = sdf.parse(fields[1].trim())
                val sdf2 = SimpleDateFormat("MMM-yyyy", Locale.US)
                val dateToMonthAndYear = fields[1].split("-")
                val monthAndYear = sdf2.parse(dateToMonthAndYear[1].trim()+"-"+dateToMonthAndYear[2].trim())
                val modeOfPayment = fields[2].trim()
                val payee = fields[3].trim()
                val purpose = fields[4].trim()
                val amount = fields[5].trim().toDouble()
                dataAsObjectList.add(ExpenseData(id, date!!, modeOfPayment, payee, purpose, amount,monthAndYear))
            }
        }
    }

    fun sortDataBasedOnLatestFirst()
    {
        finalDataList.clear()
        sortedList = dataAsObjectList.sortedByDescending { it.date }
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun sortDataBasedOnOldestFirst()
    {
        finalDataList.clear()
        sortedList = dataAsObjectList.sortedBy { it.date }
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun sortDataBasedOnAmountDescending()
    {
        finalDataList.clear()
        sortedList = dataAsObjectList.sortedWith(compareBy { it.amount })
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun sortDataBasedOnAmountAscending()
    {
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