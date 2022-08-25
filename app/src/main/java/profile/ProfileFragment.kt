package profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.R
import com.example.financialassistant.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collectLatest

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        lifecycleScope.launchWhenStarted {
            viewModel.validateMsg.collectLatest {
                when(it)
                {
                    1-> { binding.etNewUsername.error = viewModel.errorMsg }
                    2-> { binding.etNewPass.error = viewModel.errorMsg }
                    3-> { binding.etConfirmNewPass.error = viewModel.errorMsg }
                    4-> { binding.etConfirmNewPass.error = "Passwords does not match" }
                }
            }
        }

        binding.tvChangeUsername.setOnClickListener {
            if(binding.layoutChangePassword.visibility == View.VISIBLE)
            {
                binding.layoutChangePassword.visibility = View.GONE
                binding.layoutChangeUsername.visibility = View.VISIBLE
            }
            else
                binding.layoutChangeUsername.visibility = View.VISIBLE
        }

        binding.tvChangePassword.setOnClickListener {
            if(binding.layoutChangeUsername.visibility == View.VISIBLE)
            {
                binding.layoutChangeUsername.visibility = View.GONE
                binding.layoutChangePassword.visibility = View.VISIBLE
            }
            else
                binding.layoutChangePassword.visibility = View.VISIBLE
        }



        return binding.root
    }

}