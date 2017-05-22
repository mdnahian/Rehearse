package com.mdislam.rehearse

import android.util.Log

import org.json.JSONException
import org.json.JSONObject
import org.xml.sax.Locator

/**
 * Created by mdnah on 5/21/2017.
 */

class ParseResponse(private val raw: String) {


    fun execute(): Transcript? {
        val transcript = Transcript()

        try {

            val jsonObject = JSONObject(raw)
            val status = jsonObject.getString("status")
            if (status == "error") {
                return null
            } else {
                transcript.overallGrade = jsonObject.getString("overall_grade")
                transcript.sentences = jsonObject.getString("sentences")
                transcript.words = jsonObject.getString("words")
                transcript.filler_words = jsonObject.getString("filler_words")
                transcript.sloppy_language = jsonObject.getString("sloppy_language")
                transcript.pauses = jsonObject.getString("pauses")
                transcript.transcript = jsonObject.getString("transcript")
                transcript.corrected = jsonObject.getString("corrected")
                return transcript
            }
        } catch (e: JSONException) {
            //            Log.d("Crash", e.getMessage());
            return null
        } catch (e: NullPointerException) {
            //            Log.d("Crash", e.getMessage());
            return null
        }

    }

}
