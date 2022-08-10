package expenditure

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.financialassistant.databinding.FragmentExpenditureHistoryBinding

class ExpenditureHistoryFragment : Fragment() {

    private lateinit var binding: FragmentExpenditureHistoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExpenditureHistoryBinding.inflate(layoutInflater)



        return binding.root
    }


}