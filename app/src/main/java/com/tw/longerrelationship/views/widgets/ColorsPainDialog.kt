package com.tw.longerrelationship.views.widgets

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.tw.longerrelationship.views.activity.DairyEditActivity
import android.view.*
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.ColorItemSelectAdapter
import com.tw.longerrelationship.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class ColorsPainDialog(val activity: DairyEditActivity, val colorSelectCallBack: (Int) -> Unit) : DialogFragment() {
    private lateinit var mView: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var colorSelectAdapter: ColorItemSelectAdapter
    private var lastX = 0f
    private var lastY = 0f
    private val dialogInitHeight = dp2px(250)
    private var dialogHeight = dialogInitHeight

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.layout_colors_select, container, false)
        return mView
    }

    override fun onStart() {
        super.onStart()
        //设置dialog相应属性
        dialog?.window?.also {
            val params = it.attributes
            params.gravity = Gravity.BOTTOM
            params.height = dialogHeight
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.attributes = params
        }
        activity.closeKeyboard()
        slideToUp(mView)

        mView.findViewById<ImageView>(R.id.iv_arrow_down).apply {
            setColorFilter(ContextCompat.getColor(requireContext(), R.color.DairyEditHintText))
            setOnClickListener { dismiss() }
        }
        initRecyclerView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        colorSelectAdapter = ColorItemSelectAdapter(this, requireContext(), currentColorIndex)
        mRecyclerView = mView.findViewById(R.id.rv_background_select)
        mRecyclerView.apply {
            adapter = colorSelectAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        mView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - lastX
                    val deltaY = event.rawY - lastY

                    if (abs(deltaY) > abs(deltaX)) {
                        logD("弹窗拖动", "deltaY:${deltaY},deltaX:${deltaX}")
                        dragDialog(deltaY)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (dialogHeight / dialogInitHeight < 0.8) {
                        dismiss()
                    }
                }
            }
            lastX = event.rawX
            lastY = event.rawY
            true
        }
    }

    private fun dragDialog(dragHeight: Float) {
        dialogHeight -= dragHeight.toInt()
        dialogHeight = maxOf(0, dialogHeight)
        dialogHeight = minOf(dialogHeight, dialogInitHeight)
        dialog?.window?.also {
            val params = it.attributes
            params.height = dialogHeight
            params.gravity = Gravity.BOTTOM
            it.attributes = params
        }
    }

    fun onColorSelect(@ColorRes colorRes: Int, position: Int) {
        colorSelectCallBack(colorRes)
        colorSelectAdapter.currentIndex = position
        colorSelectAdapter.notifyItemChanged(currentColorIndex)
        colorSelectAdapter.notifyItemChanged(position)
        currentColorIndex = position
    }

    companion object {
        const val DEFAULT_COLOR_INDEX = "colorIndex"

        var currentColorIndex: Int
            get() = DataStoreUtil[DEFAULT_COLOR_INDEX] ?: 0
            set(value) {
                CoroutineScope(Dispatchers.IO).launch {
                    DataStoreUtil.putData(DEFAULT_COLOR_INDEX, value)
                }
            }
    }
}