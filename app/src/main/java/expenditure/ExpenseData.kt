package expenditure

data class ExpenseData(
    val id: String,
    val date: String,
    val modeOfExpense: String,
    val payee: String,
    val purpose: String,
    val amount: Double
)
