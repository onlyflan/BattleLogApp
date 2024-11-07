package com.example.battlelog.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.battlelog.R
import com.example.battlelog.ui.theme.BatteLogTheme


@Preview(showBackground = true)
@Composable
fun ChampionDetailPreview(){
    BatteLogTheme {
        ChampionDetail()
    }
}
// Class data de dummy du lieu
data class Image(val imageRes: Int, val shortcut: String)

@SuppressLint("SuspiciousIndentation")
@Composable
fun ChampionDetail(
    navController: NavController? = null
) {
    // Dummy data
    val skills = listOf(
        Image(imageRes = R.drawable.quinn_q, shortcut = "Q"),
        Image(imageRes = R.drawable.quinn_w, shortcut = "W"),
        Image(imageRes = R.drawable.quinn_e, shortcut = "E"),
        Image(imageRes = R.drawable.quinn_r, shortcut = "R"),
    )
    val skillOrderList = listOf(
        Image(imageRes = R.drawable.quinn_q, shortcut = "Q"),
        Image(imageRes = R.drawable.quinn_w, shortcut = "W"),
        Image(imageRes = R.drawable.quinn_e, shortcut = "E"),
    )
    val spellList = listOf(
        Image(R.drawable.spell_flash, "Flash"),
        Image(R.drawable.spell_ignite, "Ignite")
    )
    val itemList = listOf(
        Image(R.drawable.vo_cuc, "Vô Cực"),
        Image(R.drawable.loi_nhac, "Lời Nhắc")
    )
    val counterChampions = listOf(
        Image(R.drawable.rell, "Rell"),
        Image(R.drawable.samira, "Samira")
    )

    Scaffold (
        topBar = {
            ChampionDetailHeader(
                navController = navController,
                championName = "Quinn",
                tierChamp = "B"
            )
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            championSkills(skills)
            ChampionRunes()
            championSpells(spellList)
            championSkillOrder(skillOrderList)
            championItemBuilds(itemList)
            counterChampions(counterChampions)
        }

    }

}

@Composable
fun ChampionDetailHeader(
    navController: NavController?,
    championName: String,
    tierChamp: String,
){
    Column (
        modifier = Modifier

            .fillMaxWidth()
            // Profile champion art
            .paint(
                painterResource(R.drawable.quin_profile),
                contentScale = ContentScale.FillBounds
            ),

        ){

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            IconButton(
                onClick = {
                    navController?.navigateUp()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.fachevronleft),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp),
                    tint = Color.White

                )

            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            //Tier

            val backgroundColor = when (tierChamp) {
                "S" -> Color(0xFFE44848) // S Tier Color
                "A" -> Color(0xFF49A0D5) // A Tier Color
                "B" -> Color(0xFF5AD19E) // B Tier Color
                "C" -> Color(0xFFFFB900) // C Tier Color
                "D" -> Color(0xFF9AA4AF) // D Tier Color
                else -> Color.Transparent
            }
            Text(
                text = tierChamp,
                modifier = Modifier
                    .background(backgroundColor)
                    .padding(top = 2.dp, bottom = 2.dp, start = 5.dp, end = 5.dp),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )


            // champion name
            Text(
                text =  championName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight(500)

            )



        }

    }
}


@Composable
fun championSkills(skills: List<Image>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.skillText),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            skills.forEach { skill ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = 10.dp)
                ) {
                    Image(
                        painter = painterResource(skill.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(35.dp).clip(RoundedCornerShape(5.dp))
                    )
                    Text(
                        text = skill.shortcut,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .offset(x = 10.dp, y = -20.dp)
                            .background(Color.Red)
                            .padding(top = 1.dp, bottom = 1.dp, start = 3.dp, end = 3.dp),
                        color = Color.White,
                    )
                }
            }
        }
    }
}



@Composable
fun ChampionRunes() {
    // Ngọc
    val columns = listOf(
        listOf(
            Image(R.drawable.electrocute, ""), // chính
            Image(R.drawable.cheapshot, ""),
            Image(R.drawable.zombieward, ""),
            Image(R.drawable.relentlesshunter, "")
        ),
        listOf(
            Image(R.drawable.darkharvest, ""), // chính
            Image(R.drawable.greenterror_tasteofblood, ""),
            Image(R.drawable.ghostporo, ""),
            Image(R.drawable.relentlesshunter, "")
        ),
        listOf(
            Image(R.drawable.hailofblades, ""), // chính
            Image(R.drawable.suddenimpact, ""),
            Image(R.drawable.eyeballcollection, ""),
            Image(R.drawable.ultimatehunter, "")
        )
    )

    val statRunes = listOf(
        Image(R.drawable.statmodsattackspeedicon, ""), // Hàng 1
        Image(R.drawable.statmodsadaptiveforceicon, ""),  // Hàng 2
        Image(R.drawable.statmodshealthplusicon, ""),  // Hàng 3
    )

    Text(
        text = stringResource(R.string.runesText),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 10.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ){


        // Bảng chính
        Column(
            modifier = Modifier.fillMaxWidth(0.5f),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.domination),
                contentDescription = null,
                modifier = Modifier
                    .size(25.dp)
                    .clip(RoundedCornerShape(25.dp))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                columns.forEach { columnIcons ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Item đầu tiên trong listOf sẽ đứng đầu vói size to hơn 2.dp
                        // Ngọc chính
                        Image(
                            painter = painterResource(columnIcons.first().imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(25.dp))
                        )
                        // Ngọc phụ
                        columnIcons.drop(1).forEach { icon ->
                            Image(
                                painter = painterResource(icon.imageRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(RoundedCornerShape(25.dp))
                            )
                        }
                    }
                }
            }
        }

        //Bảng phụ

        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.domination),
                contentDescription = null,
                modifier = Modifier
                    .size(25.dp)
                    .clip(RoundedCornerShape(25.dp))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                columns.forEach { columnIcons ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Ngọc phụ
                        columnIcons.drop(1).forEach { icon ->
                            Image(
                                painter = painterResource(icon.imageRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(RoundedCornerShape(25.dp))
                            )
                        }
                    }
                }
            }
        }

        // Chỉ số
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            statRunes.forEach { statRune ->
                Image(
                    painter = painterResource(statRune.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clip(RoundedCornerShape(25.dp))
                )
            }

        }

    }


}
@Composable
fun championSpells(spells: List<Image>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Phép bổ trợ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            spells.forEach { spell ->
                Image(
                    painter = painterResource(spell.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp).clip(RoundedCornerShape(25.dp))
                )
            }
        }
    }
}

@Composable
fun championSkillOrder(skills: List<Image>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Kỹ năng ưu tiên",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            skills.forEach { skill ->
                Image(
                    painter = painterResource(skill.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp)
                        .clip(RoundedCornerShape(25.dp))
                )
            }
        }
    }
}

@Composable
fun championItemBuilds(items: List<Image>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Lối lên đồ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items.forEach { item ->
                Image(
                    painter = painterResource(item.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp).clip(RoundedCornerShape(25.dp))
                )
            }
        }
    }
}

@Composable
fun counterChampions(champions: List<Image>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Tướng khắc chế",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            champions.forEach { champion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(champion.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(35.dp).clip(RoundedCornerShape(25.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = champion.shortcut,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}