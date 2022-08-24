package authentication

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.financialassistant.R
import com.example.financialassistant.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import utils.Common.toastShort
import utils.database.Database
import utils.database.LoginData

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
            viewModel.sharedFlow.collectLatest {
                toastShort(requireContext(), it)
            }
        }

        binding.imgRegShow1.setOnClickListener {
            if (binding.etPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgRegShow1.setImageResource(R.drawable.hide)
                setCursorEnd()
            } else {
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgRegShow1.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                setCursorEnd()
            }
        }

        binding.imgRegShow2.setOnClickListener {
            if (binding.etConfirmPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.etConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgRegShow2.setImageResource(R.drawable.hide)
                setCursorEnd()
            } else {
                binding.etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgRegShow2.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                setCursorEnd()
            }
        }

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

    private fun setCursorEnd()
    {
        binding.etPassword.setSelection(binding.etPassword.length())
        binding.etConfirmPassword.setSelection(binding.etConfirmPassword.length())
    }

}