package com.example.taskplanner.ui.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskplanner.data.Task
import com.example.taskplanner.data.TaskDao
import com.example.taskplanner.ui.ADD_TASK_RESULT_OK
import com.example.taskplanner.ui.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val task = savedStateHandle.get<Task>("task")

    var taskName = savedStateHandle.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            savedStateHandle.set("taskName", value)
        }

    var taskImportance =
        savedStateHandle.get<Boolean>("taskImportance") ?: task?.importance ?: false
        set(value) {
            field = value
            savedStateHandle.set("taskImportance", value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            // show invalid input message
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, importance = taskImportance)
            updatedTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, importance = taskImportance)
            createTask(newTask)
        }
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch{
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    private fun createTask(newTask: Task) = viewModelScope.launch {
        taskDao.insert(newTask)
        // navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updatedTask(updatedTask: Task) = viewModelScope.launch {
        taskDao.update(updatedTask)
        // navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    sealed class AddEditTaskEvent {

        data class ShowInvalidInputMessage(val message: String) : AddEditTaskEvent()

        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }


}