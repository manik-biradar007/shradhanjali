package com.ereader.ripcardmaker.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ereader.ripcardmaker.app
import com.ereader.ripcardmaker.ui.editor.EditorScreen
import com.ereader.ripcardmaker.ui.home.HomeScreen
import com.ereader.ripcardmaker.ui.preview.PreviewScreen

private object Routes {
    const val HOME = "home"
    const val EDITOR = "editor/{id}"
    const val PREVIEW = "preview/{id}"
    fun editor(id: String) = "editor/$id"
    fun preview(id: String) = "preview/$id"
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val activity = LocalContext.current as Activity
    val ads = activity.app.ads
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onOpenCard = { id -> ads.maybeShowInterstitial(activity) { nav.navigate(Routes.editor(id)) } })
        }
        composable(Routes.EDITOR, arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            EditorScreen(
                draftId = id,
                onBack = { nav.popBackStack() },
                onPreview = { ads.maybeShowInterstitial(activity) { nav.navigate(Routes.preview(id)) } },
            )
        }
        composable(Routes.PREVIEW, arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            PreviewScreen(
                draftId = id,
                onBack = { nav.popBackStack() },
                onDone = { nav.popBackStack(Routes.HOME, inclusive = false) },
            )
        }
    }
}
