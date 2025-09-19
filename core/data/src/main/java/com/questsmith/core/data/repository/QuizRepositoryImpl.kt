package com.questsmith.core.data.repository

import com.questsmith.core.data.network.OpenRouterClient
import com.questsmith.core.data.network.QuestSmithSupabaseClient
import com.questsmith.core.domain.model.Option
import com.questsmith.core.domain.model.Question
import com.questsmith.core.domain.model.Question.Companion.SCALE_KEY
import com.questsmith.core.domain.model.QuestionKind
import com.questsmith.core.domain.model.QuizResult
import com.questsmith.core.domain.model.Stat
import com.questsmith.core.domain.repository.QuizRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val supabaseClient: QuestSmithSupabaseClient,
    private val openRouterClient: OpenRouterClient,
    private val userIdProvider: UserIdProvider
) : QuizRepository {

    override fun seed(): List<Question> = seedQuestions

    override suspend fun saveResult(tally: Map<Stat, Int>, aiSummary: String?) {
        supabaseClient.ensureAnonymousUser(
            userIdProvider = { userIdProvider.getUserId() },
            onUserId = { userIdProvider.setUserId(it) }
        )
        val userId = userIdProvider.getUserId()
        supabaseClient.saveQuizRun(userId = userId, tally = tally, aiSummary = aiSummary)
    }

    override suspend fun generateCoachSummary(result: QuizResult): String =
        openRouterClient.generateCoachSummary(result)

    private val seedQuestions: List<Question> = listOf(
        Question(
            id = "q1",
            title = "How do you prefer to learn?",
            kind = QuestionKind.Single,
            options = listOf(
                Option("reading", "Reading / Studying \uD83E\uDDE0"),
                Option("video", "Watching a video \uD83E\uDDE0 \uD83D\uDD2E"),
                Option("doing", "Doing it yourself \uD83C\uDFBE"),
                Option("group", "Group setting \uD83C\uDF1F")
            ),
            statWeights = mapOf(
                "reading" to listOf(Stat.INTELLIGENCE),
                "video" to listOf(Stat.INTELLIGENCE, Stat.WISDOM),
                "doing" to listOf(Stat.DEXTERITY),
                "group" to listOf(Stat.CHARISMA)
            )
        ),
        Question(
            id = "q2",
            title = "Where’s your focus when it comes to trying to be healthy?",
            kind = QuestionKind.Multi,
            options = listOf(
                Option("eating", "Eating well \uD83D\uDEE1\uFE0F"),
                Option("active", "Being active \uD83D\uDCAA \uD83C\uDFBE"),
                Option("relax", "Relaxing \uD83D\uDD2E"),
                Option("checkups", "Regular check-ups \uD83D\uDEE1\uFE0F")
            ),
            statWeights = mapOf(
                "eating" to listOf(Stat.CONSTITUTION),
                "active" to listOf(Stat.STRENGTH, Stat.DEXTERITY),
                "relax" to listOf(Stat.WISDOM),
                "checkups" to listOf(Stat.CONSTITUTION)
            )
        ),
        Question(
            id = "q3",
            title = "What kind of physical activity makes you the most happy?",
            kind = QuestionKind.Single,
            options = listOf(
                Option("outdoor", "Outdoor sports / adventures \uD83D\uDEE1\uFE0F"),
                Option("team", "Team sports \uD83C\uDF1F"),
                Option("solo", "Working out alone \uD83D\uDCAA"),
                Option("fun", "Fun activities \uD83C\uDFBE")
            ),
            statWeights = mapOf(
                "outdoor" to listOf(Stat.CONSTITUTION),
                "team" to listOf(Stat.CHARISMA),
                "solo" to listOf(Stat.STRENGTH),
                "fun" to listOf(Stat.DEXTERITY)
            )
        ),
        Question(
            id = "q4",
            title = "What makes you feel the most energized?",
            kind = QuestionKind.Single,
            options = listOf(
                Option("challenge", "Challenging yourself \uD83D\uDCAA \uD83C\uDFBE"),
                Option("friends", "Friends \uD83C\uDF1F"),
                Option("quiet", "Quiet time \uD83D\uDD2E"),
                Option("creating", "Creating / Making \uD83E\uDDE0")
            ),
            statWeights = mapOf(
                "challenge" to listOf(Stat.STRENGTH, Stat.DEXTERITY),
                "friends" to listOf(Stat.CHARISMA),
                "quiet" to listOf(Stat.WISDOM),
                "creating" to listOf(Stat.INTELLIGENCE)
            )
        ),
        Question(
            id = "q5",
            title = "How do you like to solve tough problems?",
            kind = QuestionKind.Single,
            options = listOf(
                Option("thinking", "Thinking / Planning \uD83E\uDDE0"),
                Option("testing", "Testing / Trying \uD83C\uDFBE"),
                Option("teamwork", "Advice / Teamwork \uD83C\uDF1F"),
                Option("trust", "Trusting yourself \uD83D\uDD2E")
            ),
            statWeights = mapOf(
                "thinking" to listOf(Stat.INTELLIGENCE),
                "testing" to listOf(Stat.DEXTERITY),
                "teamwork" to listOf(Stat.CHARISMA),
                "trust" to listOf(Stat.WISDOM)
            )
        ),
        Question(
            id = "q6",
            title = "How do you like to recharge?",
            kind = QuestionKind.Multi,
            options = listOf(
                Option("nature", "Nature \uD83D\uDEE1\uFE0F"),
                Option("games", "Games / Puzzles \uD83E\uDDE0"),
                Option("movies", "Movies / TV \uD83C\uDF1F"),
                Option("music", "Music / Podcasts \uD83D\uDD2E")
            ),
            statWeights = mapOf(
                "nature" to listOf(Stat.CONSTITUTION),
                "games" to listOf(Stat.INTELLIGENCE),
                "movies" to listOf(Stat.CHARISMA),
                "music" to listOf(Stat.WISDOM)
            )
        ),
        Question(
            id = "q7",
            title = "Do you enjoy working out?",
            kind = QuestionKind.Scale,
            options = listOf(
                Option("1", "Not really"),
                Option("2", "Sometimes"),
                Option("3", "Absolutely")
            ),
            statWeights = mapOf(SCALE_KEY to listOf(Stat.STRENGTH, Stat.CONSTITUTION))
        ),
        Question(
            id = "q8",
            title = "How often do you seek new knowledge quests?",
            kind = QuestionKind.Scale,
            options = listOf(
                Option("1", "Rarely"),
                Option("2", "Occasionally"),
                Option("3", "Constantly")
            ),
            statWeights = mapOf(SCALE_KEY to listOf(Stat.INTELLIGENCE, Stat.WISDOM))
        ),
        Question(
            id = "q9",
            title = "How much do you invest in your social squad?",
            kind = QuestionKind.Scale,
            options = listOf(
                Option("1", "Keeping it low"),
                Option("2", "Balancing"),
                Option("3", "All-in connector")
            ),
            statWeights = mapOf(SCALE_KEY to listOf(Stat.CHARISMA, Stat.DEXTERITY))
        )
    )
}

interface UserIdProvider {
    fun getUserId(): String
    fun setUserId(value: String)
}
