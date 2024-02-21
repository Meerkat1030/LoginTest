package com.example.logintest.Repository

import android.content.Context
import androidx.room.Room
import com.example.logintest.Database.AppDatabase
import com.example.logintest.Entity.User

class UserRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "Login.db"
    ).build()

    private val userDao = db.userDao()

    suspend fun insertUser(id: String, password: String, nick: String, profileImageUri: String) {
        val user = User(id, password, nick, profileImageUri)
        userDao.insertUser(user)
    }

    suspend fun getUserById(id: String): User? {
        return userDao.getUserById(id)
    }

    suspend fun getUserByNick(nick: String): User? {
        return userDao.getUserByNick(nick)
    }

    suspend fun getUserByIdAndPassword(id: String, password: String): User? {
        return userDao.getUserByIdAndPassword(id, password)
    }

    suspend fun getProfileImageUri(username: String): String? {
        return userDao.getProfileImageUri(username)
    }
}
