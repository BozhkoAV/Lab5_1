package com.example.bozhko_lab5_1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var textSecondsElapsed: TextView
    private lateinit var backgroundThread: Thread
    private var currentTime: Long = 0
    private var startTime: Long = 0

    private fun getThread() = Thread {
        Log.i("Thread", "${Thread.currentThread()}: Started")
        try {
            while (!Thread.currentThread().isInterrupted) {
                currentTime = System.currentTimeMillis()
                if (currentTime - startTime >= 1000) {
                    textSecondsElapsed.post {
                        textSecondsElapsed.setText(getString(R.string.seconds, secondsElapsed++))
                        Log.i("Thread", "Seconds elapsed = ${secondsElapsed}")
                    }
                    startTime = currentTime
                }
                //Comment next line and current thread do not finish
                Thread.sleep(0)
                /*Thread.sleep(1000)
                textSecondsElapsed.post {
                    textSecondsElapsed.setText(getString(R.string.seconds, secondsElapsed++))
                    Log.i("Thread", "Seconds elapsed = ${secondsElapsed}")
                }*/
            }
        } catch (e: InterruptedException) {
            Log.i("Thread", "${Thread.currentThread()}: Stopped")
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
        backgroundThread = getThread()
        backgroundThread.start()
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        secondsElapsed = sharedPref.getInt(SECONDS_ELAPSED, secondsElapsed)
    }

    override fun onStop() {
        super.onStop()
        // Comment next line and we can see growing number of process in profiler
        backgroundThread.interrupt()
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