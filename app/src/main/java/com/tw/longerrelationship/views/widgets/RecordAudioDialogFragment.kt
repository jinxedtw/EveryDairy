package com.tw.longerrelationship.views.widgets

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.tw.longerrelationship.R
import com.tw.longerrelationship.views.service.RecordingService
import java.io.File

/**
 * 开始录音的 DialogFragment
 */
class RecordAudioDialogFragment : DialogFragment() {
    private var mStartRecording = true
    var timeWhenPaused: Long = 0
    private var mFabRecord: ImageView? = null
    private var mChronometerTime: Chronometer? = null
    private var mIvClose: ImageView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_record_audio, null)
        initView(view)
        mFabRecord!!.setOnClickListener {
            onRecord(mStartRecording)
            mStartRecording = !mStartRecording
        }
        mIvClose!!.setOnClickListener { dismiss() }
        builder.setCancelable(false)
        builder.setView(view)
        return builder.create()
    }

    private fun initView(view: View) {
        mChronometerTime = view.findViewById(R.id.record_audio_chronometer_time)
        mFabRecord = view.findViewById(R.id.record_audio_fab_record)
        mIvClose = view.findViewById(R.id.record_audio_iv_close)
    }

    private fun onRecord(start: Boolean) {
        val intent = Intent(activity, RecordingService::class.java)
        if (start) {
            Toast.makeText(activity, "开始录音...", Toast.LENGTH_SHORT).show()
            val folder = File(Environment.getExternalStorageDirectory().toString() + "/SoundRecorder")
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir()
            }

            //start Chronometer
            mChronometerTime!!.base = SystemClock.elapsedRealtime()
            mChronometerTime!!.start()

            //start RecordingService
            requireActivity().startService(intent)
            //keep screen on while recording
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            //stop recording
//            mFabRecord!!.setImageResource(R.drawable.ic_mic_white_36dp)
            //mPauseButton.setVisibility(View.GONE);
            mChronometerTime!!.stop()
            timeWhenPaused = 0
            Toast.makeText(activity, "录音结束...", Toast.LENGTH_SHORT).show()
            requireActivity().stopService(intent)
            //allow the screen to turn off again once recording is finished
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                onRecord(mStartRecording)
            }
        }
    }
}