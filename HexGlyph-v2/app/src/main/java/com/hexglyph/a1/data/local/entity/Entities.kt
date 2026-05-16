package com.hexglyph.a1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encode_history")
data class EncodeHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp:    Long,
    val imageUri:     String,
    val payloadSize:  Int,
    val isEncrypted:  Boolean,
    val exportedPath: String?
)

@Entity(tableName = "decode_history")
data class DecodeHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp:   Long,
    val imageUri:    String,
    val payloadSize: Int,
    val success:     Boolean,
    val errorMsg:    String?
)

@Entity(tableName = "export_history")
data class ExportHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp:  Long,
    val exportUri:  String,
    val fileName:   String,
    val formatMime: String
)

@Entity(tableName = "analytics")
data class AnalyticsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp:   Long,
    val eventType:   String,
    val durationMs:  Long,
    val payloadSize: Int,
    val imageWidth:  Int,
    val imageHeight: Int
)

@Entity(tableName = "error_log")
data class ErrorLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val tag:       String,
    val message:   String,
    val stackTrace: String?
)
