package com.questsmith.app.navigation

sealed class NavRoute(val route: String) {
    data object Welcome : NavRoute("welcome")
    data object Results : NavRoute("results")
    data object Quiz : NavRoute("quiz/{index}")

    companion object {
        fun quiz(index: Int) = "quiz/$index"
    }
}
