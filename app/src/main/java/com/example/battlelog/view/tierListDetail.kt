package com.example.battlelog.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.battlelog.R
import com.example.battlelog.adapter.DataProvider
import com.example.battlelog.ui.theme.bottomBorder
import androidx.compose.foundation.layout.Arrangement

@Composable
fun TierListChampion(
    navController: NavController? = null
) {
    var selectedPage by remember { mutableStateOf("TOP") }
    val pages = listOf("TOP", "JUG", "MID", "ADC", "SUP")

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier.padding(top = 50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = { navController?.navigate(Routes.home) }) {
                        Icon(
                            painter = painterResource(R.drawable.angle_left_solid),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = "Xếp hạng tướng",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                TabBar(pages, selectedPage) {
                    selectedPage = it
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TierListDetail(selectedPage = selectedPage)
        }
    }
}

@Composable
fun TabBar(pages: List<String>, selectedPage: String, onPageSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        pages.forEach { page ->
            Text(
                text = page.uppercase(),
                modifier = Modifier
                    .padding(5.dp)
                    .clickable {
                        onPageSelected(page)
                    }
                    .then(
                        if (selectedPage == page) {
                            Modifier.bottomBorder(1.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier
                        }
                    )
                    .padding(10.dp),
                color = if (selectedPage == page) Color.Black else colorResource(R.color.gray_998F8F)
            )
        }
    }
}

@Composable
fun TierListDetail(
    selectedPage: String,
) {
    val context = LocalContext.current
    val championTiers = DataProvider.loadChampionTierList(context)
        .filter { it.position.equals(selectedPage, ignoreCase = true) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf(
                Pair("#", 0.1f),
                Pair("Tier", 0.12f),
                Pair("Champ", 0.25f),
                Pair("Win%", 0.15f),
                Pair("Pick%", 0.15f),
                Pair("Ban%", 0.15f)
            ).forEach { (header, weight) ->
                Text(
                    text = header,
                    modifier = Modifier.weight(weight),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        championTiers.forEachIndexed { index, entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = (index + 1).toString(), modifier = Modifier.weight(0.1f), style = MaterialTheme.typography.labelSmall)

                Box(
                    modifier = Modifier
                        .weight(0.1f)
                        .offset(x = -10.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = entry.imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .align(Alignment.Center)
                            .offset(x = 3.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(color = MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = entry.tier,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                Text(text = entry.name, modifier = Modifier.weight(0.25f))
                Text(text = "${entry.winRate}%", modifier = Modifier.weight(0.15f))
                Text(text = "${entry.pickRate}%", modifier = Modifier.weight(0.15f))
                Text(text = "${entry.banRate}%", modifier = Modifier.weight(0.15f))
            }
        }
    }
}