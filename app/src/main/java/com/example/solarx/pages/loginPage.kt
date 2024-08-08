package com.example.solarx.pages

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.example.solarx.R
import com.example.solarx.api.isValidPassword
import com.example.solarx.api.isValidPassword2
import com.example.solarx.pages.viewM.loginViewModel
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.SocketTimeoutException

class APIModel(): ViewModel() {

    val client = OkHttpClient()

    fun isValidPassword(IP: String, Serial: String, dataChange: (Boolean) -> Unit)
    {
        val formBody = FormBody.Builder()
            .add("optType", "newParaSetting")
            .add("subOption", "pwd")
            .add("Value", Serial)
            .build()

        val request = Request.Builder()
            .url("http://${IP}/")
            .post(formBody)
            .build()

        viewModelScope.launch{
            val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            dataChange(response.body?.string()?.get(0) == 'Y')
        }
    }

}

@Composable
fun LoginPage(navHost: NavHostController)
{
    val SharedPref = navHost.context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    val SharedIPAddress = SharedPref.getString("IPAddress", "192.168.10.10").toString()
    val SharedPocketWIFISN = SharedPref.getString("Serial", "").toString()
    var IPAddress by remember { mutableStateOf(SharedIPAddress) }
    var PocketWIFISN by remember { mutableStateOf(SharedPocketWIFISN) }
    var saveSN by remember { mutableStateOf(true) }
    var isValidPass by remember { mutableStateOf(true)}
    val main = ContextCompat.getMainExecutor(navHost.context)
    var vm = loginViewModel()
    Column (
//        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = Color.Black
            )
            .padding(30.dp, 40.dp)
//            .border(1.dp, Color.White, RectangleShape)
            .padding(15.dp, 50.dp, 15.dp, 80.dp)
//            .border(1.dp, Color.White)
//            .border(1.dp, Color.White)
        ,
    ) {
        // Logo Row
        LoginLogo()

        // User Input Rows
        Column(Modifier.offset(y = -30.dp)) {
            UserInput(
                value = IPAddress,
                changeValue = { IPAddress = it },
                placeholder = "Inverter IP Address",
                keyboardOption = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            UserInput(
                value = PocketWIFISN,
                changeValue = { PocketWIFISN = it },
                placeholder = "Inverter Wifi SN",
                keyboardOption = KeyboardOptions(KeyboardCapitalization.Characters, keyboardType = KeyboardType.Text),
            )
        }

        // Button
        Row () {
            Button(modifier= Modifier
                .fillMaxWidth()
                .height(50.dp),
                onClick = {
                    if (PocketWIFISN.isNullOrEmpty() or IPAddress.isNullOrEmpty())
                    {
                        Toast.makeText(navHost.context, "Please Enter Data into Desired Fields", Toast.LENGTH_SHORT).show();
                        return@Button;
                    }
                    if (PocketWIFISN.contains(" "))
                    {
                        Toast.makeText(navHost.context, "Spaces not Allowed in WI-FI SN", Toast.LENGTH_SHORT).show();
                        return@Button;
                    }
                    vm.isValidPassword(IPAddress, PocketWIFISN) { result ->
                        result.fold(
                            onSuccess = {
                                isValid ->
                                if (isValid) {
                                    main.execute {
                                        SharedPref.edit().putString("IPAddress", IPAddress)
                                            .putString("Serial", PocketWIFISN).apply()
                                        navHost.navigate("Dashboard")
                                    }
                                } else {
                                    main.execute {
                                        Toast.makeText(
                                            navHost.context,
                                            "Wrong Inverter Wifi SN",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            onFailure = {
                                exception ->
                                main.execute {
                                    when(exception) {
                                        is SocketTimeoutException -> {
                                            Toast.makeText(navHost.context, "Inverter not Online or not Present on ${IPAddress}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }

            }) {
                Text(text = "LOGIN")
            }
        }
    }
}

@Composable
fun LoginLogo()
{
    Image(
        painter = painterResource(id = R.drawable.images),
        contentDescription = "Main Logo of SOLAX",
//                modifier = Modifier.background(Color.Tran)
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth()
//                    .border(1.dp, Color.White)
        ,colorFilter = ColorFilter.tint(Color.White, BlendMode.Difference),
    )
}

@Composable
fun UserInput(
    value: String,
    changeValue: (String) -> Unit,
    placeholder: String,
    keyboardOption: KeyboardOptions
) {
    OutlinedTextField(
        value = value,
        onValueChange = {changeValue(it)},
        placeholder = { Text(text = placeholder) },
        label = { Text(text=placeholder) },
        textStyle = TextStyle(Color.White),
        keyboardOptions = keyboardOption,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}