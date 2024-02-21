package com.example.logintest

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.logintest.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRePassword: EditText
    private lateinit var editTextNick: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var imageViewProfile: ImageView

    private var profileImageUri: String? = null

    private val SELECT_IMAGE_REQUEST = 1
    private var checkId: Boolean = false
    private var checkNick: Boolean = false
    private val REQUEST_PERMISSION_CODE = 1001
    private val PERMISSION_REQUEST_CODE = 1001

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_main)

        userRepository = UserRepository(this)

        editTextId = findViewById(R.id.editTextId_Reg)
        editTextPassword = findViewById(R.id.editTextPass_Reg)
        editTextRePassword = findViewById(R.id.editTextRePass_Reg)
        editTextNick = findViewById(R.id.editTextNick_Reg)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        imageViewProfile = findViewById(R.id.imageViewProfile)
        val btnRegister = findViewById<Button>(R.id.btnRegister_Reg)
        val btnCheckId = findViewById<Button>(R.id.btnCheckId_Reg)
        val btnCheckNick = findViewById<Button>(R.id.btnCheckNick_Reg)


        btnSelectImage.setOnClickListener {
            Log.d("으어어어어엉", "으어어어어어어어엉어ㅓㅇ")
            checkPermissionAndOpenImagePicker()
        }

        btnCheckId.setOnClickListener {
            val user = editTextId.text.toString()
            val idPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{6,15}$"

            if (user == "") {
                Toast.makeText(this@RegisterActivity, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if (Pattern.matches(idPattern, user)) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val checkUsername = userRepository.getUserById(user)
                        if (checkUsername == null) {
                            checkId = true
                            runOnUiThread {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "사용 가능한 아이디입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "이미 존재하는 아이디입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "아이디 형식이 옳지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCheckNick.setOnClickListener {
            val nick = editTextNick.text.toString()
            val nickPattern = "^[ㄱ-ㅣ가-힣a-zA-Z0-9]{2,20}$"

            if (nick == "") {
                Toast.makeText(this@RegisterActivity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if (Pattern.matches(nickPattern, nick)) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val checkNick = userRepository.getUserByNick(nick)
                        if (checkNick == null) {
                            this@RegisterActivity.checkNick = true
                            runOnUiThread {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "사용 가능한 닉네임입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "이미 존재하는 닉네임입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "닉네임 형식이 옳지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

//        btnSelectImage.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
//        }

        btnRegister.setOnClickListener {
            val user = editTextId.text.toString()
            val pass = editTextPassword.text.toString()
            val repass = editTextRePassword.text.toString()
            val nick = editTextNick.text.toString()

            if (user == "" || pass == "" || repass == "" || nick == "" || profileImageUri == "") {
                Toast.makeText(
                    this@RegisterActivity,
                    "회원정보를 모두 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (checkId) {
                    val pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,15}$"
                    if (Pattern.matches(pwPattern, pass)) {
                        if (pass == repass) {
                            if (checkNick) {
                                GlobalScope.launch(Dispatchers.IO) {
                                    userRepository.insertUser(user, pass, nick, profileImageUri!!)
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "가입되었습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(applicationContext, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "닉네임 중복확인을 해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "비밀번호가 일치하지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "비밀번호 형식이 옳지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "아이디 중복확인을 해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            val inputStream = contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageViewProfile.setImageBitmap(bitmap)
            profileImageUri = selectedImageUri.toString()
        } else {
            Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkPermissionAndOpenImagePicker() {


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Log.d("dddjdfkjas;lkfjsadkl", "dsajkhksdjaghaslkdj")
                AlertDialog.Builder(this)
                    .setTitle("권한 요청")
                    .setMessage("사진을 선택하려면 외부 저장소 권한이 필요합니다.")
                    .setPositiveButton("확인") { dialog, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_CODE
                        )
                        dialog.dismiss()
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
                //requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        } else {
            openImagePicker()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                openImagePicker()
            } else {
                Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_IMAGE_REQUEST)
    }
}
