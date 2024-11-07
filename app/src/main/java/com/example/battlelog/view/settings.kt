package com.example.battlelog.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.battlelog.R
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.ui.theme.bottomBorder
import com.example.battlelog.ui.theme.topBorder

@Preview(showBackground = true)
@Composable
fun SettingsPreview(){
    BatteLogTheme {
        SettingsView()
    }
}




@Composable
fun SettingsView(
    navController: NavController? = null
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {

            Column(
                modifier = Modifier.padding(top = 50.dp, start = 10.dp, end = 10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.angle_left_solid),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController?.navigateUp()
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 15.dp)
                )
            }

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {

            Column(
                modifier = Modifier.padding(top = 30.dp)
            ) {
                SettingsOption("Login", OnClick = {

                })
                SettingsOption("Cài đặt thông báo", OnClick = {

                })
                SettingsOption("Màu nền ứng dụng", OnClick = {

                })
                SettingsOption("Báo lỗi và phản hồi khách hàng", OnClick = {

                })
                SettingsOption("Đến máy chủ discord", OnClick = {

                })
                SettingsOption("Phiên bản 1.0.0", OnClick = {
                })
            }


        }

    }
}

@Composable
fun SettingsOption(
    title: String,
    OnClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .topBorder(1.dp, Color.Gray)
            .bottomBorder(1.dp, Color.Gray)
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .clickable {
                OnClick()
            },

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Icon(
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer(rotationZ = 180f),
            painter = painterResource(R.drawable.angle_left_solid),
            contentDescription = "Navigate",
        )

    }
}



fun openUrl(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    context.startActivity(intent)
}