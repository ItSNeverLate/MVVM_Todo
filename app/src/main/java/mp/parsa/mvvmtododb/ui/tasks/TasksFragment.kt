package mp.parsa.mvvmtododb.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mp.parsa.mvvmtododb.R
import mp.parsa.mvvmtododb.data.db.dao.SortOrder
import mp.parsa.mvvmtododb.data.db.entity.Task
import mp.parsa.mvvmtododb.databinding.FragmentTasksBinding
import mp.parsa.mvvmtododb.util.exhaustive
import mp.parsa.mvvmtododb.util.setOnQueryTextChanged

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnClickListener {

    private val viewModel: TasksViewModel by viewModels()

    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val tasksAdapter = TasksAdapter(this)

        binding.apply {
            recyclerView.apply {
                adapter = tasksAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = tasksAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwipe(task)
                }
            }).attachToRecyclerView(recyclerView)

            fabAddTask.setOnClickListener {
                viewModel.onAddTaskClick()
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            tasksAdapter.submitList(it)
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TasksViewModel.TasksEvent.ShowUndoTaskDeleted -> {
                        Snackbar.make(requireView(), "Task deleted!", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                viewModel.onDeleteUndoClick(event.task)
                            }
                            .show()
                    }
                    is TasksViewModel.TasksEvent.NavigateToAddTask -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                                null,
                                "Add Task"
                            )
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToEditTask -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                                event.task,
                                "Edit Task"
                            )
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    TasksViewModel.TasksEvent.NavigateToDeleteCompletedTasksScreen -> {
                        val action =
                            TasksFragmentDirections.actionGlobalDeletedCompletedTasksDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setFragmentResultListener("add_edit_task_request") { _, bundle ->
            val result = bundle.getInt("add_edit_task_result")
            viewModel.onAddEditTaskResult(result)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search_query)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.setOnQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewModel.preferencesFlow.first().hideCompletedTasks // first: because we need its value once
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_order_by_name -> {
                viewModel.onSortOrderSelect(SortOrder.BY_NAME)
                true
            }
            R.id.action_order_by_created_date -> {
                viewModel.onSortOrderSelect(SortOrder.BY_CREATED_DATE)
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedTasksClick(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {
                viewModel.onDeleteCompletedTasksClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelect(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckBoxChange(task, isChecked)
    }
}