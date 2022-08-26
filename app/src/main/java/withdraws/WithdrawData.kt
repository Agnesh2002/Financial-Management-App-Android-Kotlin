package withdraws

import java.util.*

data class WithdrawData(
    val id: String,
    val date: Date,
    val amount: Double,
    val monthAndYear: Date
)
