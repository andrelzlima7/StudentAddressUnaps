package com.unasp.studantaddress.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY nome ASC")
    fun findAll(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE localId = :localId")
    suspend fun findById(localId: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE synced = 0")
    suspend fun findPending(): List<StudentEntity>

    @Query("SELECT * FROM students WHERE remoteId = :remoteId LIMIT 1")
    suspend fun findByRemoteId(remoteId: Long): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity): Long

    @Update
    suspend fun update(student: StudentEntity)

    @Delete
    suspend fun delete(student: StudentEntity)

    @Query("DELETE FROM students WHERE localId = :localId")
    suspend fun deleteById(localId: Long)

    @Query("UPDATE students SET synced = 1, remoteId = :remoteId WHERE localId = :localId")
    suspend fun markAsSynced(localId: Long, remoteId: Long)
}