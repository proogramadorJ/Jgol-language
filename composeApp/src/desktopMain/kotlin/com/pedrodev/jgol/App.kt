package com.pedrodev.jgol

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
import androidx.compose.material.Colors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jgol.composeapp.generated.resources.Res
import jgol.composeapp.generated.resources.folder
import jgol.composeapp.generated.resources.new_document
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainContent()

    }
}

@Preview
@Composable
fun MainContent() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.DarkGray
    ) {
        CardsRow()
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun CardsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Card(
            onClick = { openFile() },
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
            onClick = { createNewFile() },
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

fun createNewFile() {
    println("New File")
}

fun openFile() {
    println("Open File")
}