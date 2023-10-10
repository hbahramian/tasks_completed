package edu.iu.habahram.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TasksViewModel(val dao: TaskDao) : ViewModel() {
    var newTaskName = ""
    val tasks = dao.getAll()
    private val _navigateToTask = MutableLiveData<Long?>()
    val navigateToTask: LiveData<Long?>
        get() = _navigateToTask
    fun addTask() {
        viewModelScope.launch {
            val task = Task()
            task.taskName = newTaskName
            dao.insert(task)
        }
    }
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val task = Task()
            task.taskId = taskId
            dao.delete(task)
        }
    }
    fun onTaskClicked(taskId: Long) {
        _navigateToTask.value = taskId
    }
    fun onTaskNavigated() {
        _navigateToTask.value = null
    }
}