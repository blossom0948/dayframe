package com.example.dayframe.feature.detail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayframe.DayframeViewModel
import com.example.dayframe.ui.DiaryImage
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DayframeViewModel,
    entryId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val entry = state.entries.firstOrNull { it.id == entryId }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("기록") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "뒤로") } },
                actions = {
                    entry?.let {
                        IconButton(onClick = { viewModel.toggleFavorite(it) }) { Icon(if (it.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder, "즐겨찾기") }
                        IconButton(onClick = { onEdit(it.id) }) { Icon(Icons.Outlined.Edit, "편집") }
                    }
                },
            )
        },
    ) { padding ->
        if (entry == null) {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.Center) {
                Text("기록을 찾을 수 없어요", style = MaterialTheme.typography.titleLarge)
                Button(onClick = onBack, modifier = Modifier.padding(top = 14.dp)) { Text("돌아가기") }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                Text(entry.entryDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 · E", Locale.KOREAN)), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                DiaryImage(entry.photoUri, "${entry.entryDate} 사진", Modifier.fillMaxWidth().height(360.dp).padding(top = 12.dp), androidx.compose.ui.layout.ContentScale.Crop)
                if (entry.title.isNotBlank()) Text(entry.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
                if (entry.body.isNotBlank()) Text(entry.body, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 10.dp))
                if (entry.musicTitle.isNotBlank()) {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(18.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(Icons.Outlined.MusicNote, null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(entry.musicTitle, fontWeight = FontWeight.SemiBold)
                                if (entry.musicArtist.isNotBlank()) Text(entry.musicArtist, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (entry.musicUrl.isNotBlank()) TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(entry.musicUrl))) }) { Text("열기") }
                        }
                    }
                }
                entry.mood?.let { mood -> Text("기분 · ${mood.label}", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp)) }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextButton(onClick = { onEdit(entry.id) }) { Icon(Icons.Outlined.Edit, null); Text("편집", modifier = Modifier.padding(start = 6.dp)) }
                    TextButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Outlined.DeleteOutline, null); Text("삭제", modifier = Modifier.padding(start = 6.dp)) }
                    TextButton(onClick = {
                        val share = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "${entry.entryDate}\n${entry.body}") }
                        context.startActivity(Intent.createChooser(share, "기록 공유"))
                    }) { Icon(Icons.Outlined.IosShare, null); Text("공유", modifier = Modifier.padding(start = 6.dp)) }
                }
                Spacer(Modifier.height(28.dp))
            }
        }
    }

    if (showDeleteDialog && entry != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("기록을 휴지통으로 옮길까요?") },
            text = { Text("삭제한 기록은 나중에 복원할 수 있도록 보관됩니다.") },
            confirmButton = { TextButton(onClick = { viewModel.deleteEntry(entry.id); showDeleteDialog = false; onBack() }) { Text("삭제") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("취소") } },
        )
    }
}
