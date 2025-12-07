package com.example.lab_week_08

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.lab_week_08.worker.FirstWorker
import com.example.lab_week_08.worker.SecondWorker
import com.example.lab_week_08.worker.ThirdWorker

class MainActivity : AppCompatActivity() {

    private val workManager by lazy { WorkManager.getInstance(this) }
    private var firstServiceStarted = false
    private var secondServiceStarted = false

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

        val third = OneTimeWorkRequestBuilder<ThirdWorker>()
            .setConstraints(constraints)
            .addTag("ThirdWorker")
            .setInputData(workDataOf(ThirdWorker.INPUT_DATA_ID to id))
            .build()

        workManager.beginUniqueWork("mywork", ExistingWorkPolicy.REPLACE, first)
            .then(second)
            .then(third)
            .enqueue()

        workManager.getWorkInfosForUniqueWorkLiveData("mywork").observe(this) { list ->
            val firstWorkerDone = list.any { it.tags.contains("FirstWorker") && it.state.isFinished }
            val secondWorkerDone = list.any { it.tags.contains("SecondWorker") && it.state.isFinished }
            val thirdWorkerDone = list.any { it.tags.contains("ThirdWorker") && it.state.isFinished }

            if (firstWorkerDone) {
                Toast.makeText(this, "First process done", Toast.LENGTH_SHORT).show()
            }

            if (secondWorkerDone && !firstServiceStarted) {
                firstServiceStarted = true
                Toast.makeText(this, "Second process done", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, CountdownForegroundService::class.java)
                intent.putExtra("channel_id", id)
                startServiceCompat(intent)  // ⬅ FIX
            }

            if (thirdWorkerDone && !secondServiceStarted) {
                secondServiceStarted = true
                Toast.makeText(this, "Third process done", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, SecondNotificationService::class.java)
                intent.putExtra("channel_id", id)
                startServiceCompat(intent)  // ⬅ FIX
            }
        }
    }

    private fun startServiceCompat(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
