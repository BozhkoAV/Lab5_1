package com.example.bozhko_lab5_1

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class MainActivity2 : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var textSecondsElapsed: TextView
    private val executorService by lazy {(application as ExecutorServiceApplication).executor}
    private lateinit var timeExecutor: Future<*>
    private var currentTime: Long = 0
    private var startTime: Long = 0

    private fun getThread() = Thread {
        Log.i("ExecutorService", "${Thread.currentThread()}: Started")
        try {
            while (!Thread.currentThread().isInterrupted) {
                currentTime = System.currentTimeMillis()
                if (currentTime - startTime >= 1000) {
                    textSecondsElapsed.post {
                        textSecondsElapsed.setText(getString(R.string.seconds, secondsElapsed++))
                        Log.i("ExecutorService", "Seconds elapsed = ${secondsElapsed}")
                    }
                    startTime = currentTime
                }
                Thread.sleep(0)
            }
        } catch (e: InterruptedException) {
            Log.i("ExecutorService", "${Thread.currentThread()}: Stopped")
            return@Thread
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.continue_watch)
        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
    }

    override fun onStart() {
        super.onStart()
        timeExecutor = executorService.submit(getThread())
        startTime = System.currentTimeMillis()
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        secondsElapsed = sharedPref.getInt(SECONDS_ELAPSED, secondsElapsed)
    }

    override fun onStop() {
        super.onStop()
        timeExecutor.cancel(true)
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putInt(SECONDS_ELAPSED, secondsElapsed)
            apply()
        }
    }

    companion object {
        val SECONDS_ELAPSED = "secondsElapsed"
    }
}

class ExecutorServiceApplication: Application() {
    var executor: ExecutorService = Executors.newSingleThreadExecutor()
}