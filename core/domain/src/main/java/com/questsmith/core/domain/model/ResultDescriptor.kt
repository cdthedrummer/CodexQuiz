package com.questsmith.core.domain.model

object ResultDescriptor {
    private val blurbs = mapOf(
        Stat.STRENGTH to ResultBlurb(
            title = "Forge Your Power",
            description = "You thrive on physical challenges and disciplined routines. Channel that drive into structured training cycles and bold adventures.",
            starters = listOf("Book a calisthenics class", "Join a community hike", "Schedule a strength assessment")
        ),
        Stat.INTELLIGENCE to ResultBlurb(
            title = "Strategist Mind",
            description = "Curiosity and analysis fuel your progress. Lean into brainy quests that stretch your thinking and keep learning varied.",
            starters = listOf("Draft a new learning sprint", "Pick up a programming mini-course", "Host a study circle")
        ),
        Stat.WISDOM to ResultBlurb(
            title = "Inner Compass",
            description = "Reflection and calm insight guide your decisions. Build rituals that protect your focus and nurture long-view planning.",
            starters = listOf("Block a weekly mindfulness session", "Start a reflective journal", "Schedule a nature reset")
        ),
        Stat.DEXTERITY to ResultBlurb(
            title = "Agile Adventurer",
            description = "You move fast and learn by doing. Seek playful challenges that sharpen coordination and spontaneity.",
            starters = listOf("Join a pickup game", "Try a maker workshop", "Experiment with a new sport")
        ),
        Stat.CHARISMA to ResultBlurb(
            title = "Connector Spark",
            description = "Your energy lifts teams and communities. Invest in group quests that let you facilitate, celebrate, and mentor others.",
            starters = listOf("Organize a meetup", "Volunteer for a team lead role", "Plan a creative jam session")
        ),
        Stat.CONSTITUTION to ResultBlurb(
            title = "Resilience Anchor",
            description = "You build sustainable habits that keep the party marching. Double down on recovery, preventive care, and grounded routines.",
            starters = listOf("Book a wellness check-in", "Refresh your sleep setup", "Design a balanced meal prep")
        )
    )

    fun describe(topStats: List<Stat>): List<ResultBlurb> = topStats.mapNotNull { blurbs[it] }
}

data class ResultBlurb(
    val title: String,
    val description: String,
    val starters: List<String>
)
