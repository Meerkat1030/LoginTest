package com.example.logintest

import DBHelper
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var btnLogin: Button
    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnRegister: Button
    private var DB: DBHelper? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DB = DBHelper(this)
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        btnLogin = findViewById(R.id.btnLogin)
        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)
        btnRegister = findViewById(R.id.btnRegister)

        // 로그인 버튼 클릭
        btnLogin.setOnClickListener {
            val user = editTextId.text.toString()
            val pass = editTextPassword.text.toString()

            if (user == "" || pass == "") {
                Toast.makeText(this@MainActivity, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val checkUserpass = DB!!.checkUserpass(user, pass)
                // id 와 password 일치시
                if (checkUserpass == true) {
                    // 로그인 상태를 SharedPreferences에 저장
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                    sharedPreferences.edit().putString("username", user).apply()
// 프로필 사진을 데이터베이스에서 가져와서 URI로 저장
                    val profileImageUri = DB!!.getProfileImageUri(user)
                    sharedPreferences.edit().putString("profileImageUri", profileImageUri).apply()

                    Toast.makeText(this@MainActivity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 회원가입 버튼 클릭시
        btnRegister.setOnClickListener {
            val loginIntent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(loginIntent)
        }
    }
}
