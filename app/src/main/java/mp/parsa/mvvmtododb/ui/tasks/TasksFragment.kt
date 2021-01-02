package mp.parsa.mvvmtododb.ui.tasks

import android.icu.lang.UCharacter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import mp.parsa.mvvmtododb.R
import mp.parsa.mvvmtododb.databinding.FragmentTasksBinding

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewMode: TasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val tasksAdapter = TasksAdapter()

        binding.apply {
            recyclerView.apply {
                adapter = tasksAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewMode.tasks.observe(viewLifecycleOwner, Observer {
            tasksAdapter.submitList(it)
        })
    }
}