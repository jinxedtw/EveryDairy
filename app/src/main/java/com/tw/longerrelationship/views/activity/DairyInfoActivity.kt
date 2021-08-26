package com.tw.longerrelationship.views.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
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
import com.tw.longerrelationship.views.widgets.ToastWithImage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig
import java.util.*

/**
 * 日记详情界面
 * 从[MainActivity]点击单项日记跳转，点击编辑跳转到[DairyEditActivity]
 */
class DairyInfoActivity : BaseActivity() {
    private lateinit var mBinding: ActivityDairyInfoBinding
    private var stickerId = 0
    private var dairyId = -1
    private val layoutManager by lazy {
        object : GridLayoutManager(this, 3) {
            override fun canScrollVertically(): Boolean {
                return false            // 解决Scrollview嵌套RecyclerView滑动卡顿问题
            }
        }
    }
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

    override fun init(): View {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        initView()
        click()
        observe()
        return mBinding.root
    }

    private fun observe() {
        viewModel.ifFavorites.observe(this) {
            mBinding.ivFavorites.setDrawable(if (it) R.drawable.ic_star_fill else R.drawable.ic_star)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        stickerId = sharedPreferences.getInt(STICKER_ID, 0)
        setStickerDrawable()
        mBinding.rvPhotoList.apply {
            addItemDecoration(SpacesItemDecoration(60))
            layoutManager = this@DairyInfoActivity.layoutManager
        }

        lifecycleScope.launch {
            viewModel.getDairy().collect {
                viewModel.dairyItem = it
                viewModel.pictureList = it.uriList as ArrayList<Uri>
                viewModel.ifFavorites.value = it.isLove
                dairyId = it.id ?: -1
                val calendar: Calendar = Calendar.getInstance().apply { time = it.createTime }
                mBinding.rvPhotoList.adapter =
                    PictureShowAdapter(it.uriList, this@DairyInfoActivity)
                mBinding.tvDay.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
                mBinding.tvYearAndMonth.text =
                    "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月/${
                        timeConverterMap[calendar.get(
                            Calendar.DAY_OF_WEEK
                        )]
                    }"
                mBinding.tvTime.text = getHourMinuteTime(calendar)
                mBinding.tvTitle.text = it.title
                mBinding.ivWeather.setDrawable(it.weather)
                mBinding.ivMood.setDrawable(it.mood)
                mBinding.tvContent.text = it.content
                mBinding.tvLocation.text = it.location
                mBinding.tvTextLength.text =
                    String.format(getString(R.string.text_content_num), it.content?.length ?: 0)
            }
        }
    }

    private fun setStickerDrawable() {
        val resID = resources.getIdentifier(
            "ic_sticker_${(++stickerId) % 9}",
            "drawable",
            BuildConfig.APPLICATION_ID
        )
        mBinding.ivSticker.setDrawable(resID)
    }

    private fun click() {
        setOnClickListeners(
            mBinding.tvBack,
            mBinding.ivBack,
            mBinding.ivFavorites,
            mBinding.ivMore,
            mBinding.ivEdit
        ) {
            when (this) {
                mBinding.tvBack, mBinding.ivBack -> {
                    finish()
                }
                mBinding.ivFavorites -> {
                    val bool = !viewModel.ifFavorites.value!!
                    viewModel.ifFavorites.value = bool
                    if (bool) {
                        showToast(this@DairyInfoActivity, "已收藏")
                    } else {
                        showToast(this@DairyInfoActivity, "已取消收藏")
                    }
                    viewModel.favoriteDairy(dairyId)
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
        val location = IntArray(2)
        mBinding.ivMore.getLocationOnScreen(location)

        QuickPopupBuilder.with(this).contentView(R.layout.layout_dairy_info_popup_menu)
            .config(
                QuickPopupConfig().backgroundColor(Color.TRANSPARENT)
                    .withClick(R.id.tv_info, {
                        AlertDialog.Builder(this@DairyInfoActivity).setTitle("详情")
                            .setMessage(getDateInfo())
                            .setPositiveButton("我知道了", null)
                            .show()
                    }, true)
                    .withClick(R.id.tv_copy_content, {
                        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
                            setPrimaryClip(ClipData.newPlainText("日记文本", mBinding.tvContent.text))
                        }
                        showToast(this@DairyInfoActivity, "复制文本成功")
                    }, true)
                    .withClick(R.id.tv_stickers, {
                        setStickerDrawable()
                        sharedPreferences.edit().putInt(STICKER_ID, stickerId % STICKER_NUM - 1).apply()
                        ToastWithImage.showToast("切换成功",true)
//                        showToast(baseContext, "切换成功")
                    }, true)
                    .withClick(R.id.tv_delete, {
                        viewModel.deleteDairy()
                        finish()
                        showToast(baseContext, "删除成功")
                    }, true)
            )
            .show(
                location[0] - mBinding.ivMore.width, location[1] + POPUP_WINDOW_HEIGHT + mBinding.ivMore.height
            )
    }

    private fun getDateInfo(): String {
        val str = StringBuilder()
        val dateInfo = viewModel.dairyItem.editInfoList
        for (i in dateInfo.indices) {
            val calendar: Calendar = Calendar.getInstance().apply { time = dateInfo[i] }
            str.append(
                (if (i == 0) "创建时间: " else "修改时间: ").plus(
                    "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月${
                        calendar.get(Calendar.DAY_OF_MONTH)
                    }日  ${getHourMinuteTime(calendar)}"
                )
            )
            if (i != dateInfo.size - 1) str.append("\n")
        }
        return str.toString()
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
        const val STICKER_NUM: Int = 9
        const val STICKER_ID = "stickerId"
        const val POPUP_WINDOW_HEIGHT: Int = 160
        val timeConverterMap =
            hashMapOf(1 to "周日", 2 to "周一", 3 to "周二", 4 to "周三", 5 to "周四", 6 to "周五", 7 to "周六")
    }

}