package com.tw.longerrelationship.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tw.longerrelationship.databinding.FragmentTodoBinding
import com.tw.longerrelationship.views.activity.MainActivity

/**
 * 待办事项列表
 */
class ToDoFragment : BaseFragment() {
    private lateinit var mBinding: FragmentTodoBinding
    private lateinit var mActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTodoBinding.inflate(layoutInflater, container, false)
        mActivity = activity as MainActivity
        init()
        return super.onCreateView(mBinding.root)
    }

    private fun init() {
    }
}