package com.gk.emon.allhealthappssummary.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gk.emon.allhealthappssummary.R
import com.gk.emon.allhealthappssummary.presentation.theme.AppThemeTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {

    AppThemeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(10.dp)
        ) {
            Text(
                text = "Please connect your app. For now only Google Fit and Huawei Health is available",
                fontSize = 20.sp
            )
            AllAppList(
                viewModel
            )
        }
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AllAppList(
    viewModel: HomeViewModel
) {
    Spacer(modifier = Modifier.fillMaxHeight(0.01f))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppListItem(
        name = "Google fit",
        icon = R.drawable.ic_google_fit,
        isConnected = uiState.isGoogleFitConnected,
        onAppClick = {
            handleGoogleFitClick(
                uiState.isGoogleFitConnected,
                viewModel
            )
        })
}

@Composable
fun AppListItem(name: String, icon: Int, onAppClick: () -> Unit, isConnected: Boolean = false) {
    Card(
        elevation = 10.dp,
        modifier = Modifier
            .shadow(
                10.dp,
                RectangleShape
            )
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                onAppClick.invoke()
            },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(icon),
                    contentDescription = name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .requiredSize(50.dp)
                        .padding(end = 10.dp)
                )
                Text(text = name, fontSize = 20.sp)
            }
            val result = when (isConnected) {
                true -> "Connected"
                false -> ""
            }
            Text(text = result, fontSize = 10.sp, color = Color.Green)
        }

    }

}


//private fun performActionForRequestCode(requestCode: FitActionRequestCode) =
//    when (requestCode) {
//        FitActionRequestCode.READ_DATA -> getFitnessData()
//        FitActionRequestCode.WRITE_DATA -> {
//        }
//    }


private fun handleGoogleFitClick(
    isConnected: Boolean,
    viewModel: HomeViewModel
) {
    if (!isConnected) {
        //
    } else {
//        requestCode.let {
//            GoogleSignIn.requestPermissions(
//                activity,
//                requestCode.ordinal,
//                getGoogleAccount,
//                fitnessOptions
//            )
//        }
    }
}




