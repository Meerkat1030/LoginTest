package com.example.logintest.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.logintest.Entity.User

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE nick = :nick")
    fun getUserByNick(nick: String): User?

    @Query("SELECT * FROM users WHERE id = :id AND password = :password")
    fun getUserByIdAndPassword(id: String, password: String): User?

    @Query("SELECT profile_image_uri FROM users WHERE id = :id")
    fun getProfileImageUri(id: String): String?

    @Insert
    fun insertUser(user: User)
}
