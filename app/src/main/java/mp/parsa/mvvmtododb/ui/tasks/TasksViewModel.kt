package mp.parsa.mvvmtododb.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import mp.parsa.mvvmtododb.data.db.dao.TaskDao

class TasksViewModel
@ViewModelInject
constructor(
    private val taskDao: TaskDao
) : ViewModel(){

    // We can Observe Flow in Fragment, but LiveData is view (Fragment) life cycle aware
    // We use Flow because it is more flexible to change thread and transformation
    val tasks = taskDao.getAll().asLiveData()
}