package com.example.mad_cw2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.mad_cw2.ui.theme.MAD_CW2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var selectedClubs by rememberSaveable{ mutableStateOf<List<Club>>(emptyList()) }
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
                    selectedClubs = searchByName(context, text)
                    Log.d("d",selectedClubs.toString())
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
                items(selectedClubs) { club ->
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${club.name}")
                        Text("teamLogo: ${club.teamLogo}")

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
        Log.d("ss",clubs.toString())
        clubs

    }
}