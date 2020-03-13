package com.motorola.coroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            lifecycleScope.launch {
                Log.d("tag", "1 Thread: " + Thread.currentThread().name)
                updateToolbarTextCoroutine()
                Log.d("tag", "2 Thread: " + Thread.currentThread().name)
            }
        }
    }

    /**
     * Tasks API
     */
    private fun getToolbarTextUsingTask(): Task<String> {
        val taskCompletionSource = TaskCompletionSource<String>()
        taskCompletionSource.setResult("done")
        return taskCompletionSource.task
    }

    private fun updateToolbarTextUsingTask() {
        getToolbarTextUsingTask().addOnSuccessListener {
            toolbar.title = it
        }
    }

    /**
     * Tasks API -> Coroutines API
     */
    private suspend fun updateToolbarTextCoroutine() = withContext(Dispatchers.Main) {
        Log.d("tag", "updateToolbarTextCoroutine Thread: " + Thread.currentThread().name)
        delay(10000L)
        toolbar.title = getToolbarTextUsingTask().await()
    }

    private suspend inline fun <T> Task<out T>.await(): T { //= withContext(Dispatchers.Default) {
        return suspendCoroutine { continuation ->
            this /* task */.addOnSuccessListener { continuation.resume(it) }
            addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

}