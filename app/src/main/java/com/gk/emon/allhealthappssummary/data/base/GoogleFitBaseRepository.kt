package com.gk.emon.allhealthappssummary.data.base

import kotlinx.coroutines.flow.Flow
import java.util.*

interface GoogleFitBaseRepository<T> {
    fun getHistoryResponseFlow(startTime: Calendar, endTime: Calendar): Flow<Result<T>>
    suspend fun getHistoryResponse(startTime: Calendar, endTime: Calendar): Result<T>
}