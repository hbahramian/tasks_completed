package edu.iu.habahram.tasks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class TasksViewModel : ViewModel() {
    var TAG = "TasksViewModel"
    private var auth: FirebaseAuth

    var user: User = User()
    var verifyPassword = ""
    var taskId: String = ""
    var task = MutableLiveData<Task>()
    private val _tasks: MutableLiveData<MutableList<Task>> = MutableLiveData()
    val tasks: LiveData<List<Task>>
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

    private lateinit var tasksCollection: CollectionReference


    init {
        auth = Firebase.auth
        if (taskId.trim() == "") {
            task.value = Task()
        }
        _tasks.value = mutableListOf<Task>()
        initializeTheDatabaseReference()
    }

    fun initializeTheDatabaseReference() {

        val database = Firebase.firestore
        tasksCollection = database.collection("tasks")
        tasksCollection
            .addSnapshotListener { dataSnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                var tasksList: ArrayList<Task> = ArrayList()
                for (taskSnapshot in dataSnapshot!!) {
                    var task = taskSnapshot.toObject<Task>()
                    task?.taskId = taskSnapshot.id
                    tasksList.add(task!!)
                }
                _tasks.value = tasksList

            }

    }

    fun getAll(): LiveData<List<Task>> {
        return tasks
    }

    fun updateTask() {
        if (taskId.trim() == "") {
            task.value?.userId = auth.currentUser!!.uid
            tasksCollection.add(task.value!!)
                .addOnSuccessListener { documentReference ->
                    taskId = documentReference.id
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        } else {
            tasksCollection.document(taskId).set(task.value!!)
        }
        _navigateToList.value = true
    }

    fun deleteTask(taskId: String) {
//        tasksCollection.child(taskId).removeValue()
        tasksCollection.document(taskId)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "Task successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting task ${task.value?.taskName}", e) }
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
        if (user.email.isEmpty() || user.password.isEmpty()) {
            _errorHappened.value = "Email and password cannot be empty."
            return
        }
        auth.signInWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if (it.isSuccessful) {

                _navigateToList.value = true
            } else {
                _errorHappened.value = it.exception?.message
            }
        }
    }

    fun signUp() {
        if (user.email.isEmpty() || user.password.isEmpty()) {
            _errorHappened.value = "Email and password cannot be empty."
            return
        }
        if (user.password != verifyPassword) {
            _errorHappened.value = "Password and verify do not match."
            return
        }
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if (it.isSuccessful) {
                _navigateToSignIn.value = true
            } else {
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