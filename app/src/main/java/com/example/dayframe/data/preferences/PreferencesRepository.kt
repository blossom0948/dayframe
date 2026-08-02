package com.example.dayframe.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dayframeDataStore by preferencesDataStore(name = "dayframe_settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
        val themeMode = stringPreferencesKey("theme_mode")
    }

    val onboardingComplete: Flow<Boolean> = context.dayframeDataStore.data.map {
        it[Keys.onboardingComplete] ?: false
    }

    val themeMode: Flow<ThemeMode> = context.dayframeDataStore.data.map {
        it[Keys.themeMode]?.let { value -> runCatching { ThemeMode.valueOf(value) }.getOrNull() }
            ?: ThemeMode.SYSTEM
    }

    suspend fun completeOnboarding() {
        context.dayframeDataStore.edit { it[Keys.onboardingComplete] = true }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dayframeDataStore.edit { it[Keys.themeMode] = mode.name }
    }

    suspend fun resetOnboarding() {
        context.dayframeDataStore.edit { it[Keys.onboardingComplete] = false }
    }
}
