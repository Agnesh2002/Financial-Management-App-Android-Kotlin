package statistics

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.R
import com.example.financialassistant.databinding.FragmentMonthlyStatisticsBinding
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.eazegraph.lib.communication.IOnItemFocusChangedListener
import org.eazegraph.lib.models.PieModel
import utils.Common.toastShort
import java.text.SimpleDateFormat
import java.util.*

class MonthlyStatisticsFragment : Fragment() {

    private lateinit var binding: FragmentMonthlyStatisticsBinding
    private lateinit var viewModel: StatisticsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMonthlyStatisticsBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        val mPieChart = binding.piechart

        binding.tvStatisticsSelectMonthAndYear.setOnClickListener {
            if(this.isAdded)
            {
                MonthYearPickerDialog().apply {
                    setListener { _, year, month, _ ->
                        val sdf = SimpleDateFormat("MM-yyyy", Locale.US)
                        val selectedDate = sdf.parse("$month-$year")
                        val sdf2 = SimpleDateFormat("MMM-yyyy", Locale.US)
                        binding.tvStatisticsSelectMonthAndYear.text = sdf2.format(selectedDate)
                        binding.btnGetMonthlyStatistics.visibility = View.VISIBLE
                    }
                    show(this@MonthlyStatisticsFragment.parentFragmentManager, "MonthYearPickerDialog")
                }
            }
        }

        binding.btnGetMonthlyStatistics.setOnClickListener {
            if(binding.btnGetMonthlyStatistics.text == "get")
            {
                lifecycleScope.launch {
                    viewModel.getMonthlyStatistics(viewModel.dateText.value)
                }
                binding.tvStatisticsSelectMonthAndYear.isClickable = false
                binding.btnGetMonthlyStatistics.text = "reset"
            }
            else
            {
                binding.btnGetMonthlyStatistics.visibility = View.INVISIBLE
                binding.tvStatisticsSelectMonthAndYear.isClickable = true
                binding.tvStatisticsSelectMonthAndYear.text = "Select month and year"
                binding.btnGetMonthlyStatistics.text = "get"
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.data.collectLatest {
                mPieChart.clearChart()
                binding.tvMonthlyExpenditureCount.text = it.expenditureCount.toString()
                binding.tvMonthlyExpenditureAmount.text = "₹ ${it.expenditureAmountValue}"

                binding.tvMonthlyIncomeCount.text = it.incomeCount.toString()
                binding.tvMonthlyIncomeAmount.text = "₹ ${it.incomeAmountValue}"

                Logger.w(it.expenditureAmountValue.toString())

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

                Logger.w("$roundedExpenditure || $roundedIncome")

                mPieChart.addPieSlice(PieModel("Expenditure amount of ${viewModel.dateText.value} ",roundedExpenditure, Color.RED))
                mPieChart.addPieSlice(PieModel("Income amount of ${viewModel.dateText.value} ", roundedIncome, Color.GREEN))
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
                            3-> { mPieChart.innerValueString = "₹ ${it.incomeAmountValue}" }
                            2-> { mPieChart.innerValueString = "₹ ${it.expenditureAmountValue}" }
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