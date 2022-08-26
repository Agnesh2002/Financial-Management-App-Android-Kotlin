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
import utils.Common.toastShort
import kotlin.math.roundToInt

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
            viewModel.getWholeStatistics()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.data.collectLatest {
                mPieChart.clearChart()
                binding.tvWholeExpenditureCount.text = it.expenditureCount.toString()
                binding.tvWholeExpenditureAmount.text = "₹ ${it.expenditureAmountValue}"

                binding.tvWholeIncomeCount.text = it.incomeCount.toString()
                binding.tvWholeIncomeAmount.text = "₹ ${it.incomeAmountValue}"

                val total = it.incomeAmountValue + it.expenditureAmountValue
                val percentOfExpenditure = (it.expenditureAmountValue/total)*100
                val percentOfIncome = (it.incomeAmountValue/total)*100

                var roundedExpenditure = String.format("%.2f",percentOfExpenditure).toFloat()
                var roundedIncome = String.format("%.2f",percentOfIncome).toFloat()

                if(roundedExpenditure < 1)
                    roundedExpenditure+=1
                if(roundedIncome < 1)
                    roundedIncome+=1
                if(roundedExpenditure > 99)
                    roundedExpenditure-=roundedIncome
                if(roundedIncome > 99)
                    roundedIncome-=roundedExpenditure


                mPieChart.addPieSlice(PieModel("Total expenditure amount",roundedExpenditure, Color.RED))
                mPieChart.addPieSlice(PieModel("Total income amount", roundedIncome, Color.GREEN))
                mPieChart.isUseCustomInnerValue = true
                if(it.incomeCount.toString() == "0")
                    mPieChart.innerValueString = "₹ ${it.expenditureAmountValue}"
                else if(it.expenditureCount.toString() == "0")
                    mPieChart.innerValueString = "₹ ${it.incomeAmountValue}"
                else
                    mPieChart.innerValueString = "Spin Me"

                mPieChart.startAnimation()

                mPieChart.setOnItemFocusChangedListener(object : IOnItemFocusChangedListener {
                    override fun onItemFocusChanged(_Position: Int) {
                        when(_Position)
                        {
                            1-> { mPieChart.innerValueString = "₹ ${it.incomeAmountValue}" }
                            0-> { mPieChart.innerValueString = "₹ ${it.expenditureAmountValue}" }
                        }
                    }
                })
            }
        }


        return binding.root
    }

}