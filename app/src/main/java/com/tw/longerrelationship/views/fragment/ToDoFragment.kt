package com.tw.longerrelationship.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.adapter.TodoAdapter
import com.tw.longerrelationship.databinding.FragmentTodoBinding
import com.tw.longerrelationship.viewmodel.MainViewModel
import com.tw.longerrelationship.views.activity.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 待办事项列表
 */
class ToDoFragment : BaseFragment() {
    private lateinit var mBinding: FragmentTodoBinding
    private lateinit var mActivity: HomeActivity
    private lateinit var notCompleteAdapter: TodoAdapter
    private lateinit var completeAdapter: TodoAdapter
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }
    private val linearLayoutManagerNot by lazy {
        object : LinearLayoutManager(activity) {
            override fun onLayoutChildren(              // 这里重写使之监听recyclerView的布局完成,修改UI
                recycler: RecyclerView.Recycler?,
                state: RecyclerView.State?
            ) {
                super.onLayoutChildren(recycler, state)
                viewModel.notCompleteTodoNum.value = notCompleteAdapter.itemCount
            }

            override fun canScrollVertically(): Boolean {       // 解决scroll嵌套的滑动卡顿问题
                return false
            }
        }
    }
    private val linearLayoutManagerOk by lazy {
        object : LinearLayoutManager(activity) {
            override fun onLayoutChildren(              // 这里重写使之监听recyclerView的布局完成,修改UI
                recycler: RecyclerView.Recycler?,
                state: RecyclerView.State?
            ) {
                super.onLayoutChildren(recycler, state)
                viewModel.completeTodoNum.value = completeAdapter.itemCount
            }

            override fun canScrollVertically(): Boolean {       // 解决scroll嵌套的滑动卡顿问题
                return false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTodoBinding.inflate(layoutInflater, container, false)
        mActivity = activity as HomeActivity
        init()
        return super.onCreateView(mBinding.root)
    }

    private fun init() {
        initView()
        getTodoData()
        observer()
    }

    private fun observer() {
        // 设置已完成待办事项和未完成待办事项的相关逻辑
        viewModel.notCompleteTodoNum.observe(viewLifecycleOwner) {
            if (it == 0 && viewModel.completeTodoNum.value == 0) {
                mBinding.ivEmpty.visibility = View.VISIBLE
            } else {
                mBinding.ivEmpty.visibility = View.GONE
            }
        }
        viewModel.completeTodoNum.observe(viewLifecycleOwner) {
            if (it == 0 && viewModel.notCompleteTodoNum.value == 0) {
                mBinding.ivEmpty.visibility = View.VISIBLE
            } else {
                mBinding.ivEmpty.visibility = View.GONE
            }
        }
    }

    private fun initView() {
        mBinding.rvNotComplete.apply {
            notCompleteAdapter = TodoAdapter(mActivity)
            adapter = notCompleteAdapter
            layoutManager = linearLayoutManagerNot
        }
        mBinding.rvComplete.apply {
            completeAdapter = TodoAdapter(mActivity)
            adapter = completeAdapter
            layoutManager = linearLayoutManagerOk
        }
    }

    private fun getTodoData() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getNotCompleteTodoList().collectLatest {
                notCompleteAdapter.submitData(it)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getCompleteTodoList().collectLatest {
                completeAdapter.submitData(it)
            }
        }
    }
}