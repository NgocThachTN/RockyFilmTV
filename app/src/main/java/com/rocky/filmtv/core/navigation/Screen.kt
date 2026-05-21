package com.rocky.filmtv.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    
    object Detail : Screen("detail/{slug}") {
        fun createRoute(slug: String): String = "detail/$slug"
    }
    
    object Search : Screen("search")
    
    object Player : Screen("player/{movieSlug}/{episodeIndex}") {
        fun createRoute(movieSlug: String, episodeIndex: Int): String = "player/$movieSlug/$episodeIndex"
    }
    
    object Category : Screen("category/{categoryType}/{type}/{title}") {
        fun createRoute(categoryType: String, type: String, title: String): String = "category/$categoryType/$type/$title"
    }
    
    object Favorite : Screen("favorite")
    
    object History : Screen("history")

    object GenreList : Screen("genre_list")
}
