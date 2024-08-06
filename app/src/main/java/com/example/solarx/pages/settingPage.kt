package com.example.solarx.pages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@SuppressLint("UnrememberedMutableState")
@Preview(device = Devices.PIXEL_3)
@Composable
fun SettingsView(paddingValues: PaddingValues= PaddingValues(0.dp)) {
    var monitorState by remember { mutableStateOf(false)}
    var gridPowerState by remember {
        mutableStateOf("100")
    }
    var wapdaOpenState = remember { mutableStateOf(false)}
    Column(
        Modifier
            .padding(paddingValues = paddingValues)
            .fillMaxSize()) {
        ListItem(
            headlineContent = {
                Text(text = "Enable Grid Monitor")
            },
            supportingContent = {
                Text(text = "Starts a Background Service for Monitoring Grid Power")
            },
            trailingContent = {
                Switch(checked = monitorState, onCheckedChange = { monitorState = it })
            }
        )
        if (monitorState)
        {
            ListItem(
                modifier = Modifier.clickable(enabled = monitorState, onClick = {
                    wapdaOpenState.value = true
                    Log.d("WapdaState", wapdaOpenState.toString())
                }),
                headlineContent = {
                    Text(text = "Warning Grid Power")
                },
                trailingContent = {
                    Text(text = gridPowerState + " W", fontSize = 16.sp)
                }
            )
        }
        if (wapdaOpenState.value) {
            TextDialog(
                state = wapdaOpenState,
                headingText = "Enter WAPDA",
                textDetails = "Please Enter the WAPDA Threshold in Watts when overflows from this limit would start alerting user",
                textEntered = gridPowerState,
                ontextEntered = { gridPowerState = it }
            )
        }
    }
}

@Composable
fun TextDialog(
    state: MutableState<Boolean>,
    headingText: String,
    textDetails: String,
    textEntered: String,
    ontextEntered: (String) -> Unit,
    exitCallback: () -> Unit = {state.value = false}
)
{
    var justRemember by remember {
        mutableStateOf(textEntered)
    }
    Dialog(onDismissRequest = { exitCallback() }) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = headingText, style = MaterialTheme.typography.headlineSmall)
                    Text(text = textDetails, style = MaterialTheme.typography.bodyMedium)
                }
                OutlinedTextField(
                    value = justRemember,
                    onValueChange = { justRemember=it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(modifier=Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = exitCallback ) {
                        Text(text = "Close")
                    }
                    TextButton(onClick = {
                        ontextEntered(justRemember)
                        exitCallback()
                    }) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}