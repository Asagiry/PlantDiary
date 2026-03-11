package com.asagiry.plantdiary.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val bottomBarInterpolator = FastOutSlowInInterpolator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val topLevelDestinations = setOf(
            R.id.plantsFragment,
            R.id.careRecordsFragment,
            R.id.scheduleFragment,
            R.id.helpFragment,
        )
        val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            setBottomBarVisibility(destination.id in topLevelDestinations)
        }
    }

    private fun setBottomBarVisibility(visible: Boolean) {
        val bottomBar = binding.bottomNavigation
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
}
