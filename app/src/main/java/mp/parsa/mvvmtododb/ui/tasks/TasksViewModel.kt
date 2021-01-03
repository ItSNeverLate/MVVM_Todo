package mp.parsa.mvvmtododb.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import mp.parsa.mvvmtododb.data.db.dao.SortOrder
import mp.parsa.mvvmtododb.data.db.dao.TaskDao
import mp.parsa.mvvmtododb.data.db.entity.Task
import mp.parsa.mvvmtododb.data.preferences.PreferencesManager

class TasksViewModel
@ViewModelInject
constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    ) { searchQuery, filterPreferences ->
        Pair(searchQuery, filterPreferences) // To access all of flows separately
    }.flatMapLatest { (searchQuery, filterPreferences) ->
        taskDao.getAll(
            searchQuery,
            filterPreferences.sortOrder,
            filterPreferences.hideCompletedTasks
        )
    }

    // We can Observe Flow in Fragment, but LiveData is view (Fragment) life cycle aware
    // We use Flow because it is more flexible to change thread and transformation
    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.setSortOrder(sortOrder)
    }

    fun onHideCompletedTasksClicked(isChecked: Boolean) = viewModelScope.launch {
        preferencesManager.setHideCompletedTasks(isChecked)
    }

    fun onTaskCheckBoxChange(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSelected(task: Task) {
    }
}

