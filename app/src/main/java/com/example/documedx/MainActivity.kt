package com.example.documedx

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.documedx.databinding.ActivityMainBinding
import com.example.documedx.databinding.BasicHealthInfoGatheringActivityBinding
import com.example.documedx.databinding.SignUpForStaffActivityBinding
import com.example.documedx.databinding.SignUpPageActiviyBinding

class MainActivity : AppCompatActivity() {
    //Bind For Sign up Page
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Setting the Sign up Page as default
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }


}