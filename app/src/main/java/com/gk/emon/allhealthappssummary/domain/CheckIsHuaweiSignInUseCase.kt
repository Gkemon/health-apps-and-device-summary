package com.gk.emon.allhealthappssummary.domain

import com.gk.emon.allhealthappssummary.utils.tryOffer
import com.huawei.hms.hihealth.SettingController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CheckIsHuaweiSignInUseCase @Inject constructor(
    private val settingController: SettingController,
) {
    fun isHuaweiSignInConnected() =
        callbackFlow<Result<Boolean>> {
            settingController.checkHealthAppAuthorization()
                .addOnFailureListener {
                    tryOffer(Result.failure(it))
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        tryOffer(Result.success(true))
                    } else {
                        tryOffer(Result.failure(it.exception))
                    }
                }
            awaitClose {
            }
        }.catch {
            Result.failure<Throwable>(it)
        }.flowOn(Dispatchers.Main)

}