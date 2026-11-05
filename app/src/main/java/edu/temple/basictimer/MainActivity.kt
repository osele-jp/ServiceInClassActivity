package edu.temple.basictimer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import java.util.Timer

class MainActivity : AppCompatActivity() {


    var timerBinder: TimerService.TimerBinder? = null

    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                timerBinder = service as TimerService.TimerBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerBinder = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)

        lifecycleScope.launch {
           repeatOnLifecycle(Lifecycle.State.RESUMED){
               TimerInfo.currentTime.collect { textView.text = it.toString() }
           }
        }

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )


        findViewById<Button>(R.id.startButton).setOnClickListener {
            timerBinder?.run{
                if(!isRunning){
                    timerBinder?.start(100)
                    (it as Button).text = "PAUSE"
                }else if (isRunning){
                    timerBinder?.pause()
                    (it as Button).text = "RESUME"
                }
            }



        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerBinder?.stop()
        }
    }
}