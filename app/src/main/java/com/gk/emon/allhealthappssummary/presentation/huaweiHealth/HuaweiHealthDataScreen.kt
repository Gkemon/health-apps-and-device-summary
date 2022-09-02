package com.gk.emon.allhealthappssummary.presentation.huaweiHealth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gk.emon.allhealthappssummary.R
import com.gk.emon.allhealthappssummary.presentation.theme.AppThemeTheme
import com.gk.emon.allhealthappssummary.utils.LoadingContent
import com.gk.emon.allhealthappssummary.utils.parseBold


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HuaweiScreen(
    huaweiViewModel: HuaweiViewModel = hiltViewModel()
) {
    AppThemeTheme {
        val uiState by huaweiViewModel.uiState.collectAsStateWithLifecycle()
        DataContent(
            loading = uiState.isLoading,
            empty = uiState.isEmpty,
            items = uiState.items,
            onRefresh = { huaweiViewModel.refresh() },
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxSize()
        )

    }
}

@Composable
private fun DataContent(
    loading: Boolean,
    empty: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    items: List<String>
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
                text = stringResource(id = R.string.no_data_found),
                modifier = commonModifier,
                textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
            )
        }
    ) {
        Column(
            commonModifier
                .fillMaxSize()
        ) {
            if (!loading) {
                Text(
                    text = stringResource(id = R.string.huawei_intro_title),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(5.dp))
                LazyColumn {
                    items(items) { data ->
                        Text(text = data.parseBold())
                    }
                }
            }
        }
    }
}












