package withdraws

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialassistant.R
import java.text.SimpleDateFormat
import java.util.*

class CustomWithdrawHistoryAdapter(private val withdrawList: List<WithdrawData>) : RecyclerView.Adapter<MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_withdraw_history, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val sdf = SimpleDateFormat("dd-MMM-yyy", Locale.US)
        val formattedDate = sdf.format(withdrawList[position].date)
        val amount = withdrawList[position].amount.toString()

        holder.tvDate.text = "Date : $formattedDate"
        holder.tvAmount.text = "Amount : $amount"
    }

    override fun getItemCount(): Int {
        return withdrawList.size
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal val tvDate: TextView = itemView.findViewById(R.id.custom_tv_withdraw_date)
    internal val tvAmount: TextView = itemView.findViewById(R.id.custom_tv_amount_withdrawn)
}