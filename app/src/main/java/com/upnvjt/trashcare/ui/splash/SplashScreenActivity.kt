package com.upnvjt.trashcare.ui.splash

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.upnvjt.trashcare.databinding.ActivitySplashScreenBinding
import com.upnvjt.trashcare.ui.auth.AuthActivity
import com.upnvjt.trashcare.ui.onboarding.OnBoardingActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        prepareLogoAnimate()
    }

    private fun prepareLogoAnimate() {
        binding.logoFlashScreen.alpha = 0f
        binding.logoText.alpha = 0f

        binding.logoText.animate().apply {
            duration = 1500
            alpha(1f)
        }
        binding.logoFlashScreen.animate().apply {
            duration = 1500
            alpha(1f)
        }.withEndAction {
            if (onBoardingFinished()) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            } else {
                val intent = Intent(this, OnBoardingActivity::class.java)
                startActivity(intent)

                finish()
            }
        }
    }
    private fun onBoardingFinished(): Boolean {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}