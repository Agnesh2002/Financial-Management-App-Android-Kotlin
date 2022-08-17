package setup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.databinding.FragmentSetupBinding
import kotlinx.coroutines.flow.collectLatest

class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding
    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSetupBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SetupViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlowMsg.collectLatest {
                when(it)
                {
                    1-> {binding.etBankBalance.error = viewModel.errorMsg}
                    2-> {binding.etCashInWallet.error = viewModel.errorMsg}
                    3-> {binding.etAmountInDigitalWallet.error = viewModel.errorMsg}
                    4-> {binding.etCreditCardExpenditure.error = viewModel.errorMsg}
                }
            }
        }

        return binding.root
    }


}