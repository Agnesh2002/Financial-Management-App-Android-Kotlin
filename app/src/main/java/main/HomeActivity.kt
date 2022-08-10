package main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.financialassistant.R
import com.example.financialassistant.databinding.ActivityHomeBinding
import exchangerates.ExchangeRateFragment
import expenditure.ExpenditureHistoryFragment
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
        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.nav_home -> { toastShort(applicationContext,"1"); fragmentChange(HomeFragment()) ; revertGravity() }
                R.id.nav_exchange_rate -> { toastShort(applicationContext,"2"); fragmentChange(ExchangeRateFragment()) ; revertGravity() }
                R.id.nav_expenditure_history -> { toastShort(applicationContext,"3"); fragmentChange(ExpenditureHistoryFragment()); revertGravity() }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

    private fun fragmentChange(fragment: Fragment)
    {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_home, fragment).commit()
    }
    private fun revertGravity()
    {
        binding.navDrawerLayout.closeDrawer(GravityCompat.START)
    }

}