package authentication

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.financialassistant.R
import com.example.financialassistant.databinding.FragmentLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import main.HomeActivity
import utils.Common.toastShort
import utils.database.Database
import utils.database.LoginData

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

        binding.imgShow.setOnClickListener {
            if (binding.etPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgShow.setImageResource(R.drawable.hide)
                setCursorEnd()
            } else {
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgShow.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                setCursorEnd()
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.stateFlowMsg.collectLatest {
                when (it)
                {
                    0 -> { toastShort(requireContext(), "Please login") }
                    2 -> { binding.etEmail.error = viewModel.errorMsg }
                    3 -> { binding.etPassword.error = viewModel.errorMsg }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.sharedFlow.collectLatest {
                toastShort(requireContext(), it)
                if(it.contains("Welcome"))
                {
                    if(viewModel.checkBoxState.value)
                    {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val db = Room.databaseBuilder(requireContext(), Database::class.java, "userdb").build()
                            val loginObj =LoginData(1,true)
                            db.accessDao().insertData(loginObj)
                        }
                    }
                    navigateToHomeActivity()
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

    private fun setCursorEnd()
    {
        binding.etPassword.setSelection(binding.etPassword.length())
    }

    private fun navigateToHomeActivity()
    {
        val i = Intent(requireActivity(), HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NO_HISTORY + Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        this.activity?.finish()
    }


}