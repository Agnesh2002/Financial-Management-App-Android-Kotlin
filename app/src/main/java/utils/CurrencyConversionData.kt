package utils

import utils.retrofit.Info
import utils.retrofit.Query

data class CurrencyConversionData(
    val date: String,
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
)