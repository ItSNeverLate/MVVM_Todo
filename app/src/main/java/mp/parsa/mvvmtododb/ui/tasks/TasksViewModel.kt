package mp.parsa.mvvmtododb.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mp.parsa.mvvmtododb.data.db.dao.SortOrder
import mp.parsa.mvvmtododb.data.db.dao.TaskDao
import mp.parsa.mvvmtododb.data.db.entity.Task
import mp.parsa.mvvmtododb.data.preferences.PreferencesManager
import mp.parsa.mvvmtododb.ui.ADD_TASK_RESULT_OK
import mp.parsa.mvvmtododb.ui.EDIT_TASK_RESULT_OK

class TasksViewModel
@ViewModelInject
constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksFlow = combine(
        searchQuery.asFlow(),
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

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    fun onSortOrderSelect(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.setSortOrder(sortOrder)
    }

    fun onHideCompletedTasksClick(isChecked: Boolean) = viewModelScope.launch {
        preferencesManager.setHideCompletedTasks(isChecked)
    }

    fun onTaskCheckBoxChange(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSelect(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditTask(task))
    }

    fun onTaskSwipe(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoTaskDeleted(task))
    }

    fun onDeleteUndoClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTask)
    }

    fun onAddEditTaskResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(msg: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(msg))
    }

    sealed class TasksEvent {
        data class ShowUndoTaskDeleted(val task: Task) : TasksEvent()
        object NavigateToAddTask : TasksEvent()
        data class NavigateToEditTask(val task: Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TasksEvent()
    }
}

