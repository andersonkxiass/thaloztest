package br.com.anderson.thaloztest.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SimpleListScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Text(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 7.dp),
            text = "This is a simple list", style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(100) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 7.dp, horizontal = 7.dp)
                        .clickable(
                            onClick = {
                                Toast
                                    .makeText(context, "Item $it", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        ),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "Title $it", style = MaterialTheme.typography.titleSmall)
                    Text(text = "Subtitle $it", style = MaterialTheme.typography.bodySmall)
                }

                HorizontalDivider()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SimpleListScreenPreview() {
    SimpleListScreen()
}
