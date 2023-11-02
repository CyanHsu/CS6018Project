package com.example.paintproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.paintproject.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes

import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
//import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import android.app.Activity
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.asAndroidBitmap

@Serializable
data class SharedBitmapData(val bitmap: String, val userID: String)

class GalleryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater,container,false)


        return ComposeView(requireContext()).apply {
            setContent {

                MaterialTheme {
                    FetchAndDisplayData()
                }
            }
        }
    }


    suspend fun getAllBitmap(): List<Map<String, String>> {
        try {


            val client = HttpClient(CIO)

            val response: HttpResponse = client.get("http://10.0.2.2:8080/drawings")

            val responseText = response.bodyAsText()
//        println(responseText.toString())

            val jsonArray = Json.parseToJsonElement(responseText).jsonArray

            val result = jsonArray.map { jsonObject ->
                val fileName = jsonObject.jsonObject["fileName"]?.jsonPrimitive?.content ?: ""
                val userID = jsonObject.jsonObject["creatorId"]?.jsonPrimitive?.content ?: ""
                mapOf("fileName" to fileName, "userID" to userID)
            }


            client.close()
            return result

        } catch (e: Exception) {
            println("Error: ${e.message}")
            return emptyList()

        }

    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun FetchAndDisplayData() {

        var responseText by remember { mutableStateOf("Loading...") }
        var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
        val scope = rememberCoroutineScope()
        var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
        var bitmapNameList by remember { mutableStateOf<List<String>>(emptyList()) }
        var userIDs by remember { mutableStateOf<List<String>>(emptyList()) }

        DisposableEffect(Unit) {
            scope.launch {
                val list = getAllBitmap()
                for (bitmapData in list) {
                    val bitmap = bitmapData["fileName"]
                    val userID = bitmapData["userID"]
                    val client = HttpClient(CIO)

                    try {
                        val authorizationToken = "jIpW9fIFptWwub1GRYVAZKLhXp83"

                        val response: HttpResponse =
                            client.get("http://10.0.2.2:8080/drawings/$bitmap")
                        bitmapNameList += bitmap!!
                        println("Response Status: ${response.status}")

                        if (response.status.isSuccess()) {
                            responseText = response.status.toString()
                            val imageBytes = response.readBytes()

                            val bitmapResponse =
                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            //imageBitmap = bitmapResponse.copy(Bitmap.Config.ARGB_8888, true)
                            bitmaps += bitmapResponse.copy(Bitmap.Config.ARGB_8888, true)
                            userIDs += userID!!
                        } else {
                            responseText = "Error: ${response.status}"
                        }

                    } catch (e: Exception) {
                        responseText = "Error: ${e.message}"
                    } finally {
                        client.close()
                    }
                }


            }

            onDispose { /* Dispose any resources if needed */ }
        }
        Scaffold(

            floatingActionButton = {
                Button(
                    onClick = {
                        findNavController().navigate(R.id.homeFragment)
                        //handle back
                    },
                ) {
                    Text("Back")
                }
            }


        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(128.dp),

                    // Content padding
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        top = 16.dp,
                        end = 12.dp,
                        bottom = 16.dp
                    ),

                    content = {

                        items(bitmaps.size) { index ->
                            val bitmap = bitmaps[index]
                            val imageBitmap = bitmap.asImageBitmap()
                            val cardModifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {}
                                .semantics { testTag = "cardDrawing" }
                            Card(
                                backgroundColor = Color(0xFF00CCCC),
                                modifier = cardModifier,
                                elevation = 8.dp,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(200.dp)
                                            .fillMaxWidth()
                                        //contentScale = ContentScale.Crop
                                    )
                                    Button(
                                        onClick = {
                                            val viewModel: SimpleViewModel by activityViewModels()
                                            viewModel.bitmap = imageBitmap.asAndroidBitmap()
                                            findNavController().navigate(R.id.drawFragment)
                                            //handle back
                                        },
                                        colors = ButtonDefaults.buttonColors(Color(0xFFCCCCCC))
                                    ) {
                                        Text("edit")
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}





