package edu.iu.habahram.tasks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class TasksViewModel : ViewModel() {
    val taskId : Long = -1L
    var task = MutableLiveData<Task>()
    private val _tasks : MutableLiveData<MutableList<Task>> = MutableLiveData()
    val  tasks : LiveData<List<Task>>
        get() = _tasks as LiveData<List<Task>>
    private val _navigateToTask = MutableLiveData<Long?>()
    val navigateToTask: LiveData<Long?>
        get() = _navigateToTask

    private val _navigateToList = MutableLiveData<Boolean>(false)
    val navigateToList: LiveData<Boolean>
        get() = _navigateToList

    private lateinit var tasksCollection: DatabaseReference


    init {
        if(taskId == -1L) {
            task.value = Task()
        }
        val database = Firebase.database
        _tasks.value = mutableListOf<Task>()
        tasksCollection = database.getReference("tasks")
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TaskDaoFirebase.TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new task has been added, add it to the displayed list
                val task = dataSnapshot.getValue<Task>()
                _tasks.value!!.add(task!!)
                // ...
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TaskDaoFirebase.TAG, "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val newTask = dataSnapshot.getValue<Task>()
                val taskKey = dataSnapshot.key

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TaskDaoFirebase.TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A task has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val taskKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TaskDaoFirebase.TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedTask = dataSnapshot.getValue<Task>()
                val taskKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TaskDaoFirebase.TAG, "postComments:onCancelled", databaseError.toException())

            }
        }
//        tasksCollection.addChildEventListener(childEventListener)

        tasksCollection.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var tasksList : ArrayList<Task> = ArrayList()
                for (taskSnapshot in dataSnapshot.children) {
                    // TODO: handle the post
                    var task = taskSnapshot.getValue<Task>()
                    tasksList.add(task!!)
                }
                _tasks.value = tasksList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TaskDaoFirebase.TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
    }

    fun getAll() : LiveData<List<Task>> {
        return tasks
    }

    fun updateTask() {

    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val task = Task()
            task.taskId = taskId
//            dao.delete(task)
        }
    }
    fun onTaskClicked(taskId: Long) {
        _navigateToTask.value = taskId
    }
    fun onTaskNavigated() {
        _navigateToTask.value = null
    }

    fun onNavigatedToList() {
        _navigateToList.value = false
    }
}