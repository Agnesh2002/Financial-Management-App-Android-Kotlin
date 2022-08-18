package expenditure

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialassistant.R
import java.text.SimpleDateFormat
import java.util.*

class CustomExpenditureAdapter(private val expenseList: List<ExpenseData>) : RecyclerView.Adapter<MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_expenditure_history, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val sdf = SimpleDateFormat("dd-MMM-yyy", Locale.US)
        val formattedDate = sdf.format(expenseList[position].date)
        val modeOfPayment = expenseList[position].modeOfExpense
        val payee = expenseList[position].payee
        val purpose = expenseList[position].purpose
        val amount = expenseList[position].amount.toString()

        holder.tvDate.text = "Date : $formattedDate"
        holder.tvModeOfPayment.text = "Mode of Payment : $modeOfPayment"
        holder.tvPayee.text = "Payee : $payee"
        holder.tvPurpose.text = "Purpose : $purpose"
        holder.tvAmount.text = "Amount : $amount"
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

}


class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal val tvDate: TextView = itemView.findViewById(R.id.custom_tv_expense_date)
    internal val tvModeOfPayment: TextView = itemView.findViewById(R.id.custom_tv_mode_of_payment)
    internal val tvPayee: TextView = itemView.findViewById(R.id.custom_tv_payee)
    internal val tvPurpose: TextView = itemView.findViewById(R.id.custom_tv_purpose)
    internal val tvAmount: TextView = itemView.findViewById(R.id.custom_tv_amount_spent)
}