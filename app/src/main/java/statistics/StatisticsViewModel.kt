package statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
        val job = viewModelScope.async {
            val task1 = async { statisticsRepository.getWholeExpenditure() }
            task1.join()
            val task2 = async { statisticsRepository.getWholeIncome() }
            task2.join()
            statisticsRepository.obj
        }
        _data.value = job.await()
    }

    suspend fun getMonthlyStatistics(monthAndYear: String)
    {
        val job = viewModelScope.async {
            val task1 = async { statisticsRepository.getMonthlyExpenditure(monthAndYear) }
            task1.join()
            val task2 = async { statisticsRepository.getMonthlyIncome(monthAndYear) }
            task2.join()
            statisticsRepository.obj
        }
        _data.value = job.await()

        viewModelScope.launch {
            statisticsRepository.stateFlowMsg.collectLatest {
                if(it!="")
                    toastShort(getApplication(), it)
            }
        }
    }

}