package com.example.logintest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.logintest.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var textViewWelcomeMessage: TextView
    private lateinit var textViewUsername: TextView
    private lateinit var buttonLogin: Button
    private lateinit var buttonLogout: Button
    private lateinit var imageViewProfile: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        textViewWelcomeMessage = findViewById(R.id.textViewWelcomeMessage)
        textViewUsername = findViewById(R.id.textViewUsername)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonLogout = findViewById(R.id.buttonLogout)
        imageViewProfile = findViewById(R.id.imageViewProfile)

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        userRepository = UserRepository(this)

        val welcomeMessage = "어서오세요! 홈 화면입니다."
        textViewWelcomeMessage.text = welcomeMessage

        checkLoginStatus()

        buttonLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonLogout.setOnClickListener {
            sharedPreferences.edit().remove("isLoggedIn").apply()
            sharedPreferences.edit().remove("username").apply()
            checkLoginStatus()
        }
    }

    private fun checkLoginStatus() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val username = sharedPreferences.getString("username", "")
            textViewUsername.text = "닉네임: $username"
            textViewUsername.visibility = TextView.VISIBLE
            buttonLogin.visibility = Button.GONE
            buttonLogout.visibility = Button.VISIBLE
            GlobalScope.launch(Dispatchers.IO) {
                val profileUriString = userRepository.getProfileImageUri(username!!)
                launch(Dispatchers.Main) {
                    if (!profileUriString.isNullOrEmpty()) {
                        val profileUri = Uri.parse(profileUriString)
                        imageViewProfile.setImageURI(profileUri)
                        imageViewProfile.visibility = ImageView.VISIBLE
                    } else {
                        // 프로필 이미지 URI가 없는 경우 이미지 뷰를 숨깁니다.
                        imageViewProfile.visibility = ImageView.GONE
                    }
                }
            }


        } else {
            textViewUsername.visibility = TextView.GONE
            buttonLogin.visibility = Button.VISIBLE
            buttonLogout.visibility = Button.GONE
            imageViewProfile.visibility = ImageView.GONE
        }
    }
}
