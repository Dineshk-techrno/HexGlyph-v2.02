package com.hexglyph.a1.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hexglyph.a1.data.local.dao.AnalyticsDao
import com.hexglyph.a1.data.local.dao.DecodeHistoryDao
import com.hexglyph.a1.data.local.dao.EncodeHistoryDao
import com.hexglyph.a1.data.local.dao.ErrorLogDao
import com.hexglyph.a1.data.local.dao.ExportHistoryDao
import com.hexglyph.a1.data.local.entity.AnalyticsEntity
import com.hexglyph.a1.data.local.entity.DecodeHistoryEntity
import com.hexglyph.a1.data.local.entity.EncodeHistoryEntity
import com.hexglyph.a1.data.local.entity.ErrorLogEntity
import com.hexglyph.a1.data.local.entity.ExportHistoryEntity

@Database(
    entities = [
        EncodeHistoryEntity::class,
        DecodeHistoryEntity::class,
        ExportHistoryEntity::class,
        AnalyticsEntity::class,
        ErrorLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class HexGlyphDatabase : RoomDatabase() {
    abstract fun encodeHistoryDao(): EncodeHistoryDao
    abstract fun decodeHistoryDao(): DecodeHistoryDao
    abstract fun exportHistoryDao(): ExportHistoryDao
    abstract fun analyticsDao():     AnalyticsDao
    abstract fun errorLogDao():      ErrorLogDao

    companion object {
        const val DATABASE_NAME = "hexglyph.db"
    }
}
