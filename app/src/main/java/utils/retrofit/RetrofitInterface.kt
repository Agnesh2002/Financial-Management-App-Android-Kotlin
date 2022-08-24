package utils.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import utils.CurrencyConversionData

interface RetrofitInterface {

    @Headers("apikey: MdwF3JXG5hbH96gkKPjNm0H2QX8nsPIc")
    @GET(value = "convert")
    fun sendConversionRequest(@Query("to")to: String, @Query("from")from: String, @Query("amount")amount: String): Call<CurrencyConversionData>

}