package com.gk.emon.allhealthappssummary.data.remote

import android.content.Context
import com.gk.emon.allhealthappssummary.data.GoogleFitBaseDataSource
import com.gk.emon.allhealthappssummary.utils.DateUtils
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class GoogleFitRemoteDataSource @Inject constructor(
    private val context: Context,
    private val googleSignInAccount: GoogleSignInAccount
) :
    GoogleFitBaseDataSource<DataReadRequest> {

    override suspend fun getHistoryResponse(
        startTime: Calendar,
        endTime: Calendar
    ): Result<DataReadRequest> {
        return suspendCoroutine {
            Fitness
                .getHistoryClient(context, googleSignInAccount)
                .readData(queryFitnessDataTodayToOneYearBack())
                .addOnSuccessListener {
                    it.toString()
                    Result.success(it)
                }.addOnFailureListener {
                    it.toString()
                    Result.failure<Exception>(it)
                }
        }
    }

    override suspend fun getHistoryResponseFlow(
        startTime: Calendar,
        endTime: Calendar
    ): Flow<Result<DataReadRequest>> {
        return suspendCoroutine {
            CoroutineScope(Dispatchers.IO).launch {
                val result = getHistoryResponse(startTime, endTime)
                val observableTasks = MutableStateFlow(result)
                observableTasks.map { tasks ->
                    if (tasks.isSuccess) {
                        Result.success(tasks)
                    } else {
                        tasks.exceptionOrNull()?.let { it -> Result.failure(it) }
                    }
                }
            }
        }
    }

    private fun queryFitnessDataTodayToOneYearBack(): DataReadRequest {
        return DataReadRequest.Builder()
            /*Steps related data*/
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
            .aggregate(DataType.TYPE_STEP_COUNT_CADENCE)
            .aggregate(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            /*Steps related data*/
            /*Heart related data*/
            .aggregate(DataType.AGGREGATE_HEART_POINTS)
            .aggregate(DataType.AGGREGATE_HEART_RATE_SUMMARY)
            .aggregate(DataType.TYPE_HEART_RATE_BPM)
            .aggregate(DataType.TYPE_HEART_POINTS)
            /*Heart related data*/
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(
                Calendar.getInstance().timeInMillis,
                DateUtils.getCalenderFromTodayToOneYearBack().timeInMillis,
                TimeUnit.MILLISECONDS
            )
            .build()
    }

}