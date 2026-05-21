package com.rocky.filmtv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rocky.filmtv.core.navigation.Screen
import com.rocky.filmtv.core.theme.RockyFilmTVTheme
import com.rocky.filmtv.ui.category.CategoryScreen
import com.rocky.filmtv.ui.detail.DetailScreen
import com.rocky.filmtv.ui.favorite.FavoriteScreen
import com.rocky.filmtv.ui.history.HistoryScreen
import com.rocky.filmtv.ui.home.HomeScreen
import com.rocky.filmtv.ui.player.PlayerScreen
import com.rocky.filmtv.ui.search.SearchScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RockyFilmTVTheme {
                RockyFilmTVAppNavigation()
            }
        }
    }
}

@Composable
fun RockyFilmTVAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Home Dashboard
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { slug ->
                    navController.navigate(Screen.Detail.createRoute(slug))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToFavorite = {
                    navController.navigate(Screen.Favorite.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToCategory = { type, title ->
                    navController.navigate(Screen.Category.createRoute(type, title))
                }
            )
        }

        // 2. Movie Details
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("slug") { type = NavType.StringType })
        ) { backStackEntry ->
            val slug = backStackEntry.arguments?.getString("slug") ?: ""
            DetailScreen(
                slug = slug,
                onNavigateToPlayer = { movieSlug, episodeIndex ->
                    navController.navigate(Screen.Player.createRoute(movieSlug, episodeIndex))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        // 3. TV-Safe Search Screen
        composable(route = Screen.Search.route) {
            SearchScreen(
                onNavigateToDetail = { slug ->
                    navController.navigate(Screen.Detail.createRoute(slug))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        // 4. HLS ExoPlayer Streaming Screen
        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("movieSlug") { type = NavType.StringType },
                navArgument("episodeIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieSlug = backStackEntry.arguments?.getString("movieSlug") ?: ""
            val episodeIndex = backStackEntry.arguments?.getInt("episodeIndex") ?: 0
            
            PlayerScreen(
                movieSlug = movieSlug,
                episodeIndex = episodeIndex,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onPlayEpisode = { nextSlug, nextIdx ->
                    // Re-route to same route but with updated episode index to force refresh playback
                    navController.navigate(Screen.Player.createRoute(nextSlug, nextIdx)) {
                        popUpTo(Screen.Player.route) { inclusive = true }
                    }
                }
            )
        }

        // 5. Category Browser (Browse list)
        composable(
            route = Screen.Category.route,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            
            CategoryScreen(
                type = type,
                title = title,
                onNavigateToDetail = { slug ->
                    navController.navigate(Screen.Detail.createRoute(slug))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        // 6. Local Favorites Screen
        composable(route = Screen.Favorite.route) {
            FavoriteScreen(
                onNavigateToDetail = { slug ->
                    navController.navigate(Screen.Detail.createRoute(slug))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        // 7. Watch History Screen
        composable(route = Screen.History.route) {
            HistoryScreen(
                onNavigateToDetail = { slug ->
                    navController.navigate(Screen.Detail.createRoute(slug))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}
