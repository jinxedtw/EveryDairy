package com.tw.longerrelationship.views.service

import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Environment
import android.os.IBinder
import com.tw.longerrelationship.util.runReportCatching
import java.util.*

/**
 * 录音的 Service
 */
class RecordingService : Service() {
    private var mFilePath: String? = null
    private var mRecorder: MediaRecorder? = null
    private var mStartingTimeMillis: Long = 0
    private var mElapsedMillis: Long = 0
    private var mIncrementTimerTask: TimerTask? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startRecording()
        return START_STICKY
    }

    override fun onDestroy() {
        if (mRecorder != null) {
            stopRecording()
        }
        super.onDestroy()
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mRecorder!!.setOutputFile(getFilePath())
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mRecorder!!.setAudioChannels(1)
        mRecorder!!.setAudioSamplingRate(44100)
        mRecorder!!.setAudioEncodingBitRate(192000)
        runReportCatching {
            mRecorder!!.prepare()
            mRecorder!!.start()
            mStartingTimeMillis = System.currentTimeMillis()
        }
    }

    private fun getFilePath(): String? {
        mFilePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.path + "/video_${System.currentTimeMillis()}.mp4"
        return mFilePath
    }


    private fun stopRecording() {
        mRecorder!!.stop()
        mElapsedMillis = System.currentTimeMillis() - mStartingTimeMillis
        mRecorder!!.release()
        getSharedPreferences("sp_name_audio", MODE_PRIVATE)
            .edit()
            .putString("audio_path", mFilePath)
            .putLong("elpased", mElapsedMillis)
            .apply()
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask!!.cancel()
            mIncrementTimerTask = null
        }
        mRecorder = null
    }
}