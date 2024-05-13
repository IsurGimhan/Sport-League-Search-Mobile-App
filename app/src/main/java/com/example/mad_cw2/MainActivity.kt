package com.example.mad_cw2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.mad_cw2.ui.theme.MAD_CW2Theme
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_CW2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box{
                        Image(
                            painter = painterResource(id = R.drawable.img1),
                            contentDescription = "BackgroundImage",
                            modifier = Modifier.fillMaxSize()
                        )

                        Box{
                            MainGUi(context = this@MainActivity)
                        }
                    }

                }
            }
        }
    }
}


// Main GUI method use to display the home page
@Composable
fun MainGUi(context : Context){
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Button(
            onClick =
            {
                coroutineScope.launch {
                addLeaguesToDatabase(context)
                }
            },
            modifier = Modifier
                .padding(3.dp)
                .size(230.dp, 45.dp)
        ) {
            Text(text = "Add Leagues to DB")
        }

        Button(
            onClick =
            {
                val intent = Intent(context, SearchClubsByLeague::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .padding(3.dp)
                .size(230.dp, 45.dp)
        ) {
            Text(text = "Search for Clubs By League")
        }

        Button(
            onClick =
            {
                val intent = Intent(context, SearchForClubs::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .padding(3.dp)
                .size(230.dp, 45.dp)
        ) {
            Text(text = "Search for Clubs")
        }
        Button(
            onClick =
            {
                val intent = Intent(context, SearchClubsInWeb::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .padding(3.dp)
                .size(230.dp, 45.dp)
        ) {
          Text("Search Clubs in Web")
        }
    }
}

//add league details to the database
suspend fun addLeaguesToDatabase(context : Context){
    val database = Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()
    val leagues = readLeaguesFromAssetFolder(context)
    database.LeagueDao().insert(leagues)
}

//base code was taken from internet
// read json file in the asset folder and add it to list of league object
fun readLeaguesFromAssetFolder(context: Context): List<League> {
    //read the file in asset folder
    val inputStream: InputStream = context.assets.open("leagues.json")
    val size: Int = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val jsonString = String(buffer, Charsets.UTF_8)
    val jsonObject = JSONObject(jsonString)
    // add all the leagues to jsonArray
    val jsonArray = jsonObject.getJSONArray("leagues")
    val leagues = mutableListOf<League>()
    for (i in 0 until jsonArray.length()) {
        val leagueObject = jsonArray.getJSONObject(i)
        val leagueId = leagueObject.getString("idLeague")
        val leagueName = leagueObject.getString("strLeague")
        val sport = leagueObject.getString("strSport")
        val leagueAlternateName = leagueObject.getString("strLeagueAlternate")
        leagues.add(League(leagueId.toInt(), leagueName, sport,leagueAlternateName))
    }
    return leagues
}


