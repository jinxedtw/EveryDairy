//package com.tw.longerrelationship.views.service
//
//import android.app.Service
//import android.content.Intent
//import android.media.MediaRecorder
//import android.os.Environment
//import android.os.IBinder
//import com.tw.longerrelationship.util.logE
//import java.io.File
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.*
//
//
//class RecordingService : Service() {
//    private var mRecorder: MediaRecorder? = null
//
//    private var mStartingTimeMillis: Long = 0
//    private var mElapsedMillis: Long = 0
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startRecording()
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        if (mRecorder != null) {
//            stopRecording()
//        }
//        super.onDestroy()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//
//    // 开始录音
//    private fun startRecording() {
//        mRecorder = MediaRecorder()
//        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
//        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //录音文件保存的格式，这里保存为 mp4
//        mRecorder!!.setOutputFile(createFilePath()) // 设置录音文件的保存路径
//        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//        mRecorder!!.setAudioChannels(1)
//        // 设置录音文件的清晰度
//        mRecorder!!.setAudioSamplingRate(44100)
//        mRecorder!!.setAudioEncodingBitRate(192000)
//        try {
//            mRecorder!!.prepare()
//            mRecorder!!.start()
//            mStartingTimeMillis = System.currentTimeMillis()
//        } catch (e: IOException) {
//            logE("RecordingService", "start recording failed", e)
//        }
//    }
//
//    // 设置录音文件的名字和保存路径
//    private fun createFilePath():String {
//
//
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DCIM)
//        return File.createTempFile(
//            "RECORDING_${timeStamp}_", /* prefix */
//            ".mp4", /* suffix */
//            storageDir /* directory */
//        ).toString()
//    }
//
//    // 停止录音
//    private fun stopRecording() {
//        mRecorder!!.stop()
//        mElapsedMillis = System.currentTimeMillis() - mStartingTimeMillis
//        mRecorder!!.release()
//        if (mIncrementTimerTask != null) {
//            mIncrementTimerTask.cancel()
//            mIncrementTimerTask = null
//        }
//        mRecorder = null
//    }
//}