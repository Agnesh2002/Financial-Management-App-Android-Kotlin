package repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import utils.Common.setUpLogger
import utils.retrofit.RetrofitInterface
import java.lang.Exception

class ExchangeRateRepository {

    private val _responseData = MutableStateFlow("")
    val responseData = _responseData.asStateFlow()

    suspend fun fetchData(from: String, to: String, amount: String) {

        setUpLogger()
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitInterface::class.java)

        val retrofitRequestForConversion = retrofitBuilder.sendConversionRequest(to, from, amount)
        try {
            val response = retrofitRequestForConversion.awaitResponse()
            if (response.isSuccessful) {
                val responseBody = response.body()
                val roundedResult = String.format("%.2f",responseBody!!.result)
                _responseData.value = roundedResult
            }
        }
        catch (e: Exception)
        {
            _responseData.value = "$e.message.toString()\n Please try again"
        }

    }

    companion object{
        const val BASE_URL = "https://api.apilayer.com/exchangerates_data/"
    }
}
