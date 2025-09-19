package com.questsmith.core.data.network

import com.questsmith.core.domain.model.Stat
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.anonymous
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class QuestSmithSupabaseClient(
    private val supabase: SupabaseClient?,
    private val json: Json
) {
    suspend fun ensureAnonymousUser(userIdProvider: () -> String, onUserId: (String) -> Unit) {
        if (supabase == null) return
        val existing = userIdProvider()
        if (existing.isNotBlank()) return
        val anon = supabase.auth.signUpWith(anonymous())
        onUserId(anon.user?.id ?: UUID.randomUUID().toString())
    }

    suspend fun saveQuizRun(
        userId: String,
        tally: Map<Stat, Int>,
        aiSummary: String?
    ) = withContext(Dispatchers.IO) {
        val client = supabase ?: return@withContext
        val payload = mapOf(
            "user_id" to userId,
            "stat_tally" to json.encodeToString(tally),
            "ai_summary" to aiSummary
        )
        client.postgrest[QUIZ_RUNS_TABLE].insert(payload)
    }

    companion object {
        const val QUIZ_RUNS_TABLE = "quiz_runs"
    }
}
