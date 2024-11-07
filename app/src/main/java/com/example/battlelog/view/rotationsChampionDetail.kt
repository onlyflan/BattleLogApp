package com.example.battlelog.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.battlelog.R
import com.example.battlelog.model.ChampionRotation
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.viewmodel.HomeViewModel
import com.noahkohrs.riot.api.RiotApi


@Preview(showBackground = true)
@Composable
fun PreviewPage(){
    BatteLogTheme {
        rotationsChampionDetail(riotApi = riotApi)
    }
}


@Composable
fun rotationsChampionDetail(
    navController: NavController? = null,
    riotApi: RiotApi,
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        homeViewModel.loadFreeChampions(riotApi, "splash", context)
    }
    val freeChampions by homeViewModel.freeChampions.collectAsState()

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier.padding(top = 50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        onClick = {
                            navController?.navigate(Routes.home)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.angle_left_solid),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = "Tướng xoay tua",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (freeChampions.isEmpty()) {
                Text("Loading champions...", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    items(freeChampions.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SplashRotationChamp(champion = pair[0])
                            if (pair.size > 1) {
                                SplashRotationChamp(champion = pair[1])
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SplashRotationChamp(
    champion: ChampionRotation
) {
    Column(
        modifier = Modifier
            .size(180.dp)
            .padding()
    ) {
        Image(
            painter = rememberAsyncImagePainter(champion.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .width(170.dp)
                .clip(RoundedCornerShape(5.dp)),
            alignment = Alignment.TopStart
        )

        Text(
            text = "Tướng miễn phí",
            color = Color(0xFFFF7223),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(text = champion.name, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = champion.title,
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}