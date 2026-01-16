package com.example.tugasakhir_145.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    @Query("SELECT COUNT(*) FROM users WHERE role = 'admin'")
    suspend fun getAdminCount(): Int
    
    @Query("DELETE FROM users WHERE role = 'admin'")
    suspend fun deleteAllAdmins()
}
