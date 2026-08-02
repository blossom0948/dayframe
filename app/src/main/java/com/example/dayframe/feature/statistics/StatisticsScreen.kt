package com.example.dayframe.feature.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayframe.DayframeViewModel
import com.example.dayframe.core.model.currentStreak
import com.example.dayframe.core.model.longestStreak
import com.example.dayframe.core.model.monthProgress

@Composable
fun StatisticsScreen(viewModel: DayframeViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val entries = state.entries
    val monthEntries = entries.filter { it.entryDate.year == state.selectedMonth.year && it.entryDate.month == state.selectedMonth.month }
    val moodCounts = entries.groupingBy { it.mood?.label ?: "기록 안 함" }.eachCount().entries.sortedByDescending { it.value }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("통계", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("꾸준히 남긴 장면을 한눈에 확인해요", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("이번 달", "${monthEntries.size}일", Modifier.weight(1f))
                MetricCard("연속 기록", "${currentStreak(entries)}일", Modifier.weight(1f))
                MetricCard("올해", "${entries.count { it.entryDate.year == java.time.LocalDate.now().year }}일", Modifier.weight(1f))
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("기록 습관", fontWeight = FontWeight.SemiBold)
                    Text("최장 연속 기록 ${longestStreak(entries)}일", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
                    Text("${(monthProgress(entries, state.selectedMonth) * 100).toInt()}%의 날짜에 기록했어요", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("기분 분포", fontWeight = FontWeight.SemiBold)
                    if (moodCounts.isEmpty()) Text("아직 기분을 선택한 기록이 없어요", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
                    moodCounts.forEach { (mood, count) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                            Text(mood, modifier = Modifier.weight(1f))
                            Text("${count}회", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
