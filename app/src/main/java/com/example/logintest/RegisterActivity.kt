package com.example.logintest

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
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
    private val PERMISSION_REQUEST_CODE = 1001

    private val readStorageResultLancher : ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {isGranted ->
            if(isGranted) {
                Toast.makeText(this, "저장소 접근 퍼미션 허용", Toast.LENGTH_LONG).show()
                openImagePicker()
            } else {
                Toast.makeText(this, "저장소 접근 퍼미션 거부", Toast.LENGTH_LONG).show()
            }

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
            // 퍼미션 확인
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                // API 23이상이고, 이전에 퍼미션을 거부했을 경우
                showRationaleDialog("이미지 파일 접근", "권한이 거부되어 접근할 수 없습니다.")
            } else {
                // API 23 미만이거나 최초로 퍼미션 요청을 받았을 경우
                readStorageResultLancher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }

        btnCheckId.setOnClickListener {
            val user = editTextId.text.toString()
            val idPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{6,15}$"

            if (user == "") {
                Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
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

            // URI를 파일 경로로 변환하여 저장
            val filePath = getRealPathFromURI(selectedImageUri)
            if (filePath != null) {
                Log.d("아아아아아아아아", "${filePath}")
                profileImageUri = filePath
            } else {
                Toast.makeText(this, "이미지 경로를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    // 특정 퍼미션이 필요한 이유를 설명하는 다이얼로그, 사용자가 이전에 퍼미션 요청을 거부했을 경우에 표시
    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // 타이틀, 메시지, 버튼 1개 설정
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("닫기") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

//    private fun checkPermissionAndOpenImagePicker() {
//        if (ContextCompat.checkSelfPermission(FilePath = File(filesDir, TEMP_IM
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//            ) {
//                AlertDialog.Builder(this)
//                    .setTitle("권한 요청")
//                    .setMessage("사진을 선택하려면 외부 저장소 권한이 필요합니다.")
//                    .setPositiveButton("확인") { dialog, _ ->
//                        ActivityCompat.requestPermissions(
//                            this,
//                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                            PERMISSION_REQUEST_CODE
//                        )
//                        dialog.dismiss()
//                    }
//                    .setNegativeButton("취소") { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                    .create()
//                    .show()
//            } else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    PERMISSION_REQUEST_CODE
//                )
//            }
//        } else {
//            openImagePicker()
//        }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
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

    private fun getRealPathFromURI(uri: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            realPath = it.getString(columnIndex)
        }
        return realPath
    }
}
