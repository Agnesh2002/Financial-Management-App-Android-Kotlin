package authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.flow.collectLatest
import utils.Common.toastShort

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlowMsg.collectLatest {
                when (it)
                {
                    0 -> { toastShort(requireContext(),"Fill out the fields and register") }
                    1 -> { binding.etUsername.error = viewModel.errorMsg }
                    2 -> { binding.etEmail.error = viewModel.errorMsg }
                    3 -> { binding.etPassword.error = viewModel.errorMsg }
                    4 -> {
                        binding.etConfirmPassword.error = "Passwords does not match"
                        viewModel.etPassword = ""
                        viewModel.etConfirmPassword = ""
                    }
                }
            }
        }

        return binding.root
    }

}