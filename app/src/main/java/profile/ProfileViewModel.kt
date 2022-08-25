package profile

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import main.HomeActivity
import repositories.ProfileRepository
import utils.Common.auth
import utils.Common.toastLong
import utils.Common.toastShort

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    var username = ""
    var newPassword = ""
    var confirmNewPassword = ""
    private val profileRepository = ProfileRepository(auth.currentUser?.email!!)
    val errorMsg = "This field is required"
    val validateMsg = MutableStateFlow(0)

    fun validateNewUsername()
    {
        if(username == "" || username.isEmpty())
        {
            validateMsg.value = 1
            return
        }
        changeUsername()
    }

    fun validateNewPassword()
    {
        if(newPassword == "" || newPassword.isEmpty())
        {
            validateMsg.value = 2
            return
        }
        if(confirmNewPassword == "" || confirmNewPassword.isEmpty())
        {
            validateMsg.value = 3
            return
        }
        if(newPassword != confirmNewPassword)
        {
            validateMsg.value = 4
            return
        }
        changePassword()
    }

    private fun changeUsername() {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.updateUsername(username)
        }
        startObserving()
    }

    private fun changePassword()
    {

        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.updatePassword(confirmNewPassword)
        }
        startObserving()
    }

    private fun startObserving()
    {
        viewModelScope.launch {
            profileRepository.stateFlowMsg.collectLatest {
                if(it != "")
                    toastLong(getApplication(),it)
                if(it.contains("Username"))
                {
                    withContext(Dispatchers.Main)
                    {
                        refreshActivity()
                    }
                }
            }
        }
    }

    private fun refreshActivity()
    {
        val i = Intent(getApplication<Application>().applicationContext, HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
        getApplication<Application>().startActivity(i)
    }

}