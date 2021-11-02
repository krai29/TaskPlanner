package com.example.taskplanner.ui.tasks

import androidx.lifecycle.*
import com.example.taskplanner.data.SortOrder
import com.example.taskplanner.data.Task
import com.example.taskplanner.data.TaskDao
import com.example.taskplanner.data.TaskPreferencesManager
import com.example.taskplanner.ui.ADD_TASK_RESULT_OK
import com.example.taskplanner.ui.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val taskPreferencesManager: TaskPreferencesManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = savedStateHandle.getLiveData("searchQuery","")

 //   val searchQuery = MutableStateFlow("")

//    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//    val hideCompleted = MutableStateFlow(false)
    val preferencesFlow = taskPreferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

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
        searchQuery.asFlow(),
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

    fun onTaskSelected(task: Task) = viewModelScope.launch{
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean)=
        viewModelScope.launch {
            taskDao.update(task.copy(completed = isChecked))
        }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))

    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when(result){
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(s: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(s))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
    }

    sealed class TasksEvent{
        data class ShowUndoDeleteTaskMessage(val task : Task):TasksEvent()

        data class NavigateToEditTaskScreen(val task : Task):TasksEvent()

        object NavigateToAddTaskScreen : TasksEvent()

        object NavigateToDeleteAllCompletedScreen : TasksEvent()

        data class ShowTaskSavedConfirmationMessage(val message : String):TasksEvent()
    }


}

