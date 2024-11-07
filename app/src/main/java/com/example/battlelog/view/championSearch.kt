package com.example.battlelog.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.battlelog.R
import com.example.battlelog.model.ChampionSearch
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.viewmodel.ChampionSearchViewModel

@Preview(showBackground = true)
@Composable
fun PreviewChampionSearch() {
    BatteLogTheme {
        championSearch(navController = null)
    }
}

@Composable
fun SearchBarChampion(
    modifier: Modifier,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 0.dp),
        shape = CircleShape,
        value = value,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        onValueChange = {
            onValueChange(it)
        },
        maxLines = 1,
        placeholder = { Text(text = placeholder) },
    )
}

fun categorizeChampions(championNames: List<String>): Map<Char, List<String>> {
    return championNames
        .groupBy { it.first().uppercaseChar() }
        .toSortedMap()
}

@Composable
fun championSearch(
    navController: NavController?,
    viewModel: ChampionSearchViewModel = viewModel()
) {
    var selectedLetter by remember { mutableStateOf('A') }
    var searchQuery by remember { mutableStateOf("") }

    val champions by viewModel.champions.collectAsState()

    val allChampions = champions.groupBy { it.name.first().uppercaseChar() }.toSortedMap()

    val filteredChampions = allChampions.values.flatten().filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.clickable {
                        navController?.navigateUp()
                    }
                ) {
                    Image(
                        painter = painterResource(R.drawable.angle_left_solid),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        alignment = Alignment.Center
                    )
                }
                SearchBarChampion(
                    Modifier, stringResource(R.string.champion_placeholder),
                    value = searchQuery,
                    onValueChange = { searchQuery = it }
                )
            }
        },
        bottomBar = {}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {

            if (searchQuery.isEmpty()) {
                AlphabetBar(
                    onLetterClick = { letter -> selectedLetter = letter },
                    selectedLetter = selectedLetter
                )
            }

            val championsToShow = if (searchQuery.isNotEmpty()) {
                filteredChampions
            } else {
                allChampions[selectedLetter] ?: emptyList()
            }

            val letterToShow = if (searchQuery.isNotEmpty()) ' ' else selectedLetter

            ChampionGrid(championsToShow, letter = letterToShow, navController = navController)

        }
    }
}

@Composable
fun AlphabetBar(onLetterClick: (Char) -> Unit, selectedLetter: Char) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Alphabet", style = MaterialTheme.typography.titleMedium)
        ('A'..'Z').chunked(8).forEach { rowLetters ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowLetters.forEach { letter ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onLetterClick(letter) }
                            .background(
                                color = if (letter == selectedLetter) Color.Blue else Color.LightGray,
                                shape = RoundedCornerShape(5.dp)
                            ),
                    ) {
                        Text(
                            text = letter.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            color = if (letter == selectedLetter) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChampionGrid(
    championSearchList: List<ChampionSearch>,
    letter: Char,
    navController: NavController?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        if (championSearchList.isNotEmpty()) {
            Text(text = letter.toString(), style = MaterialTheme.typography.titleMedium)
        }

        championSearchList.chunked(5).forEach { rowChampions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowChampions.forEach { champion ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = rememberAsyncImagePainter(model =champion.imageUrl),
                            contentDescription = null,
                            modifier = Modifier.size(58.dp)
                                .clickable {
                                navController?.navigate(Routes.championDetail)
                            }
                        )
                        Text(text = champion.name, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}