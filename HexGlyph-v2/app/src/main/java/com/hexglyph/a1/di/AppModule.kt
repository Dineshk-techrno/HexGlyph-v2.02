package com.hexglyph.a1.di

import android.content.Context
import androidx.room.Room
import com.hexglyph.a1.data.local.dao.AnalyticsDao
import com.hexglyph.a1.data.local.dao.DecodeHistoryDao
import com.hexglyph.a1.data.local.dao.EncodeHistoryDao
import com.hexglyph.a1.data.local.dao.ErrorLogDao
import com.hexglyph.a1.data.local.dao.ExportHistoryDao
import com.hexglyph.a1.data.local.database.HexGlyphDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HexGlyphDatabase =
        Room.databaseBuilder(
            context,
            HexGlyphDatabase::class.java,
            HexGlyphDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()

    @Provides @Singleton fun provideEncodeHistoryDao(db: HexGlyphDatabase): EncodeHistoryDao = db.encodeHistoryDao()
    @Provides @Singleton fun provideDecodeHistoryDao(db: HexGlyphDatabase): DecodeHistoryDao = db.decodeHistoryDao()
    @Provides @Singleton fun provideExportHistoryDao(db: HexGlyphDatabase): ExportHistoryDao = db.exportHistoryDao()
    @Provides @Singleton fun provideAnalyticsDao(db: HexGlyphDatabase):     AnalyticsDao     = db.analyticsDao()
    @Provides @Singleton fun provideErrorLogDao(db: HexGlyphDatabase):      ErrorLogDao      = db.errorLogDao()
}
