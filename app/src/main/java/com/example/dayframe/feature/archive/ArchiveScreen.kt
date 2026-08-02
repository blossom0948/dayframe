package com.example.dayframe.feature.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayframe.DayframeViewModel
import com.example.dayframe.ui.DiaryImage

@Composable
fun ArchiveScreen(viewModel: DayframeViewModel, onOpenDetail: (Long) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites = state.entries.filter { it.isFavorite }
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text("보관함", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("오래 기억하고 싶은 하루", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
        if (favorites.isEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 90.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text("아직 즐겨찾기한 기록이 없어요", style = MaterialTheme.typography.titleMedium)
                Text("상세 화면에서 하트를 눌러 보관해 보세요.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favorites, key = { it.id }) { entry ->
                    Card(onClick = { onOpenDetail(entry.id) }, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            DiaryImage(entry.photoUri, "보관한 ${entry.entryDate} 사진", Modifier.fillMaxWidth().height(150.dp), ContentScale.Crop)
                            Text(entry.title.ifBlank { entry.entryDate.toString() }, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(14.dp))
                        }
                    }
                }
            }
        }
    }
}
