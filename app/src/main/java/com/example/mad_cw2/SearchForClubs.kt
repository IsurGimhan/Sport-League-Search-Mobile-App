package com.example.mad_cw2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.mad_cw2.ui.theme.MAD_CW2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SearchForClubs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_CW2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchForClubsGUI(this@SearchForClubs)
                }
            }
        }
    }
}


@Composable
fun SearchForClubsGUI(context : Context){
    var text by rememberSaveable{ mutableStateOf("")}
    var clubAndImageDetailsMap by rememberSaveable{ mutableStateOf<Map<Club, Bitmap>>(emptyMap())}
    val coroutineScope = rememberCoroutineScope()

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter a text") }
        )

        Button(
            onClick =
            {
                coroutineScope.launch {
                    val selectedClubsList = searchByName(context, text)
                    clubAndImageDetailsMap = selectedClubsList.zip(imageBitMapList(selectedClubsList)).toMap()

                }
            }
        ) {
            Text("Search")
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
                items(clubAndImageDetailsMap.toList()) { (key, value) ->
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${key.name}")
                        Image(
                            bitmap = value.asImageBitmap(),
                            contentDescription = "some useful description",
                        )
                    }
                }
            }


        }
    }
}

suspend fun searchByName(context: Context, inputString: String): List<Club>{
    return withContext(Dispatchers.IO) {
        val database = Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()
        val clubs = database.ClubDao().searchClubs(inputString)
        database.close()
        clubs

    }
}

suspend fun loadWebImage(webAddress: String): Bitmap {
    return withContext(Dispatchers.IO) {
        val url = URL(webAddress)
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        con.connect()

        val inputStream = con.inputStream
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        con.disconnect()

        bitmap
    }
}

suspend fun imageBitMapList(keyList: List<Club>): List<Bitmap>{
    var imageList = mutableListOf<Bitmap>()
    for(i in keyList.indices){
        imageList.add(loadWebImage(keyList[i].teamLogo))
    }
    return imageList
}




