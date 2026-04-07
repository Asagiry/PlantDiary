package com.asagiry.plantdiary.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.asagiry.plantdiary.PlantDiaryApp
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.databinding.ActivityMainBinding
import com.asagiry.plantdiary.ui.common.shouldReduceMotion

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val bottomBarInterpolator = FastOutSlowInInterpolator()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val app = application as PlantDiaryApp
        val graph = navController.navInflater.inflate(R.navigation.nav_graph).apply {
            // The first screen depends only on whether the user has already picked a language.
            setStartDestination(
                if (app.hasSelectedLanguage()) {
                    R.id.plantsFragment
                } else {
                    R.id.languageOnboardingFragment
                },
            )
        }
        navController.setGraph(graph, intent.extras)
        val topLevelDestinations = setOf(
            R.id.plantsFragment,
            R.id.careRecordsFragment,
            R.id.scheduleFragment,
            R.id.helpFragment,
        )
        val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!navController.popBackStack()) {
                        finish()
                    }
                }
            },
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isOnboarding = destination.id == R.id.languageOnboardingFragment
            binding.toolbar.isVisible = !isOnboarding
            setBottomBarVisibility(destination.id in topLevelDestinations)
        }
    }

    private fun setBottomBarVisibility(visible: Boolean) {
        val bottomBar = binding.bottomNavigation
        if (bottomBar.context.shouldReduceMotion()) {
            bottomBar.animate().cancel()
            bottomBar.alpha = if (visible) 1f else 0f
            bottomBar.translationY = 0f
            bottomBar.visibility = if (visible) View.VISIBLE else View.GONE
            return
        }

        val travelDistance =
            bottomBar.height.takeIf { it > 0 }?.toFloat()?.times(0.35f)
                ?: bottomBar.resources.displayMetrics.density * 24f

        if (visible) {
            if (bottomBar.visibility == View.VISIBLE && bottomBar.alpha == 1f && bottomBar.translationY == 0f) {
                return
            }
            bottomBar.animate().cancel()
            bottomBar.visibility = View.VISIBLE
            bottomBar.alpha = 0f
            bottomBar.translationY = travelDistance
            bottomBar.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(260L)
                .setInterpolator(bottomBarInterpolator)
                .start()
            return
        }

        if (bottomBar.visibility != View.VISIBLE) {
            return
        }
        bottomBar.animate().cancel()
        bottomBar.animate()
            .alpha(0f)
            .translationY(travelDistance)
            .setDuration(220L)
            .setInterpolator(bottomBarInterpolator)
            .withEndAction {
                bottomBar.visibility = View.GONE
            }
            .start()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
