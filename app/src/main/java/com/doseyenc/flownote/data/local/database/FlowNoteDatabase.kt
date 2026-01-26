package com.doseyenc.flownote.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.doseyenc.flownote.data.local.dao.TaskDao
import com.doseyenc.flownote.data.model.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlowNoteDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
