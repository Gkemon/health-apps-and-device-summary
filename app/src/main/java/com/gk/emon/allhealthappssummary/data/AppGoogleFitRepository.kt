package com.gk.emon.allhealthappssummary.data

import com.gk.emon.allhealthappssummary.data.base.GoogleFitBaseDataSource
import com.gk.emon.allhealthappssummary.data.base.GoogleFitBaseRepository
import com.google.android.gms.fitness.result.DataReadResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*
import javax.inject.Inject

class AppGoogleFitRepository @Inject
constructor(
    private val googleFitRemoteDataSource: GoogleFitBaseDataSource<DataReadResponse>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    GoogleFitBaseRepository<DataReadResponse> {
    override fun getHistoryResponseFlow(
        startTime: Calendar,
        endTime: Calendar
    ) = googleFitRemoteDataSource.getHistoryResponseFlow(startTime, endTime)


    override suspend fun getHistoryResponse(
        startTime: Calendar,
        endTime: Calendar
    ): Result<DataReadResponse> {
        return googleFitRemoteDataSource.getHistoryResponse(startTime, endTime)
    }
}