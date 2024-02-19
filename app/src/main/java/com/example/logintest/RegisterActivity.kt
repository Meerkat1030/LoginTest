package com.example.logintest

import DBHelper
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private var DB:DBHelper?=null
    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRePassword: EditText
    private lateinit var editTextNick: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var imageViewProfile: ImageView

    //    lateinit var editTextPhone: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnCheckId: Button
    private var CheckId:Boolean=false
    private lateinit var btnCheckNick: Button
    private var CheckNick:Boolean=false
    private val SELECT_IMAGE_REQUEST = 1
    private var profileImageUri: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_main)

        DB = DBHelper(this)
        editTextId = findViewById(R.id.editTextId_Reg)
        editTextPassword = findViewById(R.id.editTextPass_Reg)
        editTextRePassword = findViewById(R.id.editTextRePass_Reg)
        editTextNick = findViewById(R.id.editTextNick_Reg)
//        editTextPhone = findViewById(R.id.editTextPhone_Reg)
        btnRegister = findViewById(R.id.btnRegister_Reg)
        btnCheckId = findViewById(R.id.btnCheckId_Reg)
        btnCheckNick = findViewById(R.id.btnCheckNick_Reg)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        imageViewProfile = findViewById(R.id.imageViewProfile)

        // 아이디 중복확인
        btnCheckId.setOnClickListener {
            val user = editTextId.text.toString()
            val idPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{6,15}$"

            if (user == "") {
                Toast.makeText(
                    this@RegisterActivity,
                    "아이디를 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                if (Pattern.matches(idPattern, user)) {
                    val checkUsername = DB!!.checkUser(user)
                    if(!checkUsername){
                        CheckId = true
                        Toast.makeText(this@RegisterActivity, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this@RegisterActivity, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this@RegisterActivity, "아이디 형식이 옳지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 닉네임 중복확인
        btnCheckNick.setOnClickListener {
            val nick = editTextNick.text.toString()
            val nickPattern = "^[ㄱ-ㅣ가-힣a-zA-Z0-9]{2,20}$"

            if (nick == "") {
                Toast.makeText(
                    this@RegisterActivity,
                    "닉네임을 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                if (Pattern.matches(nickPattern, nick)) {
                    val checkNick = DB!!.checkNick(nick)
                    if(!checkNick){
                        CheckNick = true
                        Toast.makeText(this@RegisterActivity, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this@RegisterActivity, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this@RegisterActivity, "닉네임 형식이 옳지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 프로필 사진 선택
        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
        }

        // 완료 버튼 클릭 시
        btnRegister.setOnClickListener {
            val user = editTextId.text.toString()
            val pass = editTextPassword.text.toString()
            val repass = editTextRePassword.text.toString()
            val nick = editTextNick.text.toString()

//            val phone = editTextPhone.text.toString()
            val pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,15}$"
//            val phonePattern = "^(\\+[0-9]+)?[0-9]{10,15}$"
            // 사용자 입력이 비었을 때
            if (user == "" || pass == "" || repass == "" || nick == "" || profileImageUri == "") Toast.makeText(
                this@RegisterActivity,
                "회원정보를 모두 입력해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            else {
                // 아이디 중복 확인이 됐을 때
                if (CheckId) {
                    // 비밀번호 형식이 맞을 때
                    if (Pattern.matches(pwPattern, pass)) {
                        // 비밀번호 재확인 성공
                        if (pass == repass) {
                            // 닉네임 중복확인
                            if (CheckNick) {
                                // 번호 형식

                                val insert = DB!!.insertData(user, pass, nick, profileImageUri)
                                // 가입 성공 시 Toast를 띄우고 메인 화면으로 전환
                                if (insert == true) {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "가입되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                // 가입 실패 시
                                else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "가입 실패하였습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                // else {
//                                    Toast.makeText(
//                                        this@RegisterActivity,
//                                        "전화번호 형식이 옳지 않습니다.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
                            }
                            // 닉네임 중복확인 하지 않았을 때
                            else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "닉네임 중복확인을 해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        // 비밀번호 재확인 실패
                        else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "비밀번호가 일치하지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    // 비밀번호 형식이 맞지 않을 때
                    else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "비밀번호 형식이 옳지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                // 아이디 중복확인이 되지 않았을 때
                else {
                    Toast.makeText(this@RegisterActivity, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // 이미지를 선택한 후 처리할 코드
            val selectedImageUri = data.data
            val inputStream = contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageViewProfile.setImageBitmap(bitmap)
            profileImageUri = imageViewProfile.tag as String? // 프로필 이미지 URI 가져오기 // 이미지 URI 저장
        } else {
            Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
