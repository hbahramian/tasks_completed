package edu.iu.habahram.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EditTaskViewModel(taskId: Long, val dao: TaskDao) : ViewModel() {
    var task = MutableLiveData<Task>()
    val taskId : Long = taskId
    private val _navigateToList = MutableLiveData<Boolean>(false)
    val navigateToList: LiveData<Boolean>
        get() = _navigateToList



    init {
            dao.get(taskId).observeForever{ it ->
               if(it == null) {
                   task.value = Task()
               } else {
                   task.value = it
               }
            }

    }
    fun updateTask() {
        viewModelScope.launch {
            if(task.value?.taskId != 0L) {
                dao.update(task.value!!)
            } else {
                dao.insert(task.value!!)
            }
            _navigateToList.value = true
        }
    }
    fun deleteTask() {
        viewModelScope.launch {
            dao.delete(task.value!!)
            _navigateToList.value = true
        }
    }
    fun onNavigatedToList() {
        _navigateToList.value = false
    }

    override fun onCleared() {
        super.onCleared()
    }
}