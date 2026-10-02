package com.NqXkLmR.vJpTzF

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.NqXkLmR.vJpTzF.core.di.ServiceLocator
import com.NqXkLmR.vJpTzF.core.navigation.Navigator
import com.NqXkLmR.vJpTzF.databinding.ActivityMainBinding
import com.NqXkLmR.vJpTzF.presentation.splash.SplashFragment

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        val inflated = ActivityMainBinding.inflate(layoutInflater)
        binding = inflated
        setContentView(inflated.root)
        if (savedInstanceState == null) {
            Navigator(supportFragmentManager, R.id.fragment_container)
                .setRoot(SplashFragment(), SplashFragment.TAG)
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
