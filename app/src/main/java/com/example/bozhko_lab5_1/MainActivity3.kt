package com.example.bozhko_lab5_1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity3 : AppCompatActivity() {
    var secondsElapsed: Int = 0
    lateinit var textSecondsElapsed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.continue_watch)
        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)

        val job = lifecycleScope.launchWhenResumed {
            Log.i("Coroutine", "Started")
            try {
                while (isActive) {
                    delay(1000)
                    textSecondsElapsed.setText(getString(R.string.seconds, secondsElapsed++))
                    Log.i("Coroutine", "Seconds elapsed = ${secondsElapsed}")
                }
            } finally {
                Log.i("Coroutine", "while::finally")
            }
        }

        job.invokeOnCompletion {
            Log.i("Coroutine", "job::onCompletion")
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        secondsElapsed = sharedPref.getInt(SECONDS_ELAPSED, secondsElapsed)
    }

    override fun onStop() {
        super.onStop()
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