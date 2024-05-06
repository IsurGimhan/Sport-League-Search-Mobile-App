package com.example.mad_cw2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.mad_cw2.ui.theme.MAD_CW2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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
                    MainGUi(context = this@MainActivity)
                }
            }
        }
    }
}



@Composable
fun MainGUi(context : Context){
    val coroutineScope = rememberCoroutineScope()
    Column {
        Button(
            onClick =
            {
                coroutineScope.launch {
                addLeaguesToDatabase(context)
            }}
        ) {
            Text(text = "Add Leagues to DB")
        }

        Button(
            onClick =
            {
                val intent = Intent(context, SearchClubsByLeague::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(text = "Search for Clubs By League")
        }

        Button(
            onClick =
            {
                val intent = Intent(context, SearchForClubs::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(text = "Search for Clubs")
        }
        Button(
            onClick =
            {
                val intent = Intent(context, SearchClubsInWeb::class.java)
                context.startActivity(intent)
            }
        ) {
          Text("Search Clubs in Web")
        }
    }
}

suspend fun addLeaguesToDatabase(context : Context){
    val database = Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()
    val leagues = readLeaguesFromJson(context)
    database.LeagueDao().insert(leagues)
}

fun readLeaguesFromJson(context: Context): List<League> {
    val inputStream: InputStream = context.assets.open("leagues.json")
    val size: Int = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val jsonString = String(buffer, Charsets.UTF_8)
    val jsonObject = JSONObject(jsonString)
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


