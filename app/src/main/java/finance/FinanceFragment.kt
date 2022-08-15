package finance

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.R
import com.example.financialassistant.databinding.FragmentFinanceBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import utils.Common

class FinanceFragment : Fragment() {

    private lateinit var binding: FragmentFinanceBinding
    private lateinit var viewModel: FinanceViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFinanceBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[FinanceViewModel::class.java]

        lifecycleScope.launch {
            viewModel.loadFinanceData()
        }

        Common.setUpLogger()

        lifecycleScope.launchWhenStarted {

            viewModel.stateFlow
                .filter {
                    it.inBankData.size > 0
                    it.inHandData.size > 0
                }
                .collectLatest {
                    binding.tvCashInBank.text = it.inBankData[0]
                    binding.tvCreditCardExpense.text = "Credit card expenditure : ${it.inBankData[1]}"
                    binding.tvCashInHand.text = it.inHandData[0]
                    binding.tvAsCash.text = "As cash : ${it.inHandData[1]}"
                    binding.tvInDigitalWallet.text = "In digital wallet : ${it.inHandData[2]}"
                    binding.tvCashInTotal.text = it.totalMoney
                }
        }

        binding.expandInBank.setOnClickListener {
            if(binding.expandableInBank.visibility == View.GONE)
            {
                changeDrawable(binding.expandInBank,true)
                binding.expandableInBank.visibility = View.VISIBLE
            }
            else
            {
                changeDrawable(binding.expandInBank,false)
                binding.expandableInBank.visibility = View.GONE
            }

        }

        binding.expandInHand.setOnClickListener {

            binding.etParentIncomeOtherSource.visibility = View.GONE

            if(binding.expandableInHand.visibility == View.GONE)
            {
                changeDrawable(binding.expandInHand,true)
                binding.expandableInHand.visibility = View.VISIBLE
            }
            else
            {
                changeDrawable(binding.expandInHand,false)
                binding.expandableInHand.visibility = View.GONE
            }

        }

        binding.etAmountFromOtherSource.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etParentIncomeOtherSource.visibility = View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })



        return binding.root
    }

    private fun changeDrawable(imageView: ImageView, state: Boolean)
    {
        if(state)
            imageView.setImageResource(R.drawable.collapse)
        else
            imageView.setImageResource(R.drawable.expand)
    }


}