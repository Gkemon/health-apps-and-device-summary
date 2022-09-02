package com.gk.emon.allhealthappssummary.domain

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import javax.inject.Inject

class CheckIsGoogleSignInUseCase @Inject constructor(
    private val googleSignInAccount: GoogleSignInAccount,
    private val fitnessOptions: FitnessOptions
) {
    fun isGoogleFitConnected() =
        GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)
}