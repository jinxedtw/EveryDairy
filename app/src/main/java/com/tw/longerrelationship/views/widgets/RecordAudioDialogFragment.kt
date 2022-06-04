package com.tw.longerrelationship.views.widgets

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.tw.longerrelationship.R
import com.tw.longerrelationship.views.service.RecordingService
import java.io.File

/**
 * 开始录音的 DialogFragment
 */
class RecordAudioDialogFragment : DialogFragment() {
    private var mStartRecording = true
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
            mFabRecord?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_stop_fill)
            ToastWithImage.showToast("开始录音...",true)
            val folder = File(Environment.getExternalStorageDirectory().toString() + "/SoundRecorder")
            if (!folder.exists()) {
                folder.mkdir()
            }

            mChronometerTime!!.base = SystemClock.elapsedRealtime()
            mChronometerTime!!.start()

            requireActivity().startService(intent)
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            mChronometerTime!!.stop()
            ToastWithImage.showToast("录音结束...",true)
            mFabRecord?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_fill)

            requireActivity().stopService(intent)
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            dismiss()
        }
    }
}