package com.questsmith.core.domain.model

enum class Stat(val emoji: String) {
    STRENGTH("\uD83D\uDCAA"),
    INTELLIGENCE("\uD83E\uDDE0"),
    WISDOM("\uD83D\uDD2E"),
    DEXTERITY("\uD83C\uDFBE"),
    CHARISMA("\uD83C\uDF1F"),
    CONSTITUTION("\uD83D\uDEE1\uFE0F");

    val displayName: String = name.lowercase().replaceFirstChar { it.uppercase() }
}
