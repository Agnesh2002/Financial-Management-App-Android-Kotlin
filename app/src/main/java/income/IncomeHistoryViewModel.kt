package income

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.financialassistant.R
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import repositories.IncomeRepository

class IncomeHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val arrayList = arrayListOf("Make a selection", "Latest first", "Oldest first", "Big amount first", "Small amount first")
    val spinnerAdapter = ArrayAdapter(getApplication(), android.R.layout.simple_list_item_1, arrayList)
    private val incomeRepository = IncomeRepository()

    private val incomeListFromRepo = arrayListOf<String>()
    private val dataAsObjectList = arrayListOf<IncomeData>()

    private var sortedList = listOf<IncomeData>()
    private var finalDataList = arrayListOf<IncomeData>()
    var adapter = CustomIncomeHistoryAdapter(finalDataList)


    fun getData()
    {
        viewModelScope.launch {
            incomeRepository.getIncomeData()
            incomeListFromRepo.clear()
            for(expenseList in incomeRepository.listOfIncomes)
            {
                val separated = expenseList.split("],")
                for (expense in separated)
                    incomeListFromRepo.add(expense)
            }
            convertToListOfObjects()
        }
    }

    private fun convertToListOfObjects()
    {
        viewModelScope.launch(Dispatchers.Default) {
            for (position in 0 until incomeListFromRepo.size)
            {
                val fields = incomeListFromRepo[position].replace("[","").replace("]","").split(",")
                val id = fields[0].trim()
                val date = fields[1].trim()
                val amount = fields[2].trim().toDouble()
                val source = fields[3].trim()
                val modeOfIncome = fields[4].trim()
                dataAsObjectList.add(IncomeData(id,date,amount,source,modeOfIncome))
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