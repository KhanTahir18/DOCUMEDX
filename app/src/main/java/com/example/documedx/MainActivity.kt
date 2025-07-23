package com.example.documedx

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.documedx.databinding.ActivityMainBinding
import com.example.documedx.databinding.BasicHealthInfoGatheringActivityBinding
import com.example.documedx.databinding.SignUpPageActiviyBinding

class MainActivity : AppCompatActivity() {
    //Bind For Sign up Page
    private lateinit var bindingSignUpPage: SignUpPageActiviyBinding

    //Bind For Info Gathering Page
    private lateinit var bindingBasicHEalthInfoGatheringPage: BasicHealthInfoGatheringActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Setting the Sign up Page as default
        bindingSignUpPage = SignUpPageActiviyBinding.inflate(layoutInflater)
        setContentView(bindingSignUpPage.root)

        //Sign up Button OnclickListener
        bindingSignUpPage.signUpBtn.setOnClickListener {

            //Setting info gathering page as default
            bindingBasicHEalthInfoGatheringPage = BasicHealthInfoGatheringActivityBinding.inflate(layoutInflater)
            setContentView(bindingBasicHEalthInfoGatheringPage.root)
        }
    }


}