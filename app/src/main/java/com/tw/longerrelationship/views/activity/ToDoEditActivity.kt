package com.tw.longerrelationship.views.activity

import androidx.databinding.DataBindingUtil
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivityToDoEditBinding
import com.tw.longerrelationship.views.fragment.ToDoFragment

/**
 * [ToDoFragment]点击编辑跳转至该activity
 */
class ToDoEditActivity : BaseActivity() {
    private lateinit var mBinding: ActivityToDoEditBinding

    override fun init() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
    }

    override fun getLayoutId(): Int = R.layout.activity_to_do_edit
}