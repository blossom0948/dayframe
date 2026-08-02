package com.example.dayframe.feature.editor

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dayframe.DayframeViewModel
import com.example.dayframe.core.model.DiaryEntry
import com.example.dayframe.core.model.Mood
import com.example.dayframe.ui.DiaryImage
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: DayframeViewModel,
    entryId: Long,
    initialDate: String,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
) {
    var dateText by rememberSaveable(entryId, initialDate) { mutableStateOf(initialDate.ifBlank { LocalDate.now().toString() }) }
    var photoUri by rememberSaveable(entryId) { mutableStateOf("") }
    var title by rememberSaveable(entryId) { mutableStateOf("") }
    var body by rememberSaveable(entryId) { mutableStateOf("") }
    var moodName by rememberSaveable(entryId) { mutableStateOf("") }
    var musicTitle by rememberSaveable(entryId) { mutableStateOf("") }
    var musicArtist by rememberSaveable(entryId) { mutableStateOf("") }
    var musicUrl by rememberSaveable(entryId) { mutableStateOf("") }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var errorText by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(entryId) {
        if (entryId != 0L) {
            viewModel.getEntry(entryId)?.let { entry ->
                dateText = entry.entryDate.toString()
                photoUri = entry.photoUri
                title = entry.title
                body = entry.body
                moodName = entry.mood?.name.orEmpty()
                musicTitle = entry.musicTitle
                musicArtist = entry.musicArtist
                musicUrl = entry.musicUrl
            }
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        photoUri = uri.toString()
        runCatching {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flags)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == 0L) "새 기록" else "기록 편집") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "뒤로") } },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            Text("사진 한 장", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            if (photoUri.isBlank()) {
                Button(
                    onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(top = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.AddPhotoAlternate, null, modifier = Modifier.size(40.dp))
                        Text("사진 선택", modifier = Modifier.padding(top = 8.dp))
                    }
                }
            } else {
                DiaryImage(photoUri, "선택한 사진", Modifier.fillMaxWidth().height(230.dp).padding(top = 10.dp), ContentScale.Crop)
                TextButton(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("사진 바꾸기") }
            }

            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                label = { Text("날짜") },
                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, null) },
                trailingIcon = { TextButton(onClick = { showDatePicker = true }) { Text("선택") } },
                singleLine = true,
                supportingText = { Text("예: 2026-08-02") },
            )
            OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("제목 (선택)") }, singleLine = true)
            OutlinedTextField(
                value = body,
                onValueChange = { if (it.length <= 500) body = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                label = { Text("오늘의 이야기") },
                minLines = 4,
                supportingText = { Text("${body.length} / 500") },
            )
            Text("기분", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 18.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Mood.entries.forEach { mood ->
                    FilterChip(selected = moodName == mood.name, onClick = { moodName = if (moodName == mood.name) "" else mood.name }, label = { Text(mood.label) })
                }
            }
            Text("음악 (선택)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 18.dp))
            OutlinedTextField(value = musicTitle, onValueChange = { musicTitle = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("곡 제목") }, singleLine = true)
            OutlinedTextField(value = musicArtist, onValueChange = { musicArtist = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("아티스트") }, singleLine = true)
            OutlinedTextField(value = musicUrl, onValueChange = { musicUrl = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("음악 링크 (선택)") }, singleLine = true)

            if (errorText.isNotBlank()) Text(errorText, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 10.dp))
            Button(
                onClick = {
                    val parsedDate = runCatching { LocalDate.parse(dateText) }.getOrNull()
                    when {
                        photoUri.isBlank() -> errorText = "사진을 한 장 선택해 주세요."
                        parsedDate == null -> errorText = "날짜를 YYYY-MM-DD 형식으로 입력해 주세요."
                        else -> {
                            errorText = ""
                            viewModel.saveEntry(
                                DiaryEntry(
                                    id = entryId,
                                    entryDate = parsedDate,
                                    title = title,
                                    body = body,
                                    mood = Mood.entries.firstOrNull { it.name == moodName },
                                    photoUri = photoUri,
                                    musicTitle = musicTitle,
                                    musicArtist = musicArtist,
                                    musicUrl = musicUrl,
                                ),
                                onSaved = onSaved,
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp).padding(top = 16.dp),
                shape = RoundedCornerShape(18.dp),
            ) { Text("기록 저장") }
            Spacer(Modifier.height(28.dp))
        }
    }

    if (showDatePicker) {
        val selectedMillis = runCatching { LocalDate.parse(dateText).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() }.getOrNull()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis -> dateText = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString() }
                    showDatePicker = false
                }) { Text("확인") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("취소") } },
        ) { DatePicker(state = datePickerState) }
    }
}
