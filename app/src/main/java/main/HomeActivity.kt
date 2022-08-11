package main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.R
import com.example.financialassistant.databinding.ActivityHomeBinding
import exchangerates.ExchangeRateFragment
import expenditure.ExpenditureHistoryFragment
import finance.FinanceFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.Common.toastShort

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(this, binding.navDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.navDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentChange(HomeFragment())

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.nav_home -> { fragmentChange(HomeFragment()) }
                R.id.nav_exchange_rate -> { fragmentChange(ExchangeRateFragment()) }
                R.id.nav_expenditure_history -> { fragmentChange(ExpenditureHistoryFragment()) }
                R.id.nav_finance -> { fragmentChange(FinanceFragment()) }
            }
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    private fun fragmentChange(fragment: Fragment) {

        lifecycleScope.launch {
            binding.navDrawerLayout.closeDrawer(GravityCompat.START)
            delay(350)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_home, fragment).commit()
        }
    }

}