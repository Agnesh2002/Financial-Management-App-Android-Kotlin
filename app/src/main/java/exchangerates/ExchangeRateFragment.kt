package exchangerates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.financialassistant.databinding.FragmentExchangeRateBinding

class ExchangeRateFragment : Fragment() {

    private lateinit var binding: FragmentExchangeRateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExchangeRateBinding.inflate(layoutInflater)



        return binding.root
    }


}