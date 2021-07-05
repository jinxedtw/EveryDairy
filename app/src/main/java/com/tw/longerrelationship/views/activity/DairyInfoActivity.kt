package com.tw.longerrelationship.views.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivityDairyInfoBinding
import com.tw.longerrelationship.util.InjectorUtils
import com.tw.longerrelationship.util.setOnClickListeners
import com.tw.longerrelationship.util.showToast
import com.tw.longerrelationship.viewmodel.DairyInfoViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.*


class DairyInfoActivity : BaseActivity() {
    private lateinit var mBinding: ActivityDairyInfoBinding

    private val viewModel: DairyInfoViewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getDairyInfoViewModelFactory(intent.getIntExtra("dairyId", -1))
        ).get(DairyInfoViewModel::class.java)
    }

    override fun init() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())

        initView()
        click()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        lifecycleScope.launch {
            viewModel.getDairy().filter {
                it != null
            }.collect {          // 为什么这里删除日记后collect会返回空值
                val calendar: Calendar = Calendar.getInstance().apply { time = it.time }
                mBinding.tvDay.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
                mBinding.tvYearAndMonth.text =
                    "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月/${
                        timeConverterMap[calendar.get(
                            Calendar.DAY_OF_WEEK
                        )]
                    }"
                mBinding.tvTime.text =
                    "${
                        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) "0${calendar.get(Calendar.HOUR_OF_DAY)}"
                        else calendar.get(Calendar.HOUR_OF_DAY)
                    }:${
                        if (calendar.get(Calendar.MINUTE) < 10) "0${calendar.get(Calendar.MINUTE)}"
                        else calendar.get(Calendar.MINUTE)
                    }"
                mBinding.tvTitle.text = it.title
                mBinding.ivWeather.setImageDrawable(
                    ContextCompat.getDrawable(
                        baseContext,
                        it.weather
                    )
                )
                mBinding.ivMood.setImageDrawable(
                    ContextCompat.getDrawable(
                        baseContext,
                        it.mood
                    )
                )
                mBinding.tvContent.text = it.content
                mBinding.tvLocation.text = it.location
                mBinding.tvTextLength.text =
                    String.format(getString(R.string.text_content_num), it.content?.length ?: 0)
                if (it.uriList.isNotEmpty())
                    Glide.with(this@DairyInfoActivity).load(it.uriList[0]).into(mBinding.ivPhoto)
                else
                    mBinding.ivPhoto.visibility = View.GONE
            }
        }
    }

    private fun click() {
        setOnClickListeners(
            mBinding.tvBack,
            mBinding.ivBack,
            mBinding.ivCopy,
            mBinding.ivMore,
            mBinding.ivEdit
        ) {
            when (this) {
                mBinding.tvBack, mBinding.ivBack -> {
                    finish()
                }
                mBinding.ivCopy -> {
                    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
                        setPrimaryClip(ClipData.newPlainText("日记文本", mBinding.tvContent.text))
                    }
                    showToast(this@DairyInfoActivity, "复制文本成功")
                }
                mBinding.ivEdit -> {

                }
                mBinding.ivMore -> {
                    showPopupMenu()
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_dairy_info

    /**
     * 显示弹出菜单栏
     */
    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, mBinding.ivMore)
        popupMenu.inflate(R.menu.activity_dairy_info_more)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.it_info -> {
                        Toast.makeText(baseContext, "Option 1", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.it_email -> {
                        Toast.makeText(baseContext, "Option 2", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.it_delete -> {
                        viewModel.deleteDairy()
                        finish()
                        showToast(baseContext, "删除成功")
                        return true
                    }
                    else -> {
                    }
                }
                return false
            }
        })
        popupMenu.show()
    }


    companion object {
        val timeConverterMap =
            hashMapOf(1 to "周日", 2 to "周一", 3 to "周二", 4 to "周三", 5 to "周四", 6 to "周五", 7 to "周六")
    }
}