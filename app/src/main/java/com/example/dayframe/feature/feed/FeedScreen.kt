package com.example.dayframe.feature.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayframe.DayframeViewModel
import com.example.dayframe.core.model.DiaryEntry
import com.example.dayframe.ui.DiaryImage
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FeedScreen(
    viewModel: DayframeViewModel,
    onOpenDetail: (Long) -> Unit,
    onCreate: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var oldestFirst by remember { mutableStateOf(false) }
    val monthEntries = state.entries.filter { entry ->
        entry.entryDate.year == state.selectedMonth.year && entry.entryDate.month == state.selectedMonth.month
    }
    val records = (if (state.searchQuery.isBlank()) monthEntries else state.entries)
        .sortedBy { it.entryDate }
        .let { if (oldestFirst) it else it.reversed() }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("피드", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    "${state.selectedMonth.year}년 ${state.selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.KOREAN)} · 사진으로 이어 보는 나의 하루",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = { oldestFirst = !oldestFirst }) { Icon(Icons.Outlined.Sort, "정렬") }
        }
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::setSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) IconButton(onClick = viewModel::clearSearch) { Icon(Icons.Outlined.Close, "검색 지우기") }
            },
            placeholder = { Text("제목, 이야기, 음악 검색") },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(14.dp))
        if (records.isEmpty()) {
            FeedEmptyState(onCreate)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
                items(records, key = { it.id }) { entry ->
                    FeedCard(entry, onClick = { onOpenDetail(entry.id) })
                }
            }
        }
    }
}

@Composable
private fun FeedCard(entry: DiaryEntry, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column {
            DiaryImage(entry.photoUri, "${entry.entryDate} 기록 사진", Modifier.fillMaxWidth().height(210.dp), ContentScale.Crop)
            Column(modifier = Modifier.padding(16.dp)) {
                Text(entry.entryDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 · E", Locale.KOREAN)), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                if (entry.title.isNotBlank()) Text(entry.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
                if (entry.body.isNotBlank()) Text(entry.body, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 5.dp))
                if (entry.musicTitle.isNotBlank()) Text("♫ ${entry.musicTitle}${entry.musicArtist.takeIf { it.isNotBlank() }?.let { " · $it" }.orEmpty()}", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun FeedEmptyState(onCreate: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 80.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Icon(Icons.Outlined.Search, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("아직 기록이 없어요", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp))
        Text("오늘의 사진 한 장을 남겨보세요", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        OutlinedButton(onClick = onCreate, modifier = Modifier.padding(top = 18.dp)) { Text("첫 기록 만들기") }
    }
}
