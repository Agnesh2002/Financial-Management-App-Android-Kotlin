package authentication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.financialassistant.R
import com.example.financialassistant.databinding.ActivityAuthenticationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import main.HomeActivity
import utils.Common.toastShort
import utils.database.Database

class AuthenticationActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val status = intent.getBooleanExtra("STATUS",false)

        if(status)
        {
            val i = Intent(applicationContext, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_NO_ANIMATION
            ContextCompat.startActivity(applicationContext, i, Bundle())
            this.finish()
        }
        else
        {
            fragmentChange()
            toastShort(applicationContext, "Please login")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.title = "My Financial Tracker"
                fragmentChange()
            }
        }
        return false
    }

    private fun fragmentChange() {
        supportFragmentManager.popBackStack()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_authentication, LoginFragment()).commit()

    }
}