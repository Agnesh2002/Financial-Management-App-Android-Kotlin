package exchangerates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.financialassistant.R
import com.example.financialassistant.databinding.FragmentExchangeRateBinding

class ExchangeRateFragment : Fragment() {

    private lateinit var binding: FragmentExchangeRateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExchangeRateBinding.inflate(layoutInflater)

        val currencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, requireContext().resources.getStringArray(R.array.array_currency_codes))
        binding.etFromCurrency.setAdapter(currencyAdapter)
        binding.etToCurrency.setAdapter(currencyAdapter)

        return binding.root
    }


}