package com.mdislam.rehearse

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    internal var transcripts = ArrayList<Transcript>()
    var recordingsList: ListView? = null
    private var transcriptsAdapter: TranscriptAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recordBtn = findViewById(R.id.recordBtn) as FloatingActionButton
        recordBtn.setOnClickListener {
            val recordIntent = Intent(this@MainActivity, RecordActivity::class.java)
            startActivity(recordIntent)
        }

        recordingsList = findViewById(R.id.recordings) as ListView
        transcriptsAdapter = TranscriptAdapter()
        recordingsList?.adapter = transcriptsAdapter
        getRecordings()
    }


    fun getRecordings() {

        val transcriptDbHelper = TranscriptDbHelper(this)
        val db = transcriptDbHelper.readableDatabase
        val projection = arrayOf(TranscriptModel.TranscriptEntry.COLUMN_NAME_OVERALL_GRADE, TranscriptModel.TranscriptEntry.COLUMN_NAME_SENTENCES, TranscriptModel.TranscriptEntry.COLUMN_NAME_WORDS, TranscriptModel.TranscriptEntry.COLUMN_NAME_FILLER_WORDS, TranscriptModel.TranscriptEntry.COLUMN_NAME_SLOPPY_LANGUAGE, TranscriptModel.TranscriptEntry.COLUMN_NAME_PAUSES, TranscriptModel.TranscriptEntry.COLUMN_NAME_TRANSCRIPT, TranscriptModel.TranscriptEntry.COLUMN_NAME_CORRECTED, TranscriptModel.TranscriptEntry.COLUMN_NAME_LOCATION)

        val cursor = db.query(
                TranscriptModel.TranscriptEntry.TABLE_NAME,
                projection,
                null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            val transcript = Transcript()
            transcript.overallGrade = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_OVERALL_GRADE))
            transcript.sentences = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_SENTENCES))
            transcript.words = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_WORDS))
            transcript.filler_words = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_FILLER_WORDS))
            transcript.sloppy_language = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_SLOPPY_LANGUAGE))
            transcript.pauses = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_PAUSES))
            transcript.transcript = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_TRANSCRIPT))
            transcript.corrected = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_CORRECTED))
            transcript.location = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_LOCATION))
//            transcript.created = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_CREATED))
//            transcript.duration = cursor.getString(cursor.getColumnIndexOrThrow(TranscriptModel.TranscriptEntry.COLUMN_NAME_DURATION))
            transcripts.add(transcript)
        }
        cursor.close()

        transcriptsAdapter?.notifyDataSetChanged()
    }




    private inner class TranscriptAdapter : ArrayAdapter<Transcript>(applicationContext, R.layout.item_recording, transcripts) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView

            val transcript = getItem(position)

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_recording, parent, false)
            }


            val overall_grade = convertView?.findViewById(R.id.overall_grade) as TextView
            overall_grade.text = transcript!!.overallGrade

            val time = convertView.findViewById(R.id.time) as TextView
            time.text = transcript.duration

            val created = convertView.findViewById(R.id.created) as TextView
            created.text = transcript.created

            val preview = convertView.findViewById(R.id.preview) as TextView
            val strPreview = transcript.transcript.substring(0, Math.min(transcript.transcript.length, 55)) + "..."
            preview.text = strPreview

            convertView.setOnClickListener {
                val intent = Intent(this@MainActivity, TranscriptActivity::class.java)
                intent.putExtra("transcript", transcript)
                startActivity(intent)
            }

            return convertView
        }

    }




}
