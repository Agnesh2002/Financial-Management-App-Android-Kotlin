package income

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialassistant.R
import java.text.SimpleDateFormat
import java.util.*

class CustomIncomeHistoryAdapter(private val incomeList: List<IncomeData>) : RecyclerView.Adapter<MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_income_history, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
        val formattedDate = sdf.format(incomeList[position].date)
        val amount = incomeList[position].amount.toString()
        val source = incomeList[position].source
        val modeOfIncome = incomeList[position].mode

        holder.date.text = "Date : $formattedDate"
        holder.modeOfIncome.text = "( $modeOfIncome )"
        holder.source.text = "Note : $source"
        holder.amount.text = "Amount received : $amount"
    }

    override fun getItemCount(): Int {
        return incomeList.size
    }

}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    val date: TextView = itemView.findViewById(R.id.custom_tv_income_date)
    val modeOfIncome: TextView = itemView.findViewById(R.id.custom_tv_mode_of_income)
    val source: TextView = itemView.findViewById(R.id.custom_tv_source_of_income)
    val amount: TextView = itemView.findViewById(R.id.custom_tv_income_received)
}