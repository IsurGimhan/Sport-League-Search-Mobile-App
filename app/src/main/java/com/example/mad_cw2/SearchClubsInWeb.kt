package com.example.mad_cw2

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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

class SearchClubsInWeb : ComponentActivity() {
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
                            SearchClubsInWebGUI()
                        }
                    }



                }
            }
        }
    }
}

@Composable
fun SearchClubsInWebGUI() {
    val coroutineScope = rememberCoroutineScope()
    var jerseysList by rememberSaveable { mutableStateOf<List<Bitmap>>(emptyList()) }
    var inputText by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter the club name") }
            )

            Button(
                onClick =
                {
                    coroutineScope.launch {
                        jerseysList = fetchJerseys(inputText, fetchTeams())
                    }
                }
            ) {
                Text("Search")
            }
        }


        LazyColumn {
            items(jerseysList) { item ->
                Column(modifier = Modifier.padding(16.dp)) {

                    Image(
                        bitmap = item.asImageBitmap(),
                        contentDescription = "jersey image",
                    )
                }
            }
        }
    }
}


// fetchTeams method will retrun list of Team details that fetch from the web service
suspend fun fetchTeams(): List<Teams> {

    val urlString = "https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=Arsenal"
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

    var jsonArray: JSONArray = json.getJSONArray("teams")
    // extract all the clubs from the JSON array
    val teams = mutableListOf<Teams>()
    for (i in 0..<jsonArray.length()) {
        val teamObject = jsonArray.getJSONObject(i)
        teams.add(
            Teams(
                idTeam = teamObject.getString("idTeam"),
                name = teamObject.getString("strTeam"),
                teamJersey = teamObject.getString("strTeamJersey")
            )
        )
    }
    return teams
}


// fetchJerseys method will return list of Bitmaps
suspend fun fetchJerseys(userInput: String, teamList: List<Teams>): List<Bitmap> {
    var jerseyImageList = mutableListOf<Bitmap>()

    for (team in teamList) {
        //check if the userinput is a part of any club names
        if (team.name.lowercase().contains(userInput.lowercase())) {
            val url = "https://www.thesportsdb.com/api/v1/json/3/lookupequipment.php?id=${team.idTeam}"
            val jerseysLinkList = loadJerseysFromWeb(url)

            for (link in jerseysLinkList) {
                jerseyImageList.add(loadWebImage(link.jerseyLink))
            }
        }
    }
    return jerseyImageList
}



// loadJerseysFromWeb method will take the url of a team jersey list and return list of jersey image urls
// base code was taken from the lecture notes
suspend fun loadJerseysFromWeb(urlString: String): List<Jerseys> {

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

    var jsonArray: JSONArray = json.getJSONArray("equipment")
    // extract all the clubs from the JSON array
    val jerseyList = mutableListOf<Jerseys>()

    for (i in 0..<jsonArray.length()) {
        val teamObject = jsonArray.getJSONObject(i)
        jerseyList.add(
            Jerseys(
                jerseyLink = teamObject.getString("strEquipment"),
            )
        )
    }

    return jerseyList
}

