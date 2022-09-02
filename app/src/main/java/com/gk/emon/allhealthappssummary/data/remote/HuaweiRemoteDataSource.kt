package com.gk.emon.allhealthappssummary.data.remote

import com.gk.emon.allhealthappssummary.data.base.HuaweiHealthBaseDataSource
import com.gk.emon.allhealthappssummary.utils.tryOffer
import com.huawei.hmf.tasks.Task
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.data.ActivityRecord
import com.huawei.hms.hihealth.options.ActivityRecordReadOptions
import com.huawei.hms.hihealth.result.ActivityRecordReply
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class HuaweiRemoteDataSource @Inject constructor(
    var activityRecordsController: ActivityRecordsController
) :
    HuaweiHealthBaseDataSource {
    @Override
    override fun getActivity(
        startTime: Calendar,
        endTime: Calendar
    ): Flow<Result<List<ActivityRecord>>> {
        return (callbackFlow<Result<List<ActivityRecord>>> {
            val readOption: ActivityRecordReadOptions = ActivityRecordReadOptions.Builder()
                .setTimeInterval(
                    startTime.timeInMillis,
                    endTime.timeInMillis,
                    TimeUnit.MILLISECONDS
                )
                .readActivityRecordsFromAllApps()
                .read(com.huawei.hms.hihealth.data.DataType.DT_INSTANTANEOUS_STEPS_RATE)
                .build()

            val getTask: Task<ActivityRecordReply> =
                activityRecordsController.getActivityRecord(readOption)
            getTask.addOnSuccessListener {
                tryOffer(Result.success(it.activityRecords))
            }.addOnFailureListener {
                tryOffer(Result.failure(it))
            }
            awaitClose {}
        }.catch {
            Result.failure<Throwable>(it)
        }.flowOn(Dispatchers.IO))
    }
}
