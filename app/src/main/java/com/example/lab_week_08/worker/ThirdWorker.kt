package com.example.lab_week_08.worker

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class ThirdWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        Thread.sleep(2500)

        val id = inputData.getString(INPUT_DATA_ID)
        val output = Data.Builder()
            .putString(OUTPUT_DATA_ID, id)
            .build()

        return Result.success(output)
    }

    companion object {
        const val INPUT_DATA_ID = "INPUT3"
        const val OUTPUT_DATA_ID = "OUTPUT3"
    }
}
