package com.example.taskplanner.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskplanner.R
import com.example.taskplanner.data.SortOrder
import com.example.taskplanner.data.Task
import com.example.taskplanner.databinding.FragmentTasksBinding
import com.example.taskplanner.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.OnItemClickListener {

    private val viewModel : TasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TaskAdapter(this)

        binding.apply {
            rvTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner){
            taskAdapter.submitList(it)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks,menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            // update search query
            viewModel.searchQuery.value = it
        }

        // to read current hide completed state from the preferences flow
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_sort_by_name -> {
               // viewModel.sortOrder.value = SortOrder.BY_NAME
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date -> {
              //  viewModel.sortOrder.value = SortOrder.BY_DATE
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_delete_all_completed_tasks -> {
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
               // viewModel.hideCompleted.value = item.isChecked
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClicked(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task,isChecked)
    }
}