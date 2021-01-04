package mp.parsa.mvvmtododb.ui.deletecompletedtasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mp.parsa.mvvmtododb.data.db.dao.TaskDao
import mp.parsa.mvvmtododb.di.annotation.AppScope

class DeleteCompletedTasksViewModel
@ViewModelInject
constructor(
    val taskDao: TaskDao,
    @AppScope private val appScope: CoroutineScope
) : ViewModel() {

    fun onDeleteCompletedTaskConfirm() = appScope.launch {
        taskDao.deleteCompletedTasks()
    }
}