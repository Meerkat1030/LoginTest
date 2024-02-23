package com.example.logintest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    private lateinit var buttonEditProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        textViewWelcomeMessage = findViewById(R.id.textViewWelcomeMessage)
        textViewUsername = findViewById(R.id.textViewUsername)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonLogout = findViewById(R.id.buttonLogout)
        imageViewProfile = findViewById(R.id.imageViewProfile)
        buttonEditProfile = findViewById(R.id.buttonEditProfile)

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        userRepository = UserRepository(this)
//        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

        val welcomeMessage = "어서오세요! 홈 화면입니다."
        textViewWelcomeMessage.text = welcomeMessage

        checkLoginStatus()

        buttonLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonLogout.setOnClickListener {
            logout()
        }

        buttonEditProfile.setOnClickListener {
            // 프로필 수정 액티비티로 이동하는 인텐트 추가
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLoginStatus() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val username = sharedPreferences.getString("username", "")
            Log.d("고침된 user Id", "$username")
            val userId = sharedPreferences.getString("userId", "")
            textViewUsername.text = "닉네임: $username"
            textViewUsername.visibility = TextView.VISIBLE
            buttonLogin.visibility = Button.GONE
            buttonLogout.visibility = Button.VISIBLE
            buttonEditProfile.visibility = Button.VISIBLE
            GlobalScope.launch(Dispatchers.IO) {
                if (!userId.isNullOrEmpty()) {
                    val profileUriString = userRepository.getProfileImageUri(userId)
                    if (!profileUriString.isNullOrEmpty()) {
                        val profileUri = Uri.parse(profileUriString)
                        imageViewProfile.post {
                            imageViewProfile.setImageURI(profileUri)
                            imageViewProfile.visibility = ImageView.VISIBLE
                        }
                    } else {
                        // 프로필 이미지 URI가 없는 경우 이미지 뷰를 숨깁니다.
                        imageViewProfile.post {
                            imageViewProfile.visibility = ImageView.GONE
                        }
                    }
                } else {
                    // 사용자 ID가 없는 경우 처리
                    Log.e("HomeActivity", "User ID is null or empty")
                }
            }

        } else {
            textViewUsername.visibility = TextView.GONE
            buttonLogin.visibility = Button.VISIBLE
            buttonLogout.visibility = Button.GONE
            buttonEditProfile.visibility = Button.GONE // 로그아웃 상태에서는 프로필 수정 버튼을 숨깁니다.
            imageViewProfile.visibility = ImageView.GONE
        }
    }

    private fun logout() {
        sharedPreferences.edit().remove("isLoggedIn").apply()
        sharedPreferences.edit().remove("username").apply()
        sharedPreferences.edit().remove("userId").apply()
        finish() // 액티비티 종료
    }

    override fun onDestroy() {
        super.onDestroy()
        logout()
    }

    override fun onResume() {
        super.onResume()
        // 홈 화면을 새로 고침하는 작업을 수행한다.
        Log.d("새로고침", "한다아아아아앙ㅅ")

        checkLoginStatus()
    }

}
