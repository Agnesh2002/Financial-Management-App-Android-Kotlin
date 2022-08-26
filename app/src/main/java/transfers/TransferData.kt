package transfers

import java.util.*

data class TransferData(
    val id: String,
    val date: Date,
    val amount: Double,
    val monthAndYear: Date
)