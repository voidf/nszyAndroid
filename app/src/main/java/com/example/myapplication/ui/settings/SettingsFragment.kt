package com.example.myapplication.ui.settings

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.*
import com.example.myapplication.BackgroundWorker
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSettingsBinding
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroyView() {
        super.onDestroyView()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getFrontActivity())
        val dw = sharedPreferences.getBoolean("notifications", false)
        Log.d("[worker]dest", "$dw")
        if(dw)
        {
            Toast.makeText(MainActivity.getFrontActivity()!!.applicationContext, "推送启动...", Toast.LENGTH_SHORT).show()
            val c = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setTriggerContentMaxDelay(1000*1, TimeUnit.MILLISECONDS)
                .build()
            val wk = PeriodicWorkRequestBuilder<BackgroundWorker>(
                1000*60*15,
                TimeUnit.MILLISECONDS
            ).setConstraints(c).build()

            val wm = WorkManager.getInstance(MainActivity.getFrontActivity()!!.applicationContext)
            wm.cancelAllWork()
            Log.d("[WorkerManager]", "${wm.enqueue(wk)}")
            wm.enqueue(
                OneTimeWorkRequest.from(
                    BackgroundWorker::class.java
                )
            )


        }
    }

//    @RequiresApi(Build.VERSION_CODES.N)
//    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
//        Log.d("[worker]", "$key triggered!")
//
//        when(key){
//            "notifications" -> {
//                val dw = sharedPreferences!!.getBoolean(key, false)
//                if(dw)
//                {
//                    Toast.makeText(this.context, "推送启动...", Toast.LENGTH_SHORT).show()
//                    val c = Constraints.Builder()
//                        .setRequiredNetworkType(NetworkType.CONNECTED)
//                        .setRequiresBatteryNotLow(true)
//                        .setTriggerContentMaxDelay(1000*1, TimeUnit.MILLISECONDS)
//                        .build()
//                    val wk = PeriodicWorkRequestBuilder<BackgroundWorker>(
//                        1000*60*15,
//                        TimeUnit.MILLISECONDS
//                    ).setConstraints(c).build()
//
//                    val wm = WorkManager.getInstance(this.requireContext())
//                    wm.cancelAllWork()
//                    wm.enqueue(wk)
//                    wm.enqueue(
//                        OneTimeWorkRequest.from(
//                            BackgroundWorker::class.java
//                        )
//                    )
//
//                }
//            }
//        }
//    }


//    override fun onCreateView(
//            inflater: LayoutInflater,
//            container: ViewGroup?,
//            savedInstanceState: Bundle?
//    ): View? {
//        settingsViewModel =
//                ViewModelProvider(this).get(SettingsViewModel::class.java)
//
//        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        val textView: TextView = binding.textSettings
//        settingsViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}