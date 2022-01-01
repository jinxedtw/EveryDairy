package com.tw.longerrelationship.views.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.DairyAdapter
import com.tw.longerrelationship.databinding.FragmentNoteBinding
import com.tw.longerrelationship.viewmodel.MainViewModel
import com.tw.longerrelationship.views.activity.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * 笔记
 */
class DairyFragment : BaseFragment() {
    private lateinit var mBinding: FragmentNoteBinding
    private lateinit var dairyAdapter: DairyAdapter
    private lateinit var mActivity: HomeActivity
    private val linearLayoutManager by lazy {
        object : LinearLayoutManager(activity) {
            override fun onLayoutChildren(              // 这里重写使之监听recyclerView的布局完成,修改UI
                recycler: RecyclerView.Recycler?,
                state: RecyclerView.State?
            ) {
                super.onLayoutChildren(recycler, state)
                viewModel.dairyNum.value = dairyAdapter.itemCount
            }
        }
    }
    private val gridLayoutManager by lazy {
        object : StaggeredGridLayoutManager(2, VERTICAL) {
            override fun onLayoutChildren(
                recycler: RecyclerView.Recycler?,
                state: RecyclerView.State?
            ) {
                super.onLayoutChildren(recycler, state)
                viewModel.dairyNum.value = dairyAdapter.itemCount
            }
        }
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mActivity = activity as HomeActivity
        dairyAdapter = DairyAdapter(mActivity)
        mBinding = FragmentNoteBinding.inflate(layoutInflater, container, false)
        init()
        return super.onCreateView(mBinding.root)
    }

    private fun init() {
        initView()
        observe()
        getDairyData()
    }

    private fun observe() {
        viewModel.isFold.observe(viewLifecycleOwner) {
            mBinding.rvDairy.adapter = dairyAdapter
            changeRecyclerView()
            if (this.isResumed) {
                dairyAdapter.notifyDataSetChanged()
            }
        }
        dairyAdapter.checkedNum.observe(viewLifecycleOwner) {
            mActivity.setSelectNum(it)
        }
        viewModel.ifEnterCheckBoxType.observe(viewLifecycleOwner) {
            enterOrExitCheckBoxType(it)
        }
        viewModel.dairyNum.observe(viewLifecycleOwner) {
            if (it == 0) {
                mBinding.ivEmpty.visibility = View.VISIBLE
            } else {
                mBinding.ivEmpty.visibility = View.GONE
            }
            mBinding.root.findViewById<TextView>(R.id.tv_center_text).text =
                String.format("共${it}篇")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        // TODO 取消下拉刷新,后期可以在这里增加上传至服务器的操作
//        mBinding.smartRefresh.apply {
//            isEnableLoadmore = false    //是否启用上拉加载功能
//            isEnableRefresh = false
//            setOnRefreshListener {      // 设置下拉刷新
//                finishRefresh()
//            }
//        }
    }

    private fun changeRecyclerView() {
        val layoutAnimationController: LayoutAnimationController
        val animation: Animation

        // 为两种模式分别指定不同的入场动画
        if (viewModel.isFold.value!!) {
            mBinding.rvDairy.apply {
                layoutManager = linearLayoutManager
                (adapter as DairyAdapter).type = 1
            }
            animation = AnimationUtils.loadAnimation(context, R.anim.anim_scroll_to_right)
            layoutAnimationController = LayoutAnimationController(animation)
            layoutAnimationController.order = LayoutAnimationController.ORDER_NORMAL
        } else {
            mBinding.rvDairy.apply {
                layoutManager = gridLayoutManager
                (adapter as DairyAdapter).type = 2
            }
            animation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_in_center)
            layoutAnimationController = LayoutAnimationController(animation)
            layoutAnimationController.order = LayoutAnimationController.ORDER_NORMAL
        }

        mBinding.rvDairy.layoutAnimation = layoutAnimationController
    }

    /**
     * 进入或退出选择模式
     * @param boolean 是否进入选择模式
     */
    private fun enterOrExitCheckBoxType(boolean: Boolean) {
        dairyAdapter.isShowBox = boolean
        dairyAdapter.notifyDataSetChanged()
    }

    private fun getDairyData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.getAllDairy().collect {
                    dairyAdapter.submitData(it)
                }
            }
        }
    }

    fun deleteDairy() {
        dairyAdapter.checkBoxMap.forEach {
            if (it.value) {
                viewModel.deleteDairy(dairyAdapter.getDairyItem(it.key)?.id!!)
            }
        }
    }

    fun selectAll() {
        dairyAdapter.selectAll()
        dairyAdapter.notifyDataSetChanged()
    }
}