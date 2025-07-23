package dev.lumentae.logkeepr.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultPageTemplate(
    title: String,
    modifier: Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) })
        },
        floatingActionButton = floatingActionButton,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                content()
            }
        }
    }
}