package expenditure

import android.R
import android.app.Application
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import repositories.ExpenditureRepository


class ExpenditureHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val arrayList = arrayListOf("Make a selection", "Latest first", "Oldest first", "Big amount first", "Small amount first")
    val spinnerAdapter = ArrayAdapter(getApplication(), R.layout.simple_list_item_1, arrayList)
    private val expenditureRepository = ExpenditureRepository()

    private var expenseListFromRepo = arrayListOf<String>()
    private val dataAsObjectList = arrayListOf<ExpenseData>()

    private var sortedList = listOf<ExpenseData>()
    private val finalDataList = arrayListOf<ExpenseData>()
    var adapter = CustomExpenditureAdapter(finalDataList)

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
                val date = fields[1].trim()
                val modeOfPayment = fields[2].trim()
                val payee = fields[3].trim()
                val purpose = fields[4].trim()
                val amount = fields[5].trim().toDouble()
                dataAsObjectList.add(ExpenseData(id, date, modeOfPayment, payee, purpose, amount))
            }
        }
    }

    fun sortDataBasedOnLatestFirst()
    {
        finalDataList.clear()
        sortedList = dataAsObjectList.sortedWith(compareByDescending { it.id })
        for (item in sortedList)
            finalDataList.add(item)

        adapter.notifyDataSetChanged()
    }

    fun sortDataBasedOnOldestFirst()
    {
        finalDataList.clear()
        sortedList = dataAsObjectList.sortedWith(compareBy { it.id })
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

}