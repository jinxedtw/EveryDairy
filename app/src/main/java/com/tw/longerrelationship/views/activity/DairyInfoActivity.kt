package com.tw.longerrelationship.views.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.tw.longerrelationship.BuildConfig
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.PictureShowAdapter
import com.tw.longerrelationship.databinding.ActivityDairyInfoBinding
import com.tw.longerrelationship.help.SpacesItemDecoration
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.viewmodel.DairyInfoViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class DairyInfoActivity : BaseActivity() {
    private lateinit var mBinding: ActivityDairyInfoBinding
    private var stickerId = 0
    private val layoutManager = GridLayoutManager(this, 3)
    private var dairyId = -1
    private val viewModel: DairyInfoViewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getDairyInfoViewModelFactory(intent.getIntExtra(DAIRY_ID, -1))
        ).get(DairyInfoViewModel::class.java)
    }

    /**
     * 跳转Activity启动器
     */
    private val toPictureInfoLauncher = registerForActivityResult(ToPictureInfoResultContract()) {}

    override fun init() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        initView()
        click()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        stickerId = sharedPreferences.getInt(STICKER_ID, 0)
        setStickerDrawable()
        lifecycleScope.launch {
            viewModel.getDairy().filter {
                it != null
            }.collect {          // 为什么这里删除日记后collect会返回空值
                viewModel.pictureList = it.uriList as ArrayList<Uri>
                mBinding.rvPhotoList.apply {
                    addItemDecoration(SpacesItemDecoration(60))
                    adapter = PictureShowAdapter(it.uriList, this@DairyInfoActivity)
                    layoutManager = this@DairyInfoActivity.layoutManager
                }
                dairyId = it.id ?: -1
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
            }
        }
    }

    private fun setStickerDrawable() {
        val resID = resources.getIdentifier(
            "ic_sticker_${(++stickerId)%9}",
            "drawable",
            BuildConfig.APPLICATION_ID
        )
        mBinding.ivSticker.setImageDrawable(
            ContextCompat.getDrawable(
                baseContext,
                resID
            )
        )
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
                    jumpToDairyEditActivity()
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
                    R.id.it_stickers -> {
                        setStickerDrawable()
                        sharedPreferences.edit().putInt(STICKER_ID,stickerId%9-1).apply()
                        showToast(baseContext, "切换成功")
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

    fun pictureInfoActivityJump(index: Int) {
        val bundle = Bundle().apply {
            putParcelableArrayList(PICTURE_LIST, viewModel.pictureList)
            putInt(CURRENT_PICTURE, index)
            putBoolean(IF_CAN_DELETE, false)
        }
        toPictureInfoLauncher.launch(bundle)
    }

    /**
     * 携带日记Id，跳转到[DairyEditActivity]
     */
    private fun jumpToDairyEditActivity() {
        val intent = Intent(this, DairyEditActivity::class.java)
        intent.putExtra(DAIRY_ID, dairyId)

        startActivity(intent)
    }

    /**
     * 跳转到[PictureInfoActivity]  协议类
     * 传入参数  ArrayList<Bitmap>  图片列表
     * 返回参数  Unit
     */
    inner class ToPictureInfoResultContract : ActivityResultContract<Bundle, Unit>() {
        override fun createIntent(context: Context, input: Bundle): Intent {
            return Intent(context, PictureInfoActivity::class.java).apply {
                putExtras(input)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?) {
        }
    }

    companion object {
        const val STICKER_ID = "stickerId"
        val timeConverterMap =
            hashMapOf(1 to "周日", 2 to "周一", 3 to "周二", 4 to "周三", 5 to "周四", 6 to "周五", 7 to "周六")
    }
}