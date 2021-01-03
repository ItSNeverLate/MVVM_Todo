package mp.parsa.mvvmtododb.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
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
import mp.parsa.mvvmtododb.utils.setOnQueryTextChanged

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnClickListener {

    private val viewMode: TasksViewModel by viewModels()

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
                    viewMode.onTaskSwiped(task)
                }
            }).attachToRecyclerView(recyclerView)
        }

        viewMode.tasks.observe(viewLifecycleOwner, Observer {
            tasksAdapter.submitList(it)
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewMode.tasksEvent.collect { event ->
                when (event) {
                    is TasksViewModel.TasksEvent.ShowUndoTaskDeleted -> {
                        Snackbar.make(requireView(), "Task deleted!", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                viewMode.onDeleteUndoClick(event.task)
                            }
                            .show()
                    }
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search_query)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextChanged {
            viewMode.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewMode.preferencesFlow.first().hideCompletedTasks // first: because we need its value once
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_order_by_name -> {
                viewMode.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_order_by_created_date -> {
                viewMode.onSortOrderSelected(SortOrder.BY_CREATED_DATE)
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewMode.onHideCompletedTasksClicked(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onItemClick(task: Task) {
        viewMode.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewMode.onTaskCheckBoxChange(task, isChecked)
    }
}