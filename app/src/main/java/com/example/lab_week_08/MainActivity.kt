package com.example.lab_week_08

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.lab_week_08.worker.FirstWorker
import com.example.lab_week_08.worker.SecondWorker

class MainActivity : AppCompatActivity() {

    private val workManager by lazy { WorkManager.getInstance(this) }
    private var serviceStarted = false   // Prevent double start

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        startWorkProcess()
    }

    private fun startWorkProcess() {
        val id = "001"

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val first = OneTimeWorkRequestBuilder<FirstWorker>()
            .setConstraints(constraints)
            .addTag("FirstWorker")
            .setInputData(workDataOf(FirstWorker.INPUT_DATA_ID to id))
            .build()

        val second = OneTimeWorkRequestBuilder<SecondWorker>()
            .setConstraints(constraints)
            .addTag("SecondWorker")
            .setInputData(workDataOf(SecondWorker.INPUT_DATA_ID to id))
            .build()

        workManager.beginUniqueWork("mywork", ExistingWorkPolicy.REPLACE, first)
            .then(second)
            .enqueue()

        workManager.getWorkInfosForUniqueWorkLiveData("mywork").observe(this) { list ->

            val firstWorker = list.find { it.tags.contains("FirstWorker") }
            if (firstWorker?.state?.isFinished == true) {
                Toast.makeText(this, "First process is done", Toast.LENGTH_SHORT).show()
            }

            val secondWorker = list.find { it.tags.contains("SecondWorker") }
            if (secondWorker?.state?.isFinished == true && !serviceStarted) {
                Toast.makeText(this, "Second process is done", Toast.LENGTH_SHORT).show()

                serviceStarted = true

                val intent = Intent(this, CountdownForegroundService::class.java)
                intent.putExtra("channel_id", id)
                startForegroundService(intent)
            }
        }
    }
}
