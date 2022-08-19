package authentication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.financialassistant.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import main.HomeActivity
import utils.database.Database

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        lifecycleScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(applicationContext, Database::class.java, "userdb").build()
            val data = db.accessDao().getData()
            if(data)
            {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY + Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            else
            {
                fragmentChange(LoginFragment())
            }
        }
    }

    private fun fragmentChange(fragment: Fragment)
    {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_authentication, fragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                fragmentChange(LoginFragment())
            }
        }
        return false
    }
}