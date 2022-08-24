package statistics

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.R
import com.example.financialassistant.databinding.FragmentWholeStatisticsBinding
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.eazegraph.lib.communication.IOnItemFocusChangedListener
import org.eazegraph.lib.models.PieModel
import utils.Common.setUpLogger

class WholeStatisticsFragment : Fragment() {

    private lateinit var binding: FragmentWholeStatisticsBinding
    private lateinit var viewModel: StatisticsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWholeStatisticsBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        val mPieChart = binding.piechart

        lifecycleScope.launch {
            mPieChart.clearChart()
            viewModel.getWholeStatistics()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.data.collectLatest {

                binding.tvWholeExpenditureCount.text = it.expenditureCount.toString()
                binding.tvWholeExpenditureAmount.text = "₹ ${it.expenditureAmountValue}"

                binding.tvWholeIncomeCount.text = it.incomeCount.toString()
                binding.tvWholeIncomeAmount.text = "₹ ${it.incomeAmountValue}"

                mPieChart.isUseCustomInnerValue = true
                mPieChart.addPieSlice(PieModel("Total expenditure amount",it.expenditureAmountValue.toFloat(), Color.RED))
                mPieChart.addPieSlice(PieModel("Total income amount", it.incomeAmountValue.toFloat(), Color.GREEN))

                if(it.incomeCount.toString() == "0")
                    mPieChart.innerValueString = "₹ ${it.expenditureAmountValue}"
                else if(it.expenditureCount.toString() == "0")
                    mPieChart.innerValueString = "₹ ${it.incomeAmountValue}"
                else
                    mPieChart.innerValueString = "Rotate Me"
                mPieChart.startAnimation()

                mPieChart.setOnItemFocusChangedListener(object : IOnItemFocusChangedListener {
                    override fun onItemFocusChanged(_Position: Int) {
                        when(_Position)
                        {
                            3-> { mPieChart.innerValueString = "₹ ${it.incomeAmountValue}" }
                            2-> { mPieChart.innerValueString = "₹ ${it.expenditureAmountValue}" }
                        }
                    }
                })
            }
        }

        return binding.root
    }

}