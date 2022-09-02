package com.gk.emon.allhealthappssummary.presentation.home

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.gk.emon.allhealthappssummary.utils.parseBold
import com.gk.emon.allhealthappssummary.utils.permissionsBL
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val TAG = "HomeScreen"

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onGoogleFitClick: () -> Unit
) {
    AppThemeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp), horizontalAlignment = CenterHorizontally
        ) {
            Content(
                viewModel,
                onGoogleFitClick
            )
        }

    }
}


@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(
    ExperimentalLifecycleComposeApi::class,
    ExperimentalPermissionsApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun Content(
    viewModel: HomeViewModel,
    onGoogleFitClick: () -> Unit
) {

    val context = LocalContext.current
    val startForResult = managedActivityResultLauncher(viewModel, onGoogleFitClick)
    val bluetoothPermission = rememberMultiplePermissionsState(
        permissionsBL.toList()
    )
    val scan =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                GlobalScope.launch(Dispatchers.Main) {
                    viewModel.fetchPairedDevices(context as ComponentActivity)
                }
            } else {
                viewModel.uiState.value.errorMsg = "No permission given for bluetooth"
            }
        }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Text(
        text =
        stringResource(id = R.string.home_app_title),
        fontSize = 15.sp,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.fillMaxHeight(0.01f))
    AppListItem(
        name = "Google fit",
        icon = R.drawable.ic_google_fit,
        isConnected = uiState.isGoogleFitConnected,
        onAppClick = {
            if (uiState.isGoogleFitConnected) {
                onGoogleFitClick()
            } else {
                val googleSignIn =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .addExtension(viewModel.fitnessOptions)
                        .build()
                val intent = GoogleSignIn.getClient(context, googleSignIn)
                startForResult.launch(intent.signInIntent)
            }
        })
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
        ),
        modifier = Modifier
            .shadow(
                10.dp,
                RectangleShape
            )
            .padding(10.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.1f),
        onClick = {
            handleBluetoothClick(bluetoothPermission, viewModel, context, scan)
        }) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.ic_bluetooth),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .requiredSize(50.dp)
                    .padding(end = 10.dp, top = 10.dp)
            )
            Text(
                text =
                stringResource(id = R.string.home_device_title),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
    Text(
        text = stringResource(id = R.string.paired_device),
        Modifier.padding(10.dp),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.fillMaxHeight(0.01f))
    if (uiState.pairedDevices.isEmpty()) {
        Text(text = stringResource(id = R.string.no_device))
    }
    LazyColumn {
        items(uiState.pairedDevices) { data ->
            BluetoothDeviceItem(data)
        }
    }
    Spacer(modifier = Modifier.fillMaxHeight(0.03f))
    Text(
        text = stringResource(id = R.string.un_paired_device),
        Modifier.padding(10.dp),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.fillMaxHeight(0.01f))
    if (uiState.unPairedDevices.isEmpty()) {
        Text(text = stringResource(id = R.string.no_device))
    }
    LazyColumn {
        items(uiState.unPairedDevices) { data ->
            BluetoothDeviceItem(data)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun handleBluetoothClick(
    bluetoothPermission: MultiplePermissionsState,
    viewModel: HomeViewModel,
    context: Context,
    scan: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    if (bluetoothPermission.allPermissionsGranted) {
        if (viewModel.bluetoothAdapter.isEnabled) {
            GlobalScope.launch(Dispatchers.Main) {
                viewModel.fetchPairedDevices(context as ComponentActivity)
                viewModel.fetchUnPairedDevices(context)
            }
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            scan.launch(enableBtIntent)
        }
    } else {
        bluetoothPermission.launchMultiplePermissionRequest()
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("MissingPermission")
@Composable
private fun BluetoothDeviceItem(data: BluetoothDevice) {
    val openDialog = remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(Color.LightGray)
    ) {
        Image(
            painterResource(R.drawable.ic_bluetooth),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .requiredSize(40.dp)
                .padding(10.dp)
        )
        Text(
            text = data.name.parseBold(),
            fontSize = 15.sp,
            modifier = Modifier.padding(end = 10.dp)
        )
        Button(
            onClick = {
                openDialog.value = true
            }, modifier = Modifier.padding(end = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.click_to_details),
                fontSize = 10.sp,
            )
        }
    }
    showBluetoothDeviceData(bluetoothDevice = data, openDialog)

}

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("MissingPermission")
@Composable
fun showBluetoothDeviceData(bluetoothDevice: BluetoothDevice, openDialog: MutableState<Boolean>) {
    AppThemeTheme {
        Column {
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    title = {
                        Text(text = stringResource(id = R.string.device_details))
                    },
                    text = {
                        Text(
                            text = "Name: " + bluetoothDevice.name + "\n" +
                                    "Address: " + bluetoothDevice.address + "\n" +
                                    "Alias: " + bluetoothDevice.alias.toString()
                        )
                    },
                    confirmButton = {
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                            }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun managedActivityResultLauncher(
    viewModel: HomeViewModel,
    onGoogleFitClick: () -> Unit
) =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.isGoogleFitConnected.value=true
            onGoogleFitClick()
        }
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
            horizontalAlignment = CenterHorizontally,
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
                        .padding(end = 10.dp, top = 10.dp)
                )
                Text(text = name, fontSize = 20.sp)
            }
            val result = when (isConnected) {
                true -> "✔ Connected"
                false -> "✘ Not connected"
            }
            Text(
                text = result,
                fontSize = 15.sp,
                color = if (isConnected) Color.Green else Color.Red,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}







