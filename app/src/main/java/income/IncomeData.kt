package income

import java.util.*

data class IncomeData(
    val id: String,
    val date: Date,
    val amount: Double,
    val source: String,
    val mode: String,
    val monthAndYear: Date,
)
