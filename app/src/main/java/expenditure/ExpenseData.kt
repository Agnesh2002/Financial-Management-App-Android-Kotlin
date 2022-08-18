package expenditure
import java.util.*

data class ExpenseData(
    val id: String,
    val date: Date,
    val modeOfExpense: String,
    val payee: String,
    val purpose: String,
    val amount: Double,
    val monthAndYear: Date
)
