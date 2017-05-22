package com.mdislam.rehearse

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.TextView
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class RecordActivity : AppCompatActivity() {

    var fileName = ""
    val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val REQUEST_RECORD_AUDIO_PERMISSION = 200
    val recorder = MediaRecorder()
    var permissionToRecordAccepted = false
    var timerText: TextView? = null

    var MillisecondTime:Long = 0
    var StartTime:Long = 0
    var TimeBuff:Long = 0
    var UpdateTime = 0L
    var handler = Handler()
    var Seconds:Int = 0
    var Minutes:Int = 0
    var MilliSeconds:Int = 0


    var progress:ProgressDialog? = null


    val runnable = object:Runnable {
         override fun run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime
            UpdateTime = TimeBuff + MillisecondTime
            Seconds = (UpdateTime / 1000).toInt()
            Minutes = Seconds / 60
             Seconds %= 60
            MilliSeconds = (UpdateTime % 1000).toInt()
            val text = "" + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds)
            timerText!!.setText(text)
            handler.postDelayed(this, 0)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val stopBtn = findViewById(R.id.stopBtn) as FloatingActionButton
        stopBtn.setOnClickListener {
            stopRecording()
        }

        timerText = findViewById(R.id.time) as TextView

        fileName = externalCacheDir.absolutePath
        fileName += "/"+getSaltString()+".3gp"

        progress = ProgressDialog(this@RecordActivity)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
            }
        } else {
            startRecording()
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if(grantResults[0] == Activity.RESULT_CANCELED){
                    finish()
                } else {
                    permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                }
            }
        }
        if (!permissionToRecordAccepted) {
            finish()
        } else {
            startRecording()
        }
    }


    override fun onStop() {
        super.onStop()
        recorder.release()
    }



    fun startRecording() {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setOutputFile(fileName)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try
        {
            recorder.prepare()
        }
        catch (e:IOException) {
            Log.e("Crash", "prepare() failed")
        }
        recorder.start()

        startTimer()
    }


    fun stopRecording() {
        TimeBuff += MillisecondTime
        handler.removeCallbacks(runnable)
        recorder.stop()
        recorder.release()

        progress?.setMessage("Analyzing...")
        progress?.show()
        val uploadAudioTask = UploadFileTask(progress, object:OnTaskCompleted {
            override fun onTaskComplete(response: String){
                progress?.setMessage("Building Transcript...")
                Log.d("Crash", response)
                val transcript = ParseResponse(response).execute()
                if(transcript != null){
                    transcript.location = fileName
                    transcript.created = SimpleDateFormat("MMMMM d, yyyy", Locale.US).format(Date())
                    transcript.duration = timerText?.text.toString()
                    val helper = TranscriptDbHelper(this@RecordActivity)
                    val db = helper.writableDatabase
                    val values = ContentValues()
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_OVERALL_GRADE, transcript.overallGrade)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_SENTENCES, transcript.sentences)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_WORDS, transcript.words)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_FILLER_WORDS, transcript.filler_words)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_SLOPPY_LANGUAGE, transcript.sloppy_language)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_PAUSES, transcript.pauses)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_TRANSCRIPT, transcript.transcript)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_CORRECTED, transcript.corrected)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_LOCATION, transcript.location)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_CREATED, transcript.created)
                    values.put(TranscriptModel.TranscriptEntry.COLUMN_NAME_DURATION, transcript.duration)

                    db.insert(TranscriptModel.TranscriptEntry.TABLE_NAME, null, values)
                    progress?.dismiss()
                    finish()
                } else {
                    progress?.dismiss()
                    finish()
                }
            }
        })
        uploadAudioTask.execute(fileName)

    }


    fun startTimer() {
        StartTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 0)
    }




    fun getSaltString():String {
        val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < SALTCHARS.length)
        { // length of the random string.
            var index = Math.round(rnd.nextFloat() * SALTCHARS.length)
            while (index > SALTCHARS.length-1){
                index--
            }
            salt.append(SALTCHARS[index])
        }
        val saltStr = salt.toString()
        return saltStr
    }





}
