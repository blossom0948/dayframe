package com.example.dayframe.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayframe.DayframeViewModel
import com.example.dayframe.core.model.DiaryEntry
import com.example.dayframe.core.model.monthProgress
import com.example.dayframe.ui.DiaryImage
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: DayframeViewModel,
    onOpenDetail: (Long) -> Unit,
    onCreate: (LocalDate?) -> Unit,
    onSettings: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val month = state.selectedMonth
    val monthEntries = state.entries.filter { it.entryDate.year == month.year && it.entryDate.month == month.month }
    val progress = monthProgress(state.entries, month)
    val today = LocalDate.now()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    month.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(month.year.toString(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { viewModel.setSearchQuery("") }) { Icon(Icons.Outlined.Search, "검색") }
            IconButton(onClick = onSettings) { Icon(Icons.Outlined.Settings, "설정") }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            IconButton(onClick = viewModel::previousMonth) { Icon(Icons.Outlined.ChevronLeft, "이전 달") }
            OutlinedButton(onClick = viewModel::jumpToToday, modifier = Modifier.weight(1f)) {
                Icon(Icons.Outlined.Today, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("오늘")
            }
            IconButton(onClick = viewModel::nextMonth) { Icon(Icons.Outlined.ChevronRight, "다음 달") }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("이번 달 기록", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("${monthEntries.size} / ${month.lengthOfMonth()}일", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))) {
                    Box(modifier = Modifier.fillMaxWidth(progress).height(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        WeekHeader()
        CalendarGrid(
            month = month,
            entries = monthEntries,
            today = today,
            onDateClick = { date, entry -> if (entry == null) onCreate(date) else onOpenDetail(entry.id) },
        )

        if (monthEntries.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("아직 이 달의 기록이 없어요", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("오늘의 사진 한 장으로 첫 장면을 남겨보세요.", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
                    OutlinedButton(onClick = { onCreate(null) }, modifier = Modifier.padding(top = 14.dp)) { Text("첫 기록 만들기") }
                }
            }
        }
    }
}

@Composable
private fun WeekHeader() {
    val labels = listOf("일", "월", "화", "수", "목", "금", "토")
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        labels.forEachIndexed { index, label ->
            Text(label, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = if (index == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    entries: List<DiaryEntry>,
    today: LocalDate,
    onDateClick: (LocalDate, DiaryEntry?) -> Unit,
) {
    val offset = month.atDay(1).dayOfWeek.value % 7
    val cells = buildList<LocalDate?> {
        repeat(offset) { add(null) }
        for (day in 1..month.lengthOfMonth()) add(month.atDay(day))
        while (size % 7 != 0) add(null)
    }
    cells.chunked(7).forEach { week ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            week.forEach { date ->
                Box(modifier = Modifier.weight(1f).height(70.dp)) {
                    if (date != null) {
                        DayCell(date, entries.firstOrNull { it.entryDate == date }, date == today, onDateClick)
                    }
                }
            }
        }
        Spacer(Modifier.height(5.dp))
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    entry: DiaryEntry?,
    isToday: Boolean,
    onDateClick: (LocalDate, DiaryEntry?) -> Unit,
) {
    val isFuture = date.isAfter(LocalDate.now())
    Card(
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).clickable { onDateClick(date, entry) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (entry == null) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (entry != null) DiaryImage(entry.photoUri, "${date} 기록 사진", Modifier.fillMaxSize(), ContentScale.Crop)
            Box(modifier = Modifier.fillMaxSize().background(if (entry != null) Color.Black.copy(alpha = 0.22f) else Color.Transparent))
            Text(
                text = date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.TopStart).padding(7.dp),
                color = when {
                    entry != null -> Color.White
                    isFuture -> MaterialTheme.colorScheme.outline
                    date.dayOfWeek.value == 7 -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            )
            if (isToday) Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp).size(5.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
        }
    }
}
