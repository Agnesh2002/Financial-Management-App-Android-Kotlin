package authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import repositories.AuthenticationRepository
import utils.database.Database

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {

    var etUsername = ""
    var etEmail = ""
    var etPassword = ""
    var etConfirmPassword = ""
    private val _stateFlowMsg:MutableStateFlow<Int> = MutableStateFlow(0)
    val stateFlowMsg = _stateFlowMsg.asStateFlow()
    var errorMsg = "This field is required"
    private val authenticationRepository = AuthenticationRepository()
    var pBarVisibility = MutableLiveData<Boolean>()
    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()
    private var db: Database? =null
    var dbData: MutableLiveData<Boolean> = MutableLiveData()
    var checkBoxState = MutableStateFlow(false)

    fun checkForLogin()
    {
        viewModelScope.launch(Dispatchers.Default) {
            db = Room.databaseBuilder(getApplication(), Database::class.java, "userdb").build()
            dbData.postValue(db?.accessDao()!!.getData())
        }
    }

    private fun validate(): Boolean
    {
        etUsername = etUsername.trim()
        etConfirmPassword = etConfirmPassword.trim()

        if(etUsername.isEmpty() || etUsername == "")
        {
            _stateFlowMsg.value = 1
            return false
        }

        if(!validateEmailAndPassword())
            return false

        if(etConfirmPassword.isEmpty() || etConfirmPassword == "")
        {
            _stateFlowMsg.value = 4
            return false
        }
        return true

    }

    private fun validateEmailAndPassword(): Boolean
    {
        etEmail = etEmail.trim()
        etPassword = etPassword.trim()

        if(etEmail.isEmpty() || etEmail == "")
        {
            _stateFlowMsg.value = 2
            return false
        }
        if(etPassword.isEmpty() || etPassword == "")
        {
            _stateFlowMsg.value = 3
            return false
        }
        return true
    }

    fun register()
    {
        pBarVisibility.postValue(true)
        if(!validate()) {
            pBarVisibility.postValue(false)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.registerUser(etUsername, etEmail, etPassword)
            startObserving()
        }
    }

    fun login()
    {
        pBarVisibility.postValue(true)
        if(!validateEmailAndPassword()) {
            pBarVisibility.postValue(false)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.loginUser(etEmail, etPassword)
            startObserving()
        }
    }

    fun performLogout() {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.logoutUser(getApplication())
            startObserving()
        }
    }

    fun forgotPassword()
    {
        etEmail = etEmail.trim()
        if(etEmail.isEmpty() || etEmail == "")
        {
            _stateFlowMsg.value = 2
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.resetPassword(etEmail)
            startObserving()
        }
    }

    fun getDisplayInfo()
    {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.getUserInfo()
            startObserving()
        }
    }

    private suspend fun startObserving()
    {
        authenticationRepository.stateFlow.collectLatest {
            pBarVisibility.postValue(false)
            _sharedFlow.emit(it)
        }
    }


}