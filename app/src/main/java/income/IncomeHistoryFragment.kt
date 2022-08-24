package income

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialassistant.databinding.FragmentIncomeHistoryBinding
import utils.Common.toastShort
import java.util.*

class IncomeHistoryFragment : Fragment() {

    private lateinit var binding: FragmentIncomeHistoryBinding
    private lateinit var viewModel: IncomeHistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentIncomeHistoryBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[IncomeHistoryViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        binding.recylerViewIncomes.layoutManager = LinearLayoutManager(requireContext())
        binding.recylerViewIncomes.adapter = viewModel.adapter

        viewModel.getData()

        binding.spinnerSortIncome.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position==0)
                {
                    binding.tvFilterByIncome.visibility = View.INVISIBLE
                    binding.tvSelectMonthFilterIncome.visibility = View.INVISIBLE
                    binding.radioGroupIncomeHistory.visibility = View.GONE
                }
                else
                {
                    binding.tvFilterByIncome.visibility = View.VISIBLE
                    binding.tvSelectMonthFilterIncome.visibility = View.VISIBLE
                    viewModel.clearView()
                }
                when(position)
                {
                    0-> { viewModel.clearView() }
                    1-> { viewModel.sortDataBasedOnLatestFirst() }
                    2-> { viewModel.sortDataBasedOnOldestFirst() }
                    3-> { viewModel.sortDataBasedOnAmountAscending() }
                    4-> { viewModel.sortDataBasedOnAmountDescending() }
                }
                toastShort(requireContext(), parent.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        binding.tvSelectMonthFilterIncome.setOnClickListener {
            DatePickerDialog(requireActivity(),
                viewModel.dateSetListener,
                viewModel.cal.get(Calendar.YEAR),
                viewModel.cal.get(Calendar.MONTH),
                viewModel.cal.get(Calendar.DAY_OF_MONTH)
            ).show()

            if(binding.radioBtnRecordOfExactDateIncome.isChecked || binding.radioBtnRecordOfWholeMonthIncome.isChecked)
            {
                binding.radioGroupIncomeHistory.clearCheck()
                viewModel.clearView()
            }
        }

        binding.radioGroupIncomeHistory.setOnCheckedChangeListener { group, checkedId ->
            val radioBtn: RadioButton? = group.findViewById(checkedId)
            when (radioBtn?.text.toString())
            {
                "Show records of exact date" -> { viewModel.filterByExactDate() }
                "Show records of whole month" -> { viewModel.filterByMonth() }
            }
        }

        return binding.root
    }

}