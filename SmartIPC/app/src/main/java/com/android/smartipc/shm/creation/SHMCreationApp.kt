package com.android.smartipc.shm.creation

import android.app.Application
import android.content.Context
import android.util.Log
import com.android.smartipc.rtrepo.RealTimeRepository
import com.android.smartipc.rtrepo.SharedMemory
import com.android.smartipc.rtrepo.impl.TrainingRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.ArrayList

class SHMCreationApp : Application() {

    private val TAG = "PatternsApplication"

    companion object
    {
        var context: Context? = null
        var regions: ArrayList<SharedMemory> = ArrayList<SharedMemory>()
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        RealTimeRepository.init(
            context,
            object : RealTimeRepository.Callback {
                override fun onBound(success: Boolean) {
                    Log.e(TAG,"RealTimeRepository onBound $success"
                    )
                    if (success) {
                        try {
                            var sh: SharedMemory = RealTimeRepository.createInstance(
                                "TrainingRepo2",
                                1024
                            )
                            regions.add(sh)

                            CoroutineScope(Dispatchers.IO).launch {
                                Log.e(TAG, "start coroutine")
                                var time = 0L
                                var calories = 0L
                                var distance = 0.0
                                var caloriesForHour = 0.0
                                while(true)
                                {
                                    delay(1000)
                                    val trainingRepo = TrainingRepo(sh)

                                    if(success)
                                    {
                                        time++
                                        calories++
                                        distance = distance + 0.1
                                        caloriesForHour =caloriesForHour + 0.1
                                        trainingRepo.set(TrainingRepo.TIME, time)
                                        trainingRepo.set(TrainingRepo.CALORIES, calories)
                                        trainingRepo.set(TrainingRepo.DISTANCE, distance)
                                        trainingRepo.set(TrainingRepo.CALORIES_HOUR, caloriesForHour)
                                    }
                                }
                            }


                        } catch (e: Exception) {
                            Log.e(TAG,"RealTimeRepository onBound exception"
                            )
                            e.printStackTrace()
                        }
                    }
                }
            })
    }

    override fun onTerminate() {
        super.onTerminate()
        for (sh in regions) sh.close()
        RealTimeRepository.tearDown(context)
    }
}