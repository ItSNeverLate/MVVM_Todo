package mp.parsa.mvvmtododb.ui.tasks

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import mp.parsa.mvvmtododb.R

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewMode: TasksViewModel by viewModels()

}