package main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import authentication.AuthenticationActivity
import authentication.AuthenticationViewModel
import com.example.financialassistant.R
import com.example.financialassistant.databinding.ActivityHomeBinding
import com.orhanobut.logger.Logger
import exchangerates.ExchangeRateFragment
import expenditure.ExpenditureHistoryFragment
import finance.FinanceFragment
import income.IncomeHistoryFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import profile.ProfileFragment
import setup.SetupFragment
import statistics.StatisticsFragment
import transfers.TransferHistoryFragment
import utils.Common
import utils.Common.setUpLogger
import utils.Common.toastShort
import withdraws.WithdrawHistoryFragment


class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]

        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(this, binding.navDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.navDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        viewModel.getDisplayInfo()

        setUpLogger()
        val headerLayout = binding.navView.inflateHeaderView(R.layout.nav_header)
        val tvHeaderUsername: TextView = headerLayout.findViewById(R.id.header_tv_user_name)
        val tvHeaderEmail: TextView = headerLayout.findViewById(R.id.header_tv_user_email)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentChange(HomeFragment())

        binding.navView.itemIconTintList = null

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.nav_home -> { fragmentChange(HomeFragment()) }
                R.id.nav_exchange_rate -> { fragmentChange(ExchangeRateFragment()) }
                R.id.nav_expenditure_history -> { fragmentChange(ExpenditureHistoryFragment()) }
                R.id.nav_income_history -> { fragmentChange(IncomeHistoryFragment()) }
                R.id.nav_withdraw_history -> { fragmentChange(WithdrawHistoryFragment()) }
                R.id.nav_transfer_history -> { fragmentChange(TransferHistoryFragment()) }
                R.id.nav_finance -> { fragmentChange(FinanceFragment()) }
                R.id.nav_statistics -> { fragmentChange(StatisticsFragment()) }
                R.id.nav_setup -> { fragmentChange(SetupFragment()) }
                R.id.nav_profile -> { fragmentChange(ProfileFragment()) }
                R.id.nav_logout -> { performLogout() }
            }
            true
        }

        lifecycleScope.launchWhenStarted {
            viewModel.sharedFlow.collectLatest {
                if (it.contains("Logout")) {
                    val i = Intent(application.applicationContext, AuthenticationActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NO_HISTORY + Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    finish()
                }
                else if (it.contains("Hi"))
                {
                    tvHeaderUsername.text = Common.headerUname
                    tvHeaderEmail.text = Common.headerEmail
                    delay(2000)
                    toastShort(applicationContext, "$it.")
                }
                else
                {
                    toastShort(applicationContext, "$it.")
                }
            }
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

    private fun performLogout() {

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout ? ")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { _, _ ->
            this.finish()
            viewModel.performLogout()
        }

        builder.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

    }

}