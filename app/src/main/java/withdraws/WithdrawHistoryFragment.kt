package withdraws

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
import com.example.financialassistant.databinding.FragmentWithdrawHistoryBinding
import utils.Common
import java.util.*

class WithdrawHistoryFragment : Fragment() {

    private lateinit var binding: FragmentWithdrawHistoryBinding
    private lateinit var viewModel: WithdrawHistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWithdrawHistoryBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[WithdrawHistoryViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        binding.withdrawRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.withdrawRecyclerView.adapter = viewModel.adapter

        viewModel.getData()

        binding.spinnerSortWithdraw.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position==0)
                {
                    binding.tvFilterByWithdraw.visibility = View.INVISIBLE
                    binding.tvSelectMonthFilterWithdraw.visibility = View.INVISIBLE
                    binding.radioGroupWithdrawHistory.visibility = View.GONE
                }
                else
                {
                    binding.tvFilterByWithdraw.visibility = View.VISIBLE
                    binding.tvSelectMonthFilterWithdraw.visibility = View.VISIBLE
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
                Common.toastShort(requireContext(), parent.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        binding.tvSelectMonthFilterWithdraw.setOnClickListener {
            DatePickerDialog(requireActivity(),
                viewModel.dateSetListener,
                viewModel.cal.get(Calendar.YEAR),
                viewModel.cal.get(Calendar.MONTH),
                viewModel.cal.get(Calendar.DAY_OF_MONTH)
            ).show()

            if(binding.radioBtnRecordOfExactDateWithdraw.isChecked || binding.radioBtnRecordOfWholeMonthWithdraw.isChecked)
            {
                binding.radioGroupWithdrawHistory.clearCheck()
                viewModel.clearView()
            }
        }

        binding.radioGroupWithdrawHistory.setOnCheckedChangeListener { group, checkedId ->
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