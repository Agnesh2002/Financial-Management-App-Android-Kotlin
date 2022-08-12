package expenditure

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialassistant.databinding.FragmentExpenditureHistoryBinding

class ExpenditureHistoryFragment : Fragment() {

    private lateinit var binding: FragmentExpenditureHistoryBinding
    private lateinit var viewModel: ExpenditureHistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExpenditureHistoryBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[ExpenditureHistoryViewModel::class.java]

        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.expenseRecyclerView.adapter = viewModel.adapter

        viewModel.getData()

        return binding.root
    }


}