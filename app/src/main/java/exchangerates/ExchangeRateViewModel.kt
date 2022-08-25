package exchangerates

import android.app.Application
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialassistant.R
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import repositories.ExchangeRateRepository
import utils.Common.setUpLogger
import utils.Common.toastShort

class ExchangeRateViewModel(application: Application) : AndroidViewModel(application) {

    val currencyAdapter = ArrayAdapter(getApplication(),
        android.R.layout.simple_list_item_1,
        getApplication<Application>().resources.getStringArray(R.array.array_currency_codes))

    var fromCurrency = MutableStateFlow("")
    var toCurrency = MutableStateFlow("")
    var value = MutableStateFlow("")
    var result = MutableStateFlow("Result will be displayed here")
    var pBarVisibility = MutableStateFlow(false)

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()
    val errorMsg = "This field is required"

    private val exchangeRateRepository = ExchangeRateRepository()

    fun validate()
    {
        pBarVisibility.value = true
        if(fromCurrency.value == "" || fromCurrency.value.isEmpty())
        {
            _stateFlow.value = 1
            pBarVisibility.value = false
            return
        }
        if(toCurrency.value == "" || toCurrency.value.isEmpty())
        {
            _stateFlow.value = 2
            pBarVisibility.value = false
            return
        }
        if(value.value == "" || value.value.isEmpty())
        {
            _stateFlow.value = 3
            pBarVisibility.value = false
            return
        }
        requestData()
    }

    private fun requestData()
    {
        pBarVisibility.value = true
        viewModelScope.launch(Dispatchers.IO) {
            exchangeRateRepository.fetchData(fromCurrency.value, toCurrency.value, value.value)
        }
        viewModelScope.launch {
            exchangeRateRepository.responseData.collectLatest {
                if(it != "")
                {
                    pBarVisibility.value = false
                    toastShort(getApplication(), it)
                    result.value = "${value.value} ${fromCurrency.value} = $it ${toCurrency.value}"
                }
            }
        }
    }

    fun swap()
    {
        setUpLogger()
        val tmp = toCurrency.value
        toCurrency.value = fromCurrency.value
        fromCurrency.value = tmp
    }

}