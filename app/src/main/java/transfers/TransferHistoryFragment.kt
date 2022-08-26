package transfers

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
import com.example.financialassistant.databinding.FragmentTransferHistoryBinding
import utils.Common
import java.util.*

class TransferHistoryFragment : Fragment() {

    private lateinit var binding: FragmentTransferHistoryBinding
    private lateinit var viewModel: TransferHistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTransferHistoryBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[TransferHistoryViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        binding.transferRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transferRecyclerView.adapter = viewModel.adapter

        viewModel.getData()

        binding.spinnerSortTransfer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position==0)
                {
                    binding.tvFilterByTransfer.visibility = View.INVISIBLE
                    binding.tvSelectMonthFilterTransfer.visibility = View.INVISIBLE
                    binding.radioGroupTransferHistory.visibility = View.GONE
                }
                else
                {
                    binding.tvFilterByTransfer.visibility = View.VISIBLE
                    binding.tvSelectMonthFilterTransfer.visibility = View.VISIBLE
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

        binding.tvSelectMonthFilterTransfer.setOnClickListener {
            DatePickerDialog(requireActivity(),
                viewModel.dateSetListener,
                viewModel.cal.get(Calendar.YEAR),
                viewModel.cal.get(Calendar.MONTH),
                viewModel.cal.get(Calendar.DAY_OF_MONTH)
            ).show()

            if(binding.radioBtnRecordOfExactDateTransfer.isChecked || binding.radioBtnRecordOfWholeMonthTransfer.isChecked)
            {
                binding.radioGroupTransferHistory.clearCheck()
                viewModel.clearView()
            }
        }

        binding.radioGroupTransferHistory.setOnCheckedChangeListener { group, checkedId ->
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


