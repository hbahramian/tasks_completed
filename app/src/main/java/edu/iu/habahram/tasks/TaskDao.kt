package edu.iu.habahram.tasks

import androidx.lifecycle.LiveData

interface TaskDao {
    val tasks : LiveData<List<Task>>
    suspend fun insert(task: Task)
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    fun get(key: Long): LiveData<Task>
    fun getAll(): LiveData<MutableList<Task>>
}