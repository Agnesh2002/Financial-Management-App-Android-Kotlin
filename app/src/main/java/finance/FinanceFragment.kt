package finance

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.financialassistant.R
import com.example.financialassistant.databinding.FragmentFinanceBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import utils.Common.toastShort

class FinanceFragment : Fragment() {

    private lateinit var binding: FragmentFinanceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFinanceBinding.inflate(layoutInflater)

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