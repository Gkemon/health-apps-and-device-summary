package com.gk.emon.allhealthappssummary.domain

import com.gk.emon.allhealthappssummary.data.base.HuaweiHealthBaseRepository
import com.gk.emon.allhealthappssummary.di.IoDispatcher
import com.gk.emon.allhealthappssummary.domain.baseUseCase.FlowUseCase
import com.gk.emon.allhealthappssummary.utils.DateUtils
import com.huawei.hms.hihealth.data.ActivityRecord
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class GetHuaweiDataLastYearUseCase @Inject constructor(
    private val huaweiHealthBaseRepository: HuaweiHealthBaseRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Void?, List<ActivityRecord>>(dispatcher) {
    public override fun execute(parameters: Void?): Flow<Result<List<ActivityRecord>>> {
        return huaweiHealthBaseRepository.getActivity(
            Calendar.getInstance(),
            DateUtils.getCalenderFromTodayToOneYearBack()
        )
    }

}