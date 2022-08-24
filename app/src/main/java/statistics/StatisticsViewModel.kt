package statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.Common.auth
import utils.Common.toastShort
import repositories.StatisticsRepository
import utils.StatisticsData

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val statisticsRepository = StatisticsRepository(auth.currentUser?.email!!, StatisticsData(0,0.0,0,0.0))
    var dateText = MutableStateFlow("Select month and year")

    private val _data = MutableStateFlow(StatisticsData(0,0.0,0,0.0))
    val data = _data.asStateFlow()

    suspend fun getWholeStatistics()
    {
        val job1 = viewModelScope.launch(Dispatchers.IO) {
            statisticsRepository.getWholeExpenditure()
        }
        val job2 = viewModelScope.launch(Dispatchers.IO) {
            statisticsRepository.getWholeIncome()
        }
        job1.join()
        job2.join()
        _data.value = statisticsRepository.obj
    }

    suspend fun getMonthlyStatistics(monthAndYear: String)
    {
        val job1 = viewModelScope.launch(Dispatchers.IO) {
            statisticsRepository.getMonthlyExpenditure(monthAndYear)
        }
        val job2 = viewModelScope.launch(Dispatchers.IO) {
            statisticsRepository.getMonthlyIncome(monthAndYear)
        }
        job1.join()
        job2.join()
        _data.value = statisticsRepository.obj
    }

}