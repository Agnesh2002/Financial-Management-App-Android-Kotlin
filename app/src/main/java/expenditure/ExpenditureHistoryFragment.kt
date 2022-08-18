package expenditure

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialassistant.databinding.FragmentExpenditureHistoryBinding
import utils.Common
import java.util.*

class ExpenditureHistoryFragment : Fragment() {

    private lateinit var binding: FragmentExpenditureHistoryBinding
    private lateinit var viewModel: ExpenditureHistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExpenditureHistoryBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[ExpenditureHistoryViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.expenseRecyclerView.adapter = viewModel.adapter

        viewModel.getData()

        binding.spinnerSortExpense.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position==0)
                {
                    binding.tvFilterByExpense.visibility = View.INVISIBLE
                    binding.tvSelectMonthFilterExpense.visibility = View.INVISIBLE
                    binding.radioGroupExpenditureHistory.visibility = View.GONE
                }
                else
                {
                    binding.tvFilterByExpense.visibility = View.VISIBLE
                    binding.tvSelectMonthFilterExpense.visibility = View.VISIBLE
                }
                when(position)
                {
                    0-> { viewModel.clearView() }
                    1-> { viewModel.sortDataBasedOnLatestFirst() }
                    2-> { viewModel.sortDataBasedOnOldestFirst() }
                    3-> { viewModel.sortDataBasedOnAmountAscending() }
                    4-> { viewModel.sortDataBasedOnAmountDescending() }
                }
                Common.toastShort(requireContext(), parent.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        binding.tvSelectMonthFilterExpense.setOnClickListener {
            DatePickerDialog(requireActivity(),
                viewModel.dateSetListener,
                viewModel.cal.get(Calendar.YEAR),
                viewModel.cal.get(Calendar.MONTH),
                viewModel.cal.get(Calendar.DAY_OF_MONTH)
            ).show()

            if(binding.radioBtnRecordOfExactDateExpense.isChecked || binding.radioBtnRecordOfWholeMonthExpense.isChecked)
            {
                binding.radioGroupExpenditureHistory.clearCheck()
                viewModel.clearView()
            }
        }

        binding.radioGroupExpenditureHistory.setOnCheckedChangeListener { group, checkedId ->
            val radioBtn: RadioButton? = group.findViewById(checkedId)
            when (radioBtn?.text.toString())
            {
                "Record of exact date" -> { viewModel.filterByExactDate() }
                "Record of whole month" -> { viewModel.filterByMonth() }
            }
        }

        return binding.root
    }


}