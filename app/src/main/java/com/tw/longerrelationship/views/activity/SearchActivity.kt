package com.tw.longerrelationship.views.activity

import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.DairyAdapter
import com.tw.longerrelationship.databinding.ActivitySearchBinding
import com.tw.longerrelationship.util.InjectorUtils
import com.tw.longerrelationship.util.setOnClickListeners
import com.tw.longerrelationship.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class SearchActivity : BaseActivity() {
    private lateinit var mBinding: ActivitySearchBinding

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getSearchViewModelFactory()
        ).get(SearchViewModel::class.java)
    }

    private var dairyAdapter: DairyAdapter = DairyAdapter(this)

    override fun init(): View {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        initView()
        return mBinding.root
    }

    private fun initView() {
        click()
        showKeyboard()
        initEditText()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mBinding.rvDairy.apply {
            adapter = dairyAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
    }

    private fun click() {
        setOnClickListeners(
            mBinding.includeSearchBar.tvCancer,
            mBinding.includeSearchBar.ivCancer
        ) {
            when (this) {
                mBinding.includeSearchBar.tvCancer -> {
                    // 为了解决软键盘导致的view晃动问题
                    // 先让软键盘关闭，再返回上一个activity
                    closeKeyboard(windowToken)
                    handler.postDelayed({
                        finish()
                    }, 50)
                }
                mBinding.includeSearchBar.ivCancer -> {
                    mBinding.includeSearchBar.etSearch.text = null
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_search


    private fun initEditText() {
        // 输入监听
        mBinding.includeSearchBar.etSearch.doOnTextChanged { text, _, _, _ ->
            if (!TextUtils.isEmpty(text))
                mBinding.includeSearchBar.ivCancer.visibility = View.VISIBLE
            else
                mBinding.includeSearchBar.ivCancer.visibility = View.GONE
        }
        // 软键盘监听
        mBinding.includeSearchBar.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.clearFocus()                                  // 失去焦点
                getKeyDairy(v.text.toString())
                closeKeyboard(v.windowToken)
            }
            return@setOnEditorActionListener false
        }
    }

    private fun getKeyDairy(key: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getKeyDairy(key).collect {
                dairyAdapter.submitData(it)
            }
        }
    }

    /**
     * 显示键盘
     */
    private fun showKeyboard() {
        mBinding.includeSearchBar.etSearch.requestFocus()
        // 界面未构建完成时无法弹出键盘,需要开个延时
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                    mBinding.includeSearchBar.etSearch,
                    0
                )
            }
        }, 50)
    }

    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(R.anim.animation_right_in,R.anim.animation_left_out)
    }
}