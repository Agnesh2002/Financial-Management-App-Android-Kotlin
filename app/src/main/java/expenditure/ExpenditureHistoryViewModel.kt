package expenditure

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.financialassistant.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import repositories.ExpenditureRepository


class ExpenditureHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private var listOfExpenses = arrayListOf<String>()
    var adapter = CustomExpenditureAdapter(listOfExpenses)
    private val expenditureRepository = ExpenditureRepository()

    class CustomExpenditureAdapter(private val list: List<String>) : RecyclerView.Adapter<MyViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_expenditure_history, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            val fields = list[position].replace("[","").replace("]","").split(",")

            val date = fields[1].trim()
            val modeOfPayment = fields[2].trim()
            val payee = fields[3].trim()
            val purpose = fields[4].trim()
            val amount = fields[5].trim()

            holder.tvDate.text = "Date : $date"
            holder.tvModeOfPayment.text = "Mode of Payment : $modeOfPayment"
            holder.tvPayee.text = "Payee : $payee"
            holder.tvPurpose.text = "Purpose : $purpose"
            holder.tvAmount.text = "Amount : $amount"
        }

        override fun getItemCount(): Int {
            return list.size
        }


    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val tvDate: TextView = itemView.findViewById(R.id.custom_tv_expense_date)
        internal val tvModeOfPayment: TextView = itemView.findViewById(R.id.custom_tv_mode_of_payment)
        internal val tvPayee: TextView = itemView.findViewById(R.id.custom_tv_payee)
        internal val tvPurpose: TextView = itemView.findViewById(R.id.custom_tv_pupose)
        internal val tvAmount: TextView = itemView.findViewById(R.id.custom_tv_amount_spent)
    }

    fun getData()
    {
        viewModelScope.launch(Dispatchers.IO){

            expenditureRepository.getExpenseData()
            listOfExpenses.clear()
            for(expenseList in expenditureRepository.listOfExpensesFromRepo)
            {
                val separated = expenseList.split("],")
                for (expense in separated)
                    listOfExpenses.add(expense)
            }
            withContext(Dispatchers.Main)
            {
                adapter.notifyDataSetChanged()
            }
        }

    }



}