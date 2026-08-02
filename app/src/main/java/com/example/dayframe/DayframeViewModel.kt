package com.example.dayframe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayframe.core.model.DiaryEntry
import com.example.dayframe.data.preferences.PreferencesRepository
import com.example.dayframe.data.preferences.ThemeMode
import com.example.dayframe.data.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DayframeUiState(
    val entries: List<DiaryEntry> = emptyList(),
    val selectedMonth: YearMonth = YearMonth.now(),
    val searchQuery: String = "",
    val onboardingComplete: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

@HiltViewModel
class DayframeViewModel @Inject constructor(
    private val repository: DiaryRepository,
    private val preferences: PreferencesRepository,
) : ViewModel() {
    private val selectedMonth = MutableStateFlow(YearMonth.now())
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<DayframeUiState> = combine(
        repository.observeActive(),
        selectedMonth,
        searchQuery,
        preferences.onboardingComplete,
        preferences.themeMode,
    ) { entries, month, query, onboardingComplete, themeMode ->
        DayframeUiState(
            entries = entries.filter { entry ->
                query.isBlank() || listOf(entry.title, entry.body, entry.musicTitle, entry.musicArtist)
                    .any { it.contains(query, ignoreCase = true) }
            },
            selectedMonth = month,
            searchQuery = query,
            onboardingComplete = onboardingComplete,
            themeMode = themeMode,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DayframeUiState())

    fun completeOnboarding() {
        viewModelScope.launch { preferences.completeOnboarding() }
    }

    fun showOnboardingAgain() {
        viewModelScope.launch { preferences.resetOnboarding() }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferences.setThemeMode(mode) }
    }

    fun setSearchQuery(value: String) {
        searchQuery.value = value
    }

    fun clearSearch() {
        searchQuery.value = ""
    }

    fun previousMonth() {
        selectedMonth.value = selectedMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        selectedMonth.value = selectedMonth.value.plusMonths(1)
    }

    fun jumpToToday() {
        selectedMonth.value = YearMonth.now()
    }

    fun selectMonth(month: YearMonth) {
        selectedMonth.value = month
    }

    fun saveEntry(entry: DiaryEntry, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.save(entry)
            onSaved(id)
        }
    }

    fun deleteEntry(id: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.softDelete(id)
            onDone()
        }
    }

    fun toggleFavorite(entry: DiaryEntry) {
        viewModelScope.launch { repository.setFavorite(entry.id, !entry.isFavorite) }
    }

    fun restoreEntry(id: Long) {
        viewModelScope.launch { repository.restore(id) }
    }

    suspend fun getEntry(id: Long): DiaryEntry? = repository.get(id)
}
