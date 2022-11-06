package com.coder.remindme.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.coder.remindme.R

@Composable
fun AppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Remind Me",
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = {}, enabled = false) {
                Image(
                    painter = painterResource(id = R.drawable.thinking),
                    contentDescription = "Remind Me",
                    modifier = Modifier.height(40.dp)
                )
            }
        },
        backgroundColor = colorResource(id = R.color.purple_700),
        contentColor = Color.White,
        elevation = 12.dp
    )
}