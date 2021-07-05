 package com.tw.longerrelationship.views.activity

import android.app.Activity
import android.content.Intent
import android.view.MotionEvent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.ImageAdapter
import com.tw.longerrelationship.databinding.ActivityPictureInfoBinding
import com.tw.longerrelationship.viewmodel.PictureInfoViewModel
import com.tw.longerrelationship.util.InjectorUtils
import com.tw.longerrelationship.util.makeStatusBarTransparent
import com.tw.longerrelationship.util.setAndroidNativeLightStatusBar
import com.tw.longerrelationship.util.setOnClickListeners


class PictureInfoActivity : BaseActivity() {

    private lateinit var mBinding: ActivityPictureInfoBinding

    private var current: Int = -1

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getPictureInfoViewModelFactory()
        ).get(PictureInfoViewModel::class.java)
    }

    override fun init() {
        setAndroidNativeLightStatusBar(this,false)
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())

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
                data.getParcelableArrayList("pictureList") ?: ArrayList()
            viewModel.currentPicture.postValue(data.getInt("currentPicture"))
            current = data.getInt("currentPicture")
        }
    }

    private fun initView() {
        mBinding.vpShowPicture.adapter = ImageAdapter(viewModel.uriList, this)
        mBinding.vpShowPicture.setCurrentItem(current,false)

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
                    finish()
                }
                mBinding.ivGoBack -> {
                    finish()
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_picture_info

    /**
     * 设置左右滑动切换图片
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {


        return super.onTouchEvent(event)
    }
}