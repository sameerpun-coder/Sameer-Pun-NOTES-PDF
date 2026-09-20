package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotePdfDao {
    @Query("SELECT * FROM notes_pdf ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NotePdf>>

    @Query("SELECT * FROM notes_pdf WHERE subject = :subject ORDER BY createdAt DESC")
    fun getNotesBySubject(subject: String): Flow<List<NotePdf>>

    @Query("SELECT * FROM notes_pdf WHERE subject = :subject AND unit = :unit ORDER BY createdAt DESC")
    fun getNotesBySubjectAndUnit(subject: String, unit: String): Flow<List<NotePdf>>

    @Query("""
        SELECT * FROM notes_pdf 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
           OR unit LIKE '%' || :query || '%'
           OR tags LIKE '%' || :query || '%'
           OR subject LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchNotes(query: String): Flow<List<NotePdf>>

    @Query("SELECT * FROM notes_pdf WHERE id = :id")
    fun getNoteById(id: Long): Flow<NotePdf?>

    @Query("SELECT * FROM notes_pdf WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteNotes(): Flow<List<NotePdf>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotePdf): Long

    @Update
    suspend fun updateNote(note: NotePdf)

    @Delete
    suspend fun deleteNote(note: NotePdf)

    @Query("DELETE FROM notes_pdf WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("UPDATE notes_pdf SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM notes_pdf")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes_pdf WHERE subject = :subject")
    fun getCountBySubject(subject: String): Flow<Int>
}
