package edu.iu.habahram.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TasksViewModel(val dao: TaskDao) : ViewModel() {
    var newTaskName = ""
    private val tasks = dao.getAll()
    val tasksString = tasks.map {
            it -> formatTasks(it)
    }
    fun addTask() {
        viewModelScope.launch {
            val task = Task()
            task.taskName = newTaskName
            dao.insert(task)
        }
    }
    fun formatTasks(tasks: List<Task>): String {
        return tasks.fold("") {
                str, item -> str + '\n' + formatTask(item)
        }
    }
    fun formatTask(task: Task): String {
        var str = "ID: ${task.taskId}"
        str += '\n' + "Name: ${task.taskName}"
        str += '\n' + "Complete: ${task.taskDone}" + '\n'
        return str
    }
}