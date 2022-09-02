package com.gk.emon.allhealthappssummary.data

import com.gk.emon.allhealthappssummary.data.base.HuaweiHealthBaseRepository
import com.huawei.hms.hihealth.data.ActivityRecord
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class AppHuaweiRepository @Inject constructor(
    private val huaweiHealthBaseRepository:
    HuaweiHealthBaseRepository
) :
    HuaweiHealthBaseRepository {
    override fun getActivity(
        startTime: Calendar,
        endTime: Calendar
    ): Flow<Result<List<ActivityRecord>>> {
        return huaweiHealthBaseRepository.getActivity(startTime, endTime)
    }
}