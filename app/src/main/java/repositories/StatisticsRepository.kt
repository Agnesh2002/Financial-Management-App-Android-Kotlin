package repositories

import com.orhanobut.logger.Logger
import kotlinx.coroutines.tasks.await
import utils.Common
import utils.StatisticsData

class StatisticsRepository(authEmail: String, var obj: StatisticsData) {

    private val docRefStatistics = Common.collRef.document(authEmail).collection("FINANCE").document("STATISTICS")
    private val collRefMonthlyStatistics = Common.collRef.document(authEmail).collection("FINANCE").document("STATISTICS").collection("MONTHLY")

    suspend fun getWholeExpenditure()
    {
        obj = StatisticsData(0,0.0,0,0.0)
        val expenditureCount = docRefStatistics.get().await().getLong("expenditure_count")!!
        val expenditureAmountValue = docRefStatistics.get().await().getLong("total_expenditure_amount")!!
        obj.expenditureCount = expenditureCount
        obj.expenditureAmountValue = expenditureAmountValue.toDouble()
    }

    suspend fun getWholeIncome()
    {
        var incomeCount = docRefStatistics.get().await().getLong("income_count")!!
        val incomeAmountValue = docRefStatistics.get().await().getLong("total_income_amount")!!
        obj.incomeCount = incomeCount
        obj.incomeAmountValue = incomeAmountValue.toDouble()
    }

    suspend fun getMonthlyExpenditure(monthAndYear: String)
    {
        obj = StatisticsData(0,0.0,0,0.0)
        val monthlyExpenditureCount = collRefMonthlyStatistics.document(monthAndYear).get().await().getLong("expenditure_count")!!
        val monthlyExpenditureAmountValue = collRefMonthlyStatistics.document(monthAndYear).get().await().getLong("total_expenditure_amount")!!
        obj.expenditureCount = monthlyExpenditureCount
        obj.expenditureAmountValue = monthlyExpenditureAmountValue.toDouble()
    }

    suspend fun getMonthlyIncome(monthAndYear: String)
    {
        var monthlyIncomeCount = collRefMonthlyStatistics.document(monthAndYear).get().await().getLong("income_count")!!
        val monthlyIncomeAmountValue = collRefMonthlyStatistics.document(monthAndYear).get().await().getLong("total_income_amount")!!
        obj.incomeCount = monthlyIncomeCount
        obj.incomeAmountValue = monthlyIncomeAmountValue.toDouble()
    }

}