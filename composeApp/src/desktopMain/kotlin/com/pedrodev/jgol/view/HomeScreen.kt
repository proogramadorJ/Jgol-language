package com.pedrodev.jgol.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pedrodev.jgol.shared.HomeScreenEditScreenSharedData
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.pickFile
import jgol.composeapp.generated.resources.Res
import jgol.composeapp.generated.resources.folder
import jgol.composeapp.generated.resources.new_document
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun HomeScreen(navController: NavController) {
    MaterialTheme {
        MainContent(navController)
    }
}

@Preview
@Composable
fun MainContent(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.DarkGray
    ) {
        CardsRow(navController)
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun CardsRow(navController: NavController) { Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Card(
            onClick = { openFile(navController) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp)
                .height(100.dp),
            backgroundColor = Color.LightGray
        ) {

            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    modifier = Modifier
                        .width(35.dp)
                        .height(35.dp),
                    painter = painterResource(Res.drawable.folder),
                    contentDescription = "Open Icon"
                )
                Text(modifier = Modifier.padding(top = 10.dp), text = "Abrir...")
            }
        }

        Card(
            onClick = { createNewFile(navController) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp)
                .height(100.dp),
            backgroundColor = Color.LightGray
        ) {

            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    modifier = Modifier.width(35.dp).height(35.dp),
                    painter = painterResource(Res.drawable.new_document),
                    contentDescription = "Add Icon"
                )
                Text(modifier = Modifier.padding(top = 10.dp), text = "Novo Arquivo")
            }
        }
    }
}

fun createNewFile(navController: NavController) {
    println("New File")
    navController.navigate("EditorScreen")
}

fun openFile(navController : NavController) = runBlocking {
    println("Open File")
    val file = FileKit.pickFile()
    val filePath = file?.path

    if (!filePath.isNullOrEmpty()) {
        println("Selected file path is: $filePath")
        HomeScreenEditScreenSharedData.isFileSelected = true
        HomeScreenEditScreenSharedData.filePath = filePath
        navController.navigate("EditorScreen")
    } else {
        println("No file selected")
    }

}