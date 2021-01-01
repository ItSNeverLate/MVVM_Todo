package mp.parsa.mvvmtododb.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import mp.parsa.mvvmtododb.data.db.dao.TaskDao

class TasksViewModel
@ViewModelInject
constructor(
    private val taskDao: TaskDao
) : ViewModel()