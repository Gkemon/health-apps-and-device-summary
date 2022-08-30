package com.gk.emon.allhealthappssummary.domain

import com.gk.emon.allhealthappssummary.data.base.GoogleFitBaseRepository
import com.gk.emon.allhealthappssummary.di.IoDispatcher
import com.gk.emon.allhealthappssummary.domain.baseUseCase.FlowUseCase
import com.gk.emon.allhealthappssummary.utils.DateUtils
import com.google.android.gms.fitness.result.DataReadResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class GetGoogleFitDataLastYearUseCase @Inject constructor(
    private val googleFitBaseRepository: GoogleFitBaseRepository<DataReadResponse>,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Void?, DataReadResponse>(dispatcher) {
    public override fun execute(parameters: Void?): Flow<Result<DataReadResponse>> {
        return googleFitBaseRepository.getHistoryResponseFlow(
            Calendar.getInstance(),
            DateUtils.getCalenderFromTodayToOneYearBack())
    }

}