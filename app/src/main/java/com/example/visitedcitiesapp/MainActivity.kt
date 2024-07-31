package com.example.visitedcitiesapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.visitedcitiesapp.ui.theme.VisitedCitiesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisitedCitiesAppTheme {
                val cities = remember { mutableStateListOf<City>() }
                val sort = remember { mutableStateOf(false) }
                val sortedList = if (sort.value) cities.sortedBy { it.name[0] } else cities

                CitiesList(
                    sortedList,
                    onAddCity = { name, country ->
                        cities.add(City(name, country))
                    },
                    onSort = { sort.value = !sort.value }
                )
            }
        }
    }
}


data class City(
    val name: String,
    val country: String
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CitiesList(
    list: List<City>,
    onAddCity: (name: String, country: String) -> Unit,
    onSort: () -> Unit
) {
    val showDialog = remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { AddCitiesFab { showDialog.value = true } },
        topBar = { AddTopBar { onSort() } }) {
        if (list.isEmpty())
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No cities available")
            }
        else
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp, top = 90.dp)
            ) {
                items(list) {
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Color(0xffeeeeee)
                            )
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = it.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = it.country)
                    }
                }
            }
    }

    if (showDialog.value) {
        AddCityDialog(OnDismiss = { showDialog.value = false }, onConfirm = { name, country ->
            showDialog.value = false
            onAddCity.invoke(name, country)
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopBar(onSort: () -> Unit) {
    val alpha = painterResource(id = R.drawable.alpha)

    TopAppBar(
        title = { Text(text = "Cities") },
        actions = {
            IconButton(onClick = onSort) {
                Icon(alpha, contentDescription = null)
            }
        })
}

@Composable
fun AddCitiesFab(onFabClick: () -> Unit) {
    FloatingActionButton(onClick = onFabClick) {
        Icon(Icons.Default.Add, contentDescription = "Add city")
    }
}

@Composable
fun AddCityDialog(OnDismiss: () -> Unit, onConfirm: (name: String, country: String) -> Unit) {
    var cityName = remember {
        mutableStateOf(TextFieldValue(""))
    }
    var countryName = remember {
        mutableStateOf(TextFieldValue(""))
    }

    AlertDialog(
        onDismissRequest = { OnDismiss() },
        title = {
            Text(
                text = "Add city",
                modifier = Modifier.padding(5.dp),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = cityName.value,
                    onValueChange = { cityName.value = it },
                    label = { Text(text = "City name") },
                    modifier = Modifier.padding(5.dp)
                )
                TextField(
                    value = countryName.value,
                    onValueChange = { countryName.value = it },
                    label = { Text(text = "Country name") },
                    modifier = Modifier.padding(5.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val n = cityName.value.text
                val c = countryName.value.text
                if (n.isNotEmpty() && c.isNotEmpty()) {
                    onConfirm(n, c)
                }
            }) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            Button(onClick = OnDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}


