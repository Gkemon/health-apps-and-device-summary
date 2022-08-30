package com.gk.emon.allhealthappssummary.data

import kotlinx.coroutines.flow.Flow
import java.util.*

interface GoogleFitBaseRepository<T>{
   suspend fun getHistoryResponseFlow(startTime:Calendar,endTime:Calendar): Flow<Result<T>>
   suspend fun getHistoryResponse(startTime:Calendar,endTime:Calendar): Result<T>
}