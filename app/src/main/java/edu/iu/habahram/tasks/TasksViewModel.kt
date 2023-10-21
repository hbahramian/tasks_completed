package edu.iu.habahram.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class TasksViewModel : ViewModel() {
    private var auth: FirebaseAuth

    var user: User = User()
    var verifyPassword = ""
    var taskId : String = ""
    var task = MutableLiveData<Task>()
    private val _tasks : MutableLiveData<MutableList<Task>> = MutableLiveData()
    val  tasks : LiveData<List<Task>>
        get() = _tasks as LiveData<List<Task>>
    private val _navigateToTask = MutableLiveData<String?>()
    val navigateToTask: LiveData<String?>
        get() = _navigateToTask

    private val _errorHappened = MutableLiveData<String?>()
    val errorHappened: LiveData<String?>
        get() = _errorHappened

    private val _navigateToList = MutableLiveData<Boolean>(false)
    val navigateToList: LiveData<Boolean>
        get() = _navigateToList

    private val _navigateToSignUp = MutableLiveData<Boolean>(false)
    val navigateToSignUp: LiveData<Boolean>
        get() = _navigateToSignUp

    private val _navigateToSignIn = MutableLiveData<Boolean>(false)
    val navigateToSignIn: LiveData<Boolean>
        get() = _navigateToSignIn

    private lateinit var tasksCollection: DatabaseReference


    init {
        auth = Firebase.auth
        if(taskId.trim() == "") {
            task.value = Task()
        }
        val database = Firebase.database
        _tasks.value = mutableListOf<Task>()
        tasksCollection = database.getReference("tasks")
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

                // A new task has been added, add it to the displayed list
                val task = dataSnapshot.getValue<Task>()
                _tasks.value!!.add(task!!)
                // ...
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val newTask = dataSnapshot.getValue<Task>()
                val taskKey = dataSnapshot.key

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                // A task has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val taskKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedTask = dataSnapshot.getValue<Task>()
                val taskKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
//        tasksCollection.addChildEventListener(childEventListener)

        tasksCollection.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var tasksList : ArrayList<Task> = ArrayList()
                for (taskSnapshot in dataSnapshot.children) {
                    // TODO: handle the post
                    var task = taskSnapshot.getValue<Task>()
                    task?.taskId = taskSnapshot.key!!
                    tasksList.add(task!!)
                }
                _tasks.value = tasksList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        })
    }

    fun getAll() : LiveData<List<Task>> {
        return tasks
    }

    fun updateTask() {
       if (taskId.trim() == "") {
           tasksCollection.push().setValue(task.value)
       } else {
           tasksCollection.child(taskId).setValue(task.value)
       }
        _navigateToList.value = true
    }

    fun deleteTask(taskId: String) {
      tasksCollection.child(taskId).removeValue()
    }
    fun onTaskClicked(selectedTask: Task) {
        _navigateToTask.value = selectedTask.taskId
        taskId = selectedTask.taskId
        task.value = selectedTask
    }

    fun onNewTaskClicked() {
        _navigateToTask.value = ""
        taskId = ""
        task.value = Task()
    }
    fun onTaskNavigated() {
        _navigateToTask.value = null
    }

    fun onNavigatedToList() {
        _navigateToList.value = false
    }

    fun navigateToSignUp() {
        _navigateToSignUp.value = true
    }

    fun onNavigatedToSignUp() {
        _navigateToSignUp.value = false
    }

    fun navigateToSignIn() {
        _navigateToSignIn.value = true
    }

    fun onNavigatedToSignIn() {
        _navigateToSignIn.value = false
    }

    fun signIn() {
        if(user.email.isEmpty() || user.password.isEmpty()) {
            _errorHappened.value = "Email and password cannot be empty."
            return
        }
        auth.signInWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if (it.isSuccessful) {
                _navigateToList.value = true
            }
            else {
                _errorHappened.value = it.exception?.message
            }
        }
    }

    fun signUp() {
        if(user.email.isEmpty() || user.password.isEmpty()) {
            _errorHappened.value = "Email and password cannot be empty."
            return
        }
        if(user.password != verifyPassword) {
            _errorHappened.value = "Password and verify do not match."
            return
        }
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if (it.isSuccessful) {
                _navigateToSignIn.value = true
            }
            else {
                _errorHappened.value = it.exception?.message
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _navigateToSignIn.value = true
    }



    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}