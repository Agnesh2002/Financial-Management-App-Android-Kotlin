package authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repositories.AuthenticationRepository

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {

    var etUsername = ""
    var etEmail = ""
    var etPassword = ""
    var etConfirmPassword = ""
    private var _stateFlowMsg:MutableStateFlow<Int> = MutableStateFlow(0)
    var stateFlowMsg = _stateFlowMsg.asStateFlow()
    var errorMsg = "This field is required"
    private var authenticationRepository = AuthenticationRepository()

    private fun validate()
    {
        etUsername = etUsername.trim()
        etEmail = etEmail.trim()
        etPassword = etPassword.trim()
        etConfirmPassword = etConfirmPassword.trim()

        validateEmailAndPassword()

        if(etUsername.isEmpty() || etUsername == "")
        {
            _stateFlowMsg.value = 1
            return
        }

        if(etConfirmPassword.isEmpty() || etConfirmPassword == "")
        {
            _stateFlowMsg.value = 4
            return
        }

    }

    private fun validateEmailAndPassword()
    {
        if(etEmail.isEmpty() || etEmail == "")
        {
            _stateFlowMsg.value = 2
            return
        }
        if(etPassword.isEmpty() || etPassword == "")
        {
            _stateFlowMsg.value = 3
            return
        }
    }

    fun register()
    {
        validate()
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.registerUser(getApplication(),etUsername, etEmail, etPassword)
        }
    }

    fun login()
    {
        validateEmailAndPassword()
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.loginUser(getApplication(),etEmail, etPassword)
        }
    }


}