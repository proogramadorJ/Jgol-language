package com.pedrodev.jgol.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pedrodev.jgol.ide.Session
import com.pedrodev.jgol.ide.SessionLogs
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
fun CardsRow(navController: NavController) {
    Row(
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
    navController.navigate("EditorScreen")
    SessionLogs.log("Navegando Para EditorScreen: Carregando código a partir de template padrão.")
}

fun openFile(navController: NavController) = runBlocking {
    SessionLogs.log("Abrindo explorador de arquivos.")
    val file = FileKit.pickFile()
    val filePath = file?.path
    if (!filePath.isNullOrEmpty()) {
        SessionLogs.log("Carregando arquivo ${file.path}")
        if (filePath.toUpperCase().endsWith(".JGOL")) {
            HomeScreenEditScreenSharedData.isFileSelected = true
            HomeScreenEditScreenSharedData.filePath = filePath
            SessionLogs.log("Navegando Para EditorScreen: Carregando código a partir de arquivo.")
            navController.navigate("EditorScreen")
        } else {
            //TODO notificar como?

        }


    }
}