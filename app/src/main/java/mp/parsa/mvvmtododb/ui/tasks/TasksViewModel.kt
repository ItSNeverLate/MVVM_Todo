package mp.parsa.mvvmtododb.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import mp.parsa.mvvmtododb.data.db.dao.SortOrder
import mp.parsa.mvvmtododb.data.db.dao.TaskDao

class TasksViewModel
@ViewModelInject
constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.BY_NAME)
    val hideCompletedTasks = MutableStateFlow(false)

    private val tasksFlow = combine(
        searchQuery,
        sortOrder,
        hideCompletedTasks
    ) { searchQuery, sortOrder, hideCompletedTasks ->
        Triple(searchQuery, sortOrder, hideCompletedTasks) // To access all of flows separately
    }.flatMapLatest { (searchQuery, sortOrder, hideCompletedTasks) ->
        taskDao.getAll(searchQuery, sortOrder, hideCompletedTasks)
    }

    // We can Observe Flow in Fragment, but LiveData is view (Fragment) life cycle aware
    // We use Flow because it is more flexible to change thread and transformation
    val tasks = tasksFlow.asLiveData()
}

