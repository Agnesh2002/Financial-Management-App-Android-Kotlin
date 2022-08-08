package authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.R
import com.example.financialassistant.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.collectLatest
import utils.Common

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        binding.tvRegister.setOnClickListener {
            fragmentChange(RegistrationFragment())
        }

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlowMsg.collectLatest {
                when (it)
                {
                    0 -> {
                        Common.toastShort(requireContext(), "Please login")
                    }
                    2 -> { binding.etEmail.error = viewModel.errorMsg }
                    3 -> { binding.etPassword.error = viewModel.errorMsg }
                }
            }
        }

        return binding.root
    }

    private fun fragmentChange(fragment: Fragment)
    {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_authentication, fragment).commit()
    }


}