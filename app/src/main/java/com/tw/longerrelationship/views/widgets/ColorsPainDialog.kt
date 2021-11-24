package com.tw.longerrelationship.views.widgets

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
import com.tw.longerrelationship.util.DataStoreUtil
import com.tw.longerrelationship.util.slideToUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * [DairyEditActivity]修改背景弹框
 */
class ColorsPainDialog(val activity: DairyEditActivity, val colorSelectCallBack: (Int) -> Unit) : DialogFragment() {
    private var mView: View? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var colorSelectAdapter: ColorItemSelectAdapter

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
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.attributes = params
        }
        activity.closeKeyboard()
        slideToUp(mView!!)

        mView!!.findViewById<ImageView>(R.id.iv_arrow_down).apply {
            setColorFilter(ContextCompat.getColor(requireContext(), R.color.DairyEditHintText))
            setOnClickListener { dismiss() }
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        colorSelectAdapter = ColorItemSelectAdapter(
            this,
            requireContext(),
            currentColorIndex
        )
        mRecyclerView = mView!!.findViewById(R.id.rv_background_select)
        mRecyclerView.apply {
            adapter = colorSelectAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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