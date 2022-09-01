package com.gk.emon.allhealthappssummary.data.remote

import android.content.Context
import androidx.annotation.WorkerThread
import com.gk.emon.allhealthappssummary.data.base.GoogleFitBaseDataSource
import com.gk.emon.allhealthappssummary.utils.DateUtils
import com.gk.emon.allhealthappssummary.utils.tryOffer
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class GoogleFitRemoteDataSource @Inject constructor(
    var context: Context,
    var googleSignInAccount: GoogleSignInAccount
) :
    GoogleFitBaseDataSource<DataReadResponse> {


    override suspend fun getHistoryResponse(
        startTime: Calendar,
        endTime: Calendar
    ): Result<DataReadResponse> {
        return suspendCoroutine {
            context.let { it1 ->
                Fitness
                    .getHistoryClient(it1, googleSignInAccount)
                    .readData(queryFitnessDataTodayToOneYearBack())
                    .addOnSuccessListener {
                        Result.success(it)
                    }.addOnFailureListener {
                        Result.failure<Exception>(it)
                    }
            }
        }
    }

    @WorkerThread
    override fun getHistoryResponseFlow(
        startTime: Calendar,
        endTime: Calendar
    ): Flow<Result<DataReadResponse>> {
        return (callbackFlow<Result<DataReadResponse>> {
            Fitness
                .getHistoryClient(context, googleSignInAccount)
                .readData(queryFitnessDataTodayToOneYearBack())
                .addOnSuccessListener {
                    tryOffer(Result.success(it))
                }.addOnFailureListener{
                    tryOffer(Result.failure(it))
                }
            awaitClose {}
        }).catch {
            Result.failure<Throwable>(it)
        }.flowOn(Dispatchers.IO)

    }

    private fun queryFitnessDataTodayToOneYearBack(): DataReadRequest {
        return DataReadRequest.Builder()
            /*Steps related data*/
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            /*Steps related data*/
            /*Heart related data*/
            .aggregate(DataType.TYPE_HEART_RATE_BPM)
            .aggregate(DataType.TYPE_HEART_POINTS)
            /*Heart related data*/
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(
                DateUtils.getCalenderFromTodayToOneYearBack().timeInMillis,
                Calendar.getInstance().timeInMillis,
                TimeUnit.MILLISECONDS
            )
            .build()
    }
}
