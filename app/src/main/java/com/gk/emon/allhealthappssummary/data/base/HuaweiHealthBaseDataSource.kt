package com.gk.emon.allhealthappssummary.data.base

import com.huawei.hms.hihealth.data.ActivityRecord
import kotlinx.coroutines.flow.Flow
import java.util.*

interface HuaweiHealthBaseDataSource {
    fun getActivity(startTime:Calendar, endTime:Calendar):Flow<Result<List<ActivityRecord>>>
}