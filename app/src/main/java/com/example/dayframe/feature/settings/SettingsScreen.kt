package com.example.dayframe.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayframe.DayframeViewModel
import com.example.dayframe.data.preferences.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: DayframeViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showOnboardingDialog by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(title = { Text("설정") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "뒤로") } })
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text("화면", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
            ListItem(headlineContent = { Text("테마") }, supportingContent = { Text("앱 화면의 밝기를 선택하세요") })
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                ThemeOption("시스템", ThemeMode.SYSTEM, state.themeMode, viewModel::setThemeMode)
                ThemeOption("라이트", ThemeMode.LIGHT, state.themeMode, viewModel::setThemeMode)
                ThemeOption("다크", ThemeMode.DARK, state.themeMode, viewModel::setThemeMode)
            }
            Text("데이터", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp))
            ListItem(headlineContent = { Text("온보딩 다시 보기") }, modifier = Modifier.fillMaxWidth(), trailingContent = { TextButton(onClick = { showOnboardingDialog = true }) { Text("열기") } })
            Text("Dayframe 0.1.0 · 사진은 기기에만 저장됩니다.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp))
            ListItem(leadingContent = { Icon(Icons.Outlined.Info, null) }, headlineContent = { Text("개인정보 안내") }, supportingContent = { Text("외부 서버 없이 오프라인 우선으로 동작합니다.") })
        }
    }
    if (showOnboardingDialog) {
        AlertDialog(
            onDismissRequest = { showOnboardingDialog = false },
            title = { Text("온보딩을 다시 볼까요?") },
            text = { Text("다음 실행부터 처음 안내 화면이 표시됩니다.") },
            confirmButton = { TextButton(onClick = { viewModel.showOnboardingAgain(); showOnboardingDialog = false }) { Text("확인") } },
            dismissButton = { TextButton(onClick = { showOnboardingDialog = false }) { Text("취소") } },
        )
    }
}

@Composable
private fun ThemeOption(label: String, mode: ThemeMode, selected: ThemeMode, onSelected: (ThemeMode) -> Unit) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        RadioButton(selected = selected == mode, onClick = { onSelected(mode) })
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
