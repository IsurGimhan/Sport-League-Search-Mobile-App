package com.example.mad_cw2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.mad_cw2.ui.theme.MAD_CW2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SearchClubsByLeague : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_CW2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchClubsByLeagueGUI(this@SearchClubsByLeague)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MAD_CW2Theme {
//        SearchClubsByLeagueGUI()
    }
}

@Composable
fun SearchClubsByLeagueGUI(context : Context){
    var leagueName by rememberSaveable{ mutableStateOf("") }
    var clubs by rememberSaveable{ mutableStateOf<List<Club>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()


    Column{

        Card(
            modifier = Modifier
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 20.dp
            ),
            shape = RoundedCornerShape(16.dp),
        ) {

            OutlinedTextField(
                value = leagueName,
                onValueChange = { leagueName = it },
                label = { Text("Enter league name") }
            )
            Row {
                Button(
                    onClick = {
                        if (leagueName.isNotBlank()) {
                            coroutineScope.launch {
                                clubs = fetchClubs(leagueName,context)
                            }

                        }

                    }
                ) {
                    Text(text = " Retrieve Clubs")
                }

                Button(
                    onClick =
                    {
                        if (clubs.isNotEmpty()) {
                            coroutineScope.launch {
                                saveClubsToDatabase(clubs, context)
                            }
                        }
                    }
                ) {
                    Text(text = "Save clubs to Database")
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 20.dp
            ),
            shape = RoundedCornerShape(16.dp),
        ) {


            LazyColumn {
                items(clubs) { club ->
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("idTeam: ${club.idTeam}")
                        Text("Name: ${club.name}")
                        Text("shortName: ${club.shortName}")
                        Text("alternateName: ${club.alternateName}")
                        Text("formedYear: ${club.formedYear}")
                        Text("league: ${club.league}")
                        Text("stadium: ${club.stadium}")
                        Text("keywords: ${club.keywords}")
                        Text("stadiumThumb: ${club.stadiumThumb}")
                        Text("stadiumLocation: ${club.stadiumLocation}")
                        Text("stadiumCapacity: ${club.stadiumCapacity}")
                        Text("website: ${club.website}")
                        Text("teamJersey: ${club.teamJersey}")
                        Text("teamLogo: ${club.teamLogo}")

                    }
                }
            }
        }
    }
}


suspend fun fetchClubs(leagueName: String, context: Context): List<Club> {

    val urlString = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=" + leagueName.replace(" ", "%20")
    val url = URL(urlString)
    val con: HttpURLConnection = url.openConnection() as HttpURLConnection
// collecting all the JSON string
    var stb = StringBuilder()
// run the code of the launched coroutine in a new thread
    withContext(Dispatchers.IO) {
        var bf = BufferedReader(InputStreamReader(con.inputStream))
        var line: String? = bf.readLine()
        while (line != null) { // keep reading until no more lines of text
            stb.append(line + "\n")
            line = bf.readLine()
        }
    }

    // this contains the full JSON returned by the Web Service
    val json = JSONObject(stb.toString())
// Information about all the books extracted by this function
    var allBooks = StringBuilder()
    var jsonArray: JSONArray = json.getJSONArray("teams")
// extract all the books from the JSON array
    val clubs = mutableListOf<Club>()
    for (i in 0..jsonArray.length()-1) {
        val teamObject = jsonArray.getJSONObject(i)
        clubs.add(
            Club(
                idTeam = teamObject.getString("idTeam"),
                name = teamObject.getString("strTeam"),
                shortName = teamObject.getString("strTeamShort"),
                alternateName = teamObject.getString("strAlternate"),
                formedYear = teamObject.getString("intFormedYear"),
                league = teamObject.getString("strLeague"),
                stadium = teamObject.getString("strStadium"),
                keywords = teamObject.getString("strKeywords"),
                stadiumThumb = teamObject.getString("strStadiumThumb"),
                stadiumLocation = teamObject.getString("strStadiumLocation"),
                stadiumCapacity = teamObject.getString("intStadiumCapacity"),
                website = teamObject.getString("strWebsite"),
                teamJersey = teamObject.getString("strTeamJersey"),
                teamLogo = teamObject.getString("strTeamLogo")
            )
        )
    }
    return clubs
}

suspend fun saveClubsToDatabase(clubs : List<Club>, context : Context){
    val database = Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()
    val clubs = clubs
    database.ClubDao().insert(clubs)
}
