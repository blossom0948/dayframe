package com.example.dayframe.core.model

enum class Mood(val label: String, val colorHex: Long) {
    GREAT("최고", 0xFF2563EB),
    GOOD("좋음", 0xFF16A34A),
    NORMAL("평범", 0xFF64748B),
    TIRED("피곤", 0xFFF59E0B),
    HARD("힘듦", 0xFFDC2626),
}
