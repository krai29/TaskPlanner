package com.example.taskplanner.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskplanner.data.SortOrder
import com.example.taskplanner.data.Task
import com.example.taskplanner.data.TaskDao
import com.example.taskplanner.data.TaskPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val taskPreferencesManager: TaskPreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

//    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//    val hideCompleted = MutableStateFlow(false)
    val preferencesFlow = taskPreferencesManager.preferencesFlow

    //    private val tasksFlow = searchQuery.flatMapLatest {
//        taskDao.getTasks(it)
//    }
//Helps in combining all flows and lambda gets triggered even if one flow changes
    // and it will fetch latest value from other flows too
//    private val tasksFlow = combine(
//        searchQuery,
//        sortOrder,
//        hideCompleted
//    ) { query, sortOrder, hideCompleted ->
//        Triple(query, sortOrder, hideCompleted)
//    }.flatMapLatest {(query,sortOrder,hideCompleted) ->
//        taskDao.getTasks(query,sortOrder,hideCompleted)
//    }
    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    ){query,filterPreferences ->
        Pair(query,filterPreferences)
    }.flatMapLatest { (query,filterPreferences) ->
        taskDao.getTasks(query,filterPreferences.sortOrder,filterPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder)= viewModelScope.launch {
        taskPreferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted : Boolean) = viewModelScope.launch {
        taskPreferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) {

    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean)=
        viewModelScope.launch {
            taskDao.update(task.copy(completed = isChecked))
        }


}

