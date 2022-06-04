package com.tw.longerrelationship.views.widgets

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.LayoutRecodebarBinding
import com.tw.longerrelationship.util.setMultiViewState
import com.tw.longerrelationship.util.setOnClickListeners
import java.util.*
import kotlin.concurrent.timerTask

class RecordBar : ConstraintLayout {
    private lateinit var mBinding: LayoutRecodebarBinding
    private var mediaPlayer: MediaPlayer? = null
    private val timer: Timer = Timer()

    constructor(context: Context) : super(context) {
        initUI()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initUI()
        initEvent()
    }

    private fun initEvent() {
        setOnClickListeners(mBinding.ivPlayRecord) {
            when (this) {
                mBinding.ivPlayRecord -> {
                    if (isPlaying()) {
                        mBinding.ivPlayRecord.setImageDrawable(context.getDrawable(R.drawable.ic_play_fill))
                        mediaPlayer?.pause()
                    } else {
                        mBinding.ivPlayRecord.setImageDrawable(context.getDrawable(R.drawable.ic_stop_fill))
                        mediaPlayer?.start()
                    }
                }
            }
        }
    }


    private fun initUI() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_recodebar, this, true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mediaPlayer?.release()
        timer.cancel()
    }


    fun initMedia(path: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.let { it ->
            setMultiViewState(VISIBLE, mBinding.tvTimeNow, mBinding.tvTimeTotal, mBinding.seekBar, mBinding.ivPlayRecord)
            it.setAudioStreamType(AudioManager.STREAM_MUSIC)
            it.setDataSource(path)
            it.prepareAsync()
            it.isLooping = true
            it.setOnPreparedListener {
                mBinding.tvTimeNow.text = "00:00"
                refreshTime(it.duration, mBinding.tvTimeTotal)

                timer.schedule(timerTask {
                    runOnUiThread {
                        refreshTime(mediaPlayer?.currentPosition ?: 0, mBinding.tvTimeNow)
                        mBinding.seekBar.progress = (mediaPlayer!!.currentPosition.toFloat() / mediaPlayer!!.duration * 100).toInt()
                    }
                }, 0, 1000)
            }


        }
        initSeekBar()
    }

    private fun initSeekBar() {
        mBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var nowTime = 0f

            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                nowTime = progress / 100f * mediaPlayer!!.duration
                refreshTime(nowTime.toInt(), mBinding.tvTimeNow)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mediaPlayer?.seekTo(nowTime.toInt())
                mBinding.ivPlayRecord.setImageDrawable(context.getDrawable(R.drawable.ic_stop_fill))
                mediaPlayer?.start()
            }
        })
    }

    private fun refreshTime(time: Int, textView: TextView) {
        val minOfNow = time / 1000 / 60
        val secondOfNow = time / 1000 - minOfNow * 60
        var min = minOfNow.toString()
        var second = secondOfNow.toString()

        if (minOfNow < 10) {
            min = "0$minOfNow"
        }
        if (secondOfNow < 10) {
            second = "0$secondOfNow"
        }

        textView.text = "$min:$second"
    }

    private fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

}