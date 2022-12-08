package com.sounekatlogo.sounekat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.sounekatlogo.sounekat.databinding.ActivityMainBinding
import com.sounekatlogo.sounekat.ui.HomepageActivity

class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(R.drawable.logo_apps)
            .into(binding.logo)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomepageActivity::class.java))
            finish()
        }, 3000)



    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}