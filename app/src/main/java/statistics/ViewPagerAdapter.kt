package statistics

import  androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val TOTAL_TAB_COUNT = 2

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return TOTAL_TAB_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        when(position)
        {
            0-> { return WholeStatisticsFragment() }
        }
        return MonthlyStatisticsFragment()
    }


}