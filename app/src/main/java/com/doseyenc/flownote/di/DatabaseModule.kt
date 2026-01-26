package com.doseyenc.flownote.di

import android.content.Context
import androidx.room.Room
import com.doseyenc.flownote.data.local.dao.TaskDao
import com.doseyenc.flownote.data.local.database.FlowNoteDatabase
import com.doseyenc.flownote.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FlowNoteDatabase {
        return Room.databaseBuilder(
            context,
            FlowNoteDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideTaskDao(database: FlowNoteDatabase): TaskDao {
        return database.taskDao()
    }
}
