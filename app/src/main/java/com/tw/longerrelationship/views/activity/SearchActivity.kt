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

    override fun init() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        initView()
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
                    finish()
                }
                mBinding.includeSearchBar.ivCancer -> {
                    mBinding.includeSearchBar.etSearch.text = null
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_search

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
        }, 300)
    }

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
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    v.windowToken,                              // 关闭软键盘
                    0
                )
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
}