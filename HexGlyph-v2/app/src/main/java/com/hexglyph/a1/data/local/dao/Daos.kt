package com.hexglyph.a1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hexglyph.a1.data.local.entity.AnalyticsEntity
import com.hexglyph.a1.data.local.entity.DecodeHistoryEntity
import com.hexglyph.a1.data.local.entity.EncodeHistoryEntity
import com.hexglyph.a1.data.local.entity.ErrorLogEntity
import com.hexglyph.a1.data.local.entity.ExportHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EncodeHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EncodeHistoryEntity): Long

    @Query("SELECT * FROM encode_history ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<EncodeHistoryEntity>>

    @Query("DELETE FROM encode_history")
    suspend fun deleteAll()
}

@Dao
interface DecodeHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DecodeHistoryEntity): Long

    @Query("SELECT * FROM decode_history ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<DecodeHistoryEntity>>

    @Query("DELETE FROM decode_history")
    suspend fun deleteAll()
}

@Dao
interface ExportHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ExportHistoryEntity): Long

    @Query("SELECT * FROM export_history ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<ExportHistoryEntity>>

    @Query("DELETE FROM export_history")
    suspend fun deleteAll()
}

@Dao
interface AnalyticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AnalyticsEntity): Long

    @Query("SELECT * FROM analytics ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 100): Flow<List<AnalyticsEntity>>

    @Query("SELECT AVG(durationMs) FROM analytics WHERE eventType = :type")
    suspend fun averageDuration(type: String): Double?

    @Query("DELETE FROM analytics")
    suspend fun deleteAll()
}

@Dao
interface ErrorLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ErrorLogEntity): Long

    @Query("SELECT * FROM error_log ORDER BY timestamp DESC LIMIT 200")
    fun observeRecent(): Flow<List<ErrorLogEntity>>

    @Query("DELETE FROM error_log")
    suspend fun deleteAll()
}
