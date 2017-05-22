package com.mdislam.rehearse

import java.io.Serializable

/**
 * Created by mdnah on 5/21/2017.
 */

class Transcript : Serializable {

    var overallGrade: String = ""
    var sentences: String = ""
    var words: String = ""
    var filler_words: String = ""
    var sloppy_language: String = ""
    var pauses: String = ""
    var transcript: String = ""
    var corrected: String = ""
    var location: String = ""
    var created: String = ""
    var duration: String = ""


    constructor() {}


    constructor(overallGrade: String, sentences: String, words: String, filler_words: String, sloppy_language: String, pauses: String, transcript: String, corrected: String, location: String, created: String, duration: String) {
        this.overallGrade = overallGrade
        this.sentences = sentences
        this.words = words
        this.filler_words = filler_words
        this.sloppy_language = sloppy_language
        this.pauses = pauses
        this.transcript = transcript
        this.corrected = corrected
        this.location = location
        this.created = created
        this.duration = duration
    }
}
