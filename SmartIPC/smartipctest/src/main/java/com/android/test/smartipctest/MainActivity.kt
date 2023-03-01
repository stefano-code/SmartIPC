package com.android.test.smartipctest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.smartipc.rtrepo.RealTimeRepository
import com.android.smartipc.rtrepo.impl.TrainingRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    lateinit var trainingRepository : com.android.smartipc.rtrepo.impl.TrainingRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bounded = false

        trainingRepository =
            com.android.smartipc.rtrepo.impl.TrainingRepo(this, object : RealTimeRepository.Callback {
                override fun onBound(success: Boolean) {
                    Log.e(TAG, "bounded " + success)
                    bounded = success
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.e(TAG, "start coroutine")
                        while (true) {
                            delay(1000)
                            if (bounded) {
                                Log.e(
                                    TAG,
                                    "time : ${trainingRepository.getLong(TrainingRepo.TIME)}  distance: ${
                                        trainingRepository.getDouble(TrainingRepo.DISTANCE)}" +
                                    " calories ${trainingRepository.getLong(TrainingRepo.CALORIES)}  calories/hour: ${
                                        trainingRepository.getDouble(TrainingRepo.CALORIES_HOUR) }"
                                )
                            }
                        }
                    }
                }
            })
    }
}
