package edu.iu.habahram.tasks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class TaskDaoFirebase : TaskDao {

    companion object {
        val TAG = "TaskDaoFirebase"
    }

    val _tasks: MutableLiveData<MutableList<Task>> = MutableLiveData()
    override val tasks: LiveData<List<Task>>
        get() = _tasks as LiveData<List<Task>>

    private lateinit var tasksCollection: DatabaseReference

    init {
        val database = Firebase.database
        _tasks.value = mutableListOf<Task>()
        tasksCollection = database.getReference("tasks")
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new task has been added, add it to the displayed list
                val task = dataSnapshot.getValue<Task>()
                _tasks.value!!.add(task!!)
                // ...
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val newTask = dataSnapshot.getValue<Task>()
                val taskKey = dataSnapshot.key

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A task has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val taskKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedTask = dataSnapshot.getValue<Task>()
                val taskKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())

            }
        }
//        tasksCollection.addChildEventListener(childEventListener)
        // My top posts by number of stars
        tasksCollection.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (taskSnapshot in dataSnapshot.children) {
                    // TODO: handle the post
                    var task = taskSnapshot.getValue<Task>()
                    _tasks.value!!.add(task!!)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
    }


    override suspend fun insert(task: Task) {
        tasksCollection.push().setValue(task)
    }

    override suspend fun update(task: Task) {

    }

    override suspend fun delete(task: Task) {

    }

    override fun get(key: Long): LiveData<Task> {
        return MutableLiveData<Task>()
    }

    override fun getAll(): LiveData<MutableList<Task>> {
        return tasks as LiveData<MutableList<Task>>
    }

}