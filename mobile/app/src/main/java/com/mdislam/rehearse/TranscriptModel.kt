package com.mdislam.rehearse

import android.provider.BaseColumns

/**
 * Created by mdnah on 5/21/2017.
 */

class TranscriptModel {

    class TranscriptEntry : BaseColumns {
        companion object {
            val _ID = "id"
            val TABLE_NAME = "transcripts"
            val COLUMN_NAME_OVERALL_GRADE = "overall_grade"
            val COLUMN_NAME_SENTENCES = "sentences"
            val COLUMN_NAME_WORDS = "words"
            val COLUMN_NAME_FILLER_WORDS = "filler_words"
            val COLUMN_NAME_SLOPPY_LANGUAGE = "sloppy_language"
            val COLUMN_NAME_PAUSES = "pauses"
            val COLUMN_NAME_TRANSCRIPT = "transcript"
            val COLUMN_NAME_CORRECTED = "corrected"
            val COLUMN_NAME_LOCATION = "location"
            val COLUMN_NAME_CREATED = "created"
            val COLUMN_NAME_DURATION = "duration"
        }
    }

}
