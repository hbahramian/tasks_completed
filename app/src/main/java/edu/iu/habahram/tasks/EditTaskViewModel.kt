package edu.iu.habahram.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EditTaskViewModel(taskId: Long, val dao: TaskDao) : ViewModel() {
    companion object {
        var TAG = "EditTaskViewModel"
    }
    var task = MutableLiveData<Task>()
    val taskId : Long = taskId
    private val _navigateToList = MutableLiveData<Boolean>(false)
    val navigateToList: LiveData<Boolean>
        get() = _navigateToList

    init {
        if(taskId == -1L) {
            task.value = Task()
        }
//            dao.get(taskId).observeForever{ it ->
//                Log.i(TAG, "taskId: $taskId")
//                Log.i(TAG, it.toString())
//               if(it == null) {
//                   task.value = Task()
//               } else {
//                   task.value = it
//               }
//            }

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
}