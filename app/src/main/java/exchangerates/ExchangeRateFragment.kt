package exchangerates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.databinding.FragmentExchangeRateBinding
import kotlinx.coroutines.flow.collectLatest

class ExchangeRateFragment : Fragment() {

    private lateinit var binding: FragmentExchangeRateBinding
    private lateinit var viewModel: ExchangeRateViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExchangeRateBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[ExchangeRateViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collectLatest {
                when(it)
                {
                    1-> { binding.etFromCurrency.error = viewModel.errorMsg }
                    2-> { binding.etToCurrency.error = viewModel.errorMsg }
                    3-> { binding.etValueCurrency.error = viewModel.errorMsg }
                }
            }
        }


        return binding.root
    }


}