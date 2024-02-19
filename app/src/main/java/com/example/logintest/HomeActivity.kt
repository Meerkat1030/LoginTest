package com.example.logintest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var textViewWelcomeMessage: TextView
    private lateinit var textViewUsername: TextView
    private lateinit var buttonLogin: Button
    private lateinit var buttonLogout: Button
    private lateinit var imageViewProfile: ImageView // 이미지뷰 추가
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        textViewWelcomeMessage = findViewById(R.id.textViewWelcomeMessage)
        textViewUsername = findViewById(R.id.textViewUsername)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonLogout = findViewById(R.id.buttonLogout)
        imageViewProfile = findViewById(R.id.imageViewProfile)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        // 환영 메시지 설정
        val welcomeMessage = "어서오세요! 홈 화면입니다."
        textViewWelcomeMessage.text = welcomeMessage

        // 로그인 상태 확인 및 UI 갱신
        checkLoginStatus()

        // 로그인 버튼 클릭 이벤트 처리
        buttonLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼 클릭 이벤트 처리
        buttonLogout.setOnClickListener {
            // SharedPreferences에서 로그인 정보 제거
            sharedPreferences.edit().remove("isLoggedIn").apply()
            sharedPreferences.edit().remove("username").apply()
            // 사용자가 로그아웃하면 다시 체크하여 UI 갱신
            checkLoginStatus()
        }
    }

    // 로그인 상태 확인 및 UI 갱신
    private fun checkLoginStatus() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            // 로그인된 경우 닉네임을 가져와서 표시하고, 로그인 버튼을 감추고 로그아웃 버튼을 표시합니다.
            val username = sharedPreferences.getString("username", "")
            textViewUsername.text = "닉네임: $username"
            textViewUsername.visibility = TextView.VISIBLE
            buttonLogin.visibility = Button.GONE
            buttonLogout.visibility = Button.VISIBLE
//            imageViewProfile.visibility = ImageView.VISIBLE // 이미지뷰 표시
            // 프로필 사진 설정
            val profileUriString = sharedPreferences.getString("profileUri", "")
            if (!profileUriString.isNullOrEmpty()) {
                val profileUri = Uri.parse(profileUriString)
                imageViewProfile.setImageURI(profileUri)
                imageViewProfile.visibility = ImageView.VISIBLE
            } else {
                imageViewProfile.visibility = ImageView.GONE
            }
        } else {
            // 로그인되지 않은 경우 닉네임을 숨기고, 로그인 버튼을 표시하고 로그아웃 버튼을 감춥니다.
            textViewUsername.visibility = TextView.GONE
            buttonLogin.visibility = Button.VISIBLE
            buttonLogout.visibility = Button.GONE
            imageViewProfile.visibility = ImageView.GONE // 이미지뷰 숨김
        }
    }
}
