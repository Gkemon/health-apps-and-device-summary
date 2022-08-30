package com.gk.emon.allhealthappssummary.presentation.googleFit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gk.emon.allhealthappssummary.R
import com.gk.emon.allhealthappssummary.presentation.theme.AppThemeTheme
import com.gk.emon.allhealthappssummary.utils.LoadingContent
import com.gk.emon.allhealthappssummary.utils.getEndTimeString
import com.gk.emon.allhealthappssummary.utils.getStartTimeString
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.result.DataReadResponse


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun GoogleFitScreen(
    viewModel: GoogleFitDataViewModel = hiltViewModel()
) {
    AppThemeTheme {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(10.dp)
        ) {

            DataContent(
                loading = uiState.isLoading,
                empty = uiState.isEmpty,
                data = uiState.item,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.padding(all = 10.dp)
            )
        }
    }
}

@Composable
private fun DataContent(
    loading: Boolean,
    empty: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    data: DataReadResponse?
) {
    val commonModifier = modifier
        .fillMaxWidth()
        .padding(all = 10.dp)

    LoadingContent(
        loading = loading,
        empty = empty,
        onRefresh = onRefresh,
        modifier = modifier,
        emptyContent = {
            Text(
                text = stringResource(id = R.string.no_data_found_for_google_fit),
                modifier = commonModifier
            )
        }
    ) {
        Column(
            commonModifier
                .fillMaxSize()
        ) {
            if (!loading) {
                Text(
                    text = stringResource(id = R.string.google_fit_intro_title),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(10.dp))
                data?.let {
                    generateDataTotalItems(it)
                }
            }
        }
    }

}

@Composable
fun generateDataItem(result: DataSet): Collection<Unit> {
    val views = arrayListOf<Unit>()
    for (dp in result.dataPoints) {
        var result = "Data point:" +
                "\tType: ${dp.dataType.name}" +
                "\tStart: ${dp.getStartTimeString()}" +
                "\tEnd: ${dp.getEndTimeString()}"
        dp.dataType.fields.forEach {
            result += "\tField: ${it.name} Value: ${dp.getValue(it)}"
        }
        views.add(Text(text = result))
    }
    return views
}

@Composable
fun generateDataTotalItems(dataReadResult: DataReadResponse): ArrayList<Unit> {
    val views = arrayListOf<Unit>()
    if (dataReadResult.buckets.isNotEmpty()) {
        for (bucket in dataReadResult.buckets) {
            bucket.dataSets.forEach {
                it?.let { result ->
                    views.addAll(generateDataItem(result))
                }
            }
        }
    } else if (dataReadResult.dataSets.isNotEmpty()) {
        dataReadResult.dataSets.forEach {
            it?.let { result ->
                views.addAll(generateDataItem(result))
            }
        }
    }
    return views;

}












