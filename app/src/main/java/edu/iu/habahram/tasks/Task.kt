package edu.iu.habahram.tasks

import com.google.firebase.database.Exclude

data class Task(
    @get:Exclude
    var taskId: String = "",
    var taskName: String = "",
    var taskDone: Boolean = false
)
