package com.mdislam.rehearse

import android.app.ProgressDialog
import android.os.AsyncTask
import android.util.Log

import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by mdnah on 5/21/2017.
 */

class UploadFileTask(private val dialog: ProgressDialog?, private val delegate: OnTaskCompleted) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String): String? {
        val fileName = params[0]

        val conn: HttpURLConnection
        val dos: DataOutputStream
        val lineEnd = "\r\n"
        val twoHyphens = "--"
        val boundary = "*****"
        var bytesRead: Int
        var bytesAvailable: Int
        var bufferSize: Int
        val buffer: ByteArray
        val maxBufferSize = 1024 * 1024
        val sourceFile = File(fileName)

        if (!sourceFile.isFile) {

            if (dialog != null && dialog.isShowing) {
                dialog.dismiss()
            }


        } else {


            val tempBuffer = StringBuilder()

            try {

                // open a URL connection to the Servlet
                val fileInputStream = FileInputStream(sourceFile)
                val url = URL("https://6a91b4aa.ngrok.io/api")

                // Open a HTTP  connection to  the URL
                conn = url.openConnection() as HttpURLConnection
                conn.doInput = true // Allow Inputs
                conn.doOutput = true // Allow Outputs
                conn.useCaches = false // Don't use a Cached Copy
                conn.requestMethod = "POST"
                conn.setRequestProperty("Connection", "Keep-Alive")
                conn.setRequestProperty("ENCTYPE", "multipart/form-data")
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary)
                conn.setRequestProperty("file", fileName)

                dos = DataOutputStream(conn.outputStream)

                dos.writeBytes(twoHyphens + boundary + lineEnd)
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + fileName + "\"" + lineEnd)

                dos.writeBytes(lineEnd)

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available()

                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                buffer = ByteArray(bufferSize)

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize)

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize)
                    bytesAvailable = fileInputStream.available()
                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd)
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

                // Responses from the server (code and message)
                val `is` = conn.inputStream
                val isr = InputStreamReader(`is`)

                var charRead: Int
                val inputBuffer = CharArray(500)

                while (true) {
                    charRead = isr.read(inputBuffer)
                    if (charRead <= 0) {
                        break
                    }

                    tempBuffer.append(String(inputBuffer, 0, charRead))
                }

                //close the streams //
                fileInputStream.close()
                dos.flush()
                dos.close()

                return tempBuffer.toString()

            } catch (e: Exception) {

                if (dialog != null && dialog.isShowing) {
                    dialog.dismiss()
                }
            }

            if (dialog != null && dialog.isShowing) {
                dialog.dismiss()
            }


        }

        return null
    }

    override fun onPostExecute(s: String) {
        delegate.onTaskComplete(s)
    }
}
