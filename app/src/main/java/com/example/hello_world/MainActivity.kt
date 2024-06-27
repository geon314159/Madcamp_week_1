package com.example.hello_world

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hello_world.ui.theme.Hello_worldTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import coil.compose.rememberAsyncImagePainter


data class Contact(val name: String, val phone: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Hello_worldTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    val tabs = listOf("Contacts", "Gallery", "Custom")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color.Blue,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title, fontWeight = FontWeight.Bold) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (selectedTabIndex) {
                0 -> Tab1Content()
                1 -> Tab2Content()
                2 -> Tab3Content()
            }
        }
    }
}

@Composable
fun Tab1Content() {
    val contacts = remember { mutableStateListOf<Contact>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val data = loadContacts(context)
        contacts.addAll(data)
    }

    LazyColumn {
        items(contacts) { contact ->
            ContactItem(contact)
        }
    }
}

@Composable
fun ContactItem(contact: Contact) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = contact.name, fontWeight = FontWeight.Bold)
        Text(text = contact.phone)
    }
}

suspend fun loadContacts(context: Context): List<Contact> {
    return withContext(Dispatchers.IO) {
        val assetManager = context.assets
        val inputStream = assetManager.open("contacts.json")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = bufferedReader.use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        List(jsonArray.length()) { i ->
            val jsonObject = jsonArray.getJSONObject(i)
            Contact(
                name = jsonObject.getString("name"),
                phone = jsonObject.getString("phone")
            )
        }
    }
}

@Composable
fun Tab2Content() {
    val imageList = remember { (1..20).map { "file:///android_asset/$it.jpeg" } }

    LazyColumn {
        items(imageList) { imageUrl ->
            ImageItem(imageUrl)
        }
    }
}

@Composable
fun ImageItem(imageUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(imageUrl),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}


@Composable
fun Tab3Content() {
    Text(text = "Custom Tab Content")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Hello_worldTheme {
        MyApp()
    }
}
