package statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.financialassistant.databinding.FragmentStatisticsBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import utils.Common


class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var viewModel: StatisticsViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private val tabArray = arrayListOf("Whole Statistics", "Monthly Statistics")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStatisticsBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        Common.setUpLogger()

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        viewPager.isUserInputEnabled = false

        tabLayout.addTab(tabLayout.newTab().setText("WHOLE STATISTICS"))
        tabLayout.addTab(tabLayout.newTab().setText("MONTHLY STATISTICS"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = ViewPagerAdapter(parentFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            tab.text = tabArray[position]
        }.attach()


        return binding.root
    }

}