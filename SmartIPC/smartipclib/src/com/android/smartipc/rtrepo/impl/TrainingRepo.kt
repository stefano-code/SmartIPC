package com.android.smartipc.rtrepo.impl

import android.content.Context
import com.android.smartipc.rtrepo.RealTimeField
import com.android.smartipc.rtrepo.RealTimeRepository
import com.android.smartipc.rtrepo.RealTimeRepositoryProxy
import com.android.smartipc.rtrepo.SharedMemory

//class TrainingRepo(ctx: Context?)
//{}

//class TrainingRepo(ctx: Context?, callback: RealTimeRepository.Callback?) : RealTimeRepositoryProxy(
//    ctx, "TrainingRepo2",
//    callback
//)

    class TrainingRepo : RealTimeRepositoryProxy
    {
        companion object
        {
            val TIME = RealTimeField( 0, RealTimeField.Type_Long )
            val DISTANCE = RealTimeField( 8, RealTimeField.Type_Double )
            val CALORIES = RealTimeField( 16, RealTimeField.Type_Long )
            val CALORIES_HOUR = RealTimeField( 24, RealTimeField.Type_Double )
        }

        constructor(ctx: Context?, callback: RealTimeRepository.Callback?) :
                                            super(ctx, "TrainingRepo2", callback)

        constructor(sharedMemory: SharedMemory) : super(sharedMemory)
    }