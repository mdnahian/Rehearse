package com.mdislam.rehearse

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by mdnah on 5/21/2017.
 */

class TranscriptDbHelper(context: Context) : SQLiteOpenHelper(context, TranscriptDbHelper.DATABASE_NAME, null, TranscriptDbHelper.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_ENTRIES = "CREATE TABLE " + TranscriptModel.TranscriptEntry.TABLE_NAME + " (" +
                TranscriptModel.TranscriptEntry._ID + " INTEGER PRIMARY KEY," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_OVERALL_GRADE + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_SENTENCES + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_WORDS + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_FILLER_WORDS + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_SLOPPY_LANGUAGE + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_PAUSES + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_TRANSCRIPT + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_CORRECTED + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_LOCATION + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_CREATED + " TEXT," +
                TranscriptModel.TranscriptEntry.Companion.COLUMN_NAME_DURATION + " TEXT)"


        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TranscriptModel.TranscriptEntry.TABLE_NAME
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Rehearse.db"
    }
}
