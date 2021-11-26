package com.tw.longerrelationship.views.activity

import android.app.Activity
import android.content.Intent
import android.transition.TransitionInflater
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.ImageAdapter
import com.tw.longerrelationship.databinding.ActivityPictureInfoBinding
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.util.Constants.INTENT_CURRENT_PICTURE
import com.tw.longerrelationship.util.Constants.INTENT_IF_CAN_DELETE
import com.tw.longerrelationship.util.Constants.INTENT_PICTURE_LIST
import com.tw.longerrelationship.viewmodel.PictureInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PictureInfoActivity : BaseActivity<ActivityPictureInfoBinding>() {
    private var ifCanDelete: Boolean = true
    private var current: Int = -1

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getPictureInfoViewModelFactory()
        ).get(PictureInfoViewModel::class.java)
    }

    override fun init() {
        setAndroidNativeLightStatusBar(this, false)
        initBinding()
        makeStatusBarTransparent(this)
        observe()
        initParams()
        initView()
    }

    private fun observe() {
        viewModel.currentPicture.observe(this) {
            mBinding.tvCurrentPicture.text =
                String.format(
                    getString(R.string.currentPicture),
                    it + 1,
                    viewModel.uriList.size
                )
        }
    }

    private fun initParams() {
        val data = intent.extras
        if (data != null) {
            viewModel.uriList =
                data.getParcelableArrayList(INTENT_PICTURE_LIST) ?: ArrayList()
            viewModel.currentPicture.postValue(data.getInt(INTENT_CURRENT_PICTURE))
            current = data.getInt(INTENT_CURRENT_PICTURE)
            ifCanDelete = data.getBoolean(INTENT_IF_CAN_DELETE, true)
        }
    }

    private fun initView() {
        mBinding.vpShowPicture.adapter = ImageAdapter(viewModel.uriList, this)
        mBinding.vpShowPicture.setCurrentItem(current, false)
        mBinding.tvDelete.visibility = if (ifCanDelete) View.VISIBLE else View.GONE

        mBinding.vpShowPicture.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.currentPicture.value = position
            }
        })

        setOnClickListeners(mBinding.ivGoBack, mBinding.tvDelete) {
            when (this) {
                // 删除图片,结束Activity
                mBinding.tvDelete -> {
                    val intent = Intent().apply {
                        putExtra("result", viewModel.currentPicture.value)
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finishAfterTransition()
                }
                mBinding.ivGoBack -> {
                    finishAfterTransition()
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_picture_info

    /** 设置左右滑动切换图片 */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}