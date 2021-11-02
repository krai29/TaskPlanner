package com.example.taskplanner.ui.deleteallcompletedtask

import androidx.lifecycle.ViewModel
import com.example.taskplanner.data.TaskDao
import com.example.taskplanner.di.AppModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompleteTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    @AppModule.ApplicationScope private val applicationScope : CoroutineScope
) : ViewModel(){

    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }

}