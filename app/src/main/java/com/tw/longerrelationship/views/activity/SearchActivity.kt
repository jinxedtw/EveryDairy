package com.tw.longerrelationship.views.activity

import android.app.AlertDialog
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.DairyAdapter
import com.tw.longerrelationship.databinding.ActivitySearchBinding
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.*


class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private var searchHistoryString: StringBuilder = StringBuilder()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getSearchViewModelFactory()
        ).get(SearchViewModel::class.java)
    }

    private var dairyAdapter: DairyAdapter = DairyAdapter(this, isHomeActivity = false)

    override fun init() {
        initBinding()
        initView()
    }

    private fun initView() {
        initEvent()
        showKeyboard(mBinding.includeSearchBar.etSearch)
        initEditText()
        initFlowLayout()
        initRecyclerView()
    }

    private fun initFlowLayout() {
        lifecycleScope.launch {
            DataStoreUtil.getData(SEARCH_HISTORY, "").first {
                if (it.isNotEmpty()) {
                    parseStringToText(it)
                }
                true
            }
        }
    }

    private fun parseStringToText(str: String) {
        var num = 0
        str.split("$$").forEach {
            // 最多20条历史搜索
            if (it != "" && num++ < MAX_HISTORY_COUNT) {
                searchHistoryString.append("$it$$")
                addViewToFlowLayout(it)
            }
        }
    }

    private fun addViewToFlowLayout(it: String) {
        val itemView = View.inflate(this, R.layout.item_history_text, null)
        itemView.apply {
            layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundResource(R.drawable.bg_flow_item)
            setOnClickListener { _ ->
                mBinding.includeSearchBar.etSearch.setText(it)
                mBinding.includeSearchBar.etSearch.clearFocus()                                  // 失去焦点
                getKeyDairy(it)                  // 查找数据库
                closeKeyboard(mBinding.includeSearchBar.etSearch.windowToken)
                mBinding.tvHistory.gone()                       // 隐藏历史搜索相关控件
                mBinding.ivDelete.gone()
                mBinding.flowLayout.gone()
            }
            findViewById<TextView>(R.id.tv_history).text = it
            findViewById<ImageView>(R.id.iv_item_delete).setOnClickListener {
                showToast("itemDelete")
            }
        }

        mBinding.flowLayout.addView(itemView)
    }

    private fun initRecyclerView() {
        mBinding.rvDairy.apply {
            adapter = dairyAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
    }

    private fun initEvent() {
        setOnClickListeners(
            mBinding.includeSearchBar.tvCancer,
            mBinding.includeSearchBar.ivCancer,
            mBinding.ivDelete,
            mBinding.tvDeleteAll,
            mBinding.tvComplete,
            mBinding.includeSearchBar.clSearchBar
        ) {
            when (this) {
                mBinding.includeSearchBar.tvCancer -> {
                    // 为了解决软键盘导致的view晃动问题
                    // 先让软键盘关闭，再返回上一个activity
                    closeKeyboard(windowToken)
                    handler.postDelayed({
                        finish()
                    }, 50)
                    finishAfterTransition()
                }
                mBinding.includeSearchBar.ivCancer -> {
                    mBinding.includeSearchBar.etSearch.text = null
                }
                mBinding.ivDelete -> {
                    mBinding.tvDeleteAll.visible()
                    mBinding.tvComplete.visible()
                    mBinding.ivDelete.gone()
                }
                mBinding.tvComplete -> {
                    mBinding.tvDeleteAll.gone()
                    mBinding.tvComplete.gone()
                    mBinding.ivDelete.visible()
                }
                mBinding.tvDeleteAll -> {
                    AlertDialog.Builder(this@SearchActivity).setMessage("确认删除全部历史记录?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认") { _, _ ->
                            lifecycleScope.launch {
                                DataStoreUtil.removeData(SEARCH_HISTORY, "")
                            }
                            mBinding.flowLayout.gone()
                            mBinding.tvDeleteAll.gone()
                            mBinding.tvComplete.gone()
                            mBinding.ivDelete.visible()
                        }
                        .show()
                }
                mBinding.includeSearchBar.clSearchBar -> {
                    mBinding.includeSearchBar.etSearch.requestFocus()
                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                        mBinding.includeSearchBar.etSearch,
                        0
                    )
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
                getKeyDairy(v.text.toString())                  // 查找数据库
                closeKeyboard(v.windowToken)
                mBinding.tvHistory.gone()                       // 隐藏历史搜索相关控件
                mBinding.ivDelete.gone()
                mBinding.flowLayout.gone()
                if (!searchHistoryString.contains(v.text.toString())) {
                    lifecycleScope.launch {                         // 保存关键词
                        searchHistoryString.insert(0, "${v.text}$$")
                        DataStoreUtil.putData(SEARCH_HISTORY, searchHistoryString.toString())
                    }
                }
            }
            return@setOnEditorActionListener false
        }

        mBinding.includeSearchBar.etSearch.textChangeFlow()
            .debounce(300)
            .flatMapLatest {
                dairyAdapter.refresh()
                viewModel.getKeyDairy(it.toString())
            }
            .flowOn(Dispatchers.IO)
            .onEach {
                mBinding.tvHistory.gone()                       // 隐藏历史搜索相关控件
                mBinding.ivDelete.gone()
                mBinding.flowLayout.gone()
                dairyAdapter.submitData(it)
            }
            .launchIn(lifecycleScope)

    }

    private fun getKeyDairy(key: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getKeyDairy(key).collect {
                dairyAdapter.setDairyKey(key)
                dairyAdapter.submitData(it)
            }
        }
    }

    private fun EditText.textChangeFlow(): Flow<CharSequence> = callbackFlow {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.isNotEmpty()){
                        trySend(it)
                    }else{
                        lifecycleScope.launch {
                            dairyAdapter.submitData(PagingData.empty())
                        }
                    }
                }
            }
        }
        addTextChangedListener(watcher)
        awaitClose { removeTextChangedListener(watcher) }
    }

    companion object {
        private const val SEARCH_HISTORY = "searchHistory"
        private const val MAX_HISTORY_COUNT = 20
    }
}