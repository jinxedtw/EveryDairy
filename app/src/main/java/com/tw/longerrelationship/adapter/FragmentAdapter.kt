package com.tw.longerrelationship.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tw.longerrelationship.views.activity.HomeActivity
import com.tw.longerrelationship.views.fragment.BaseFragment

/**
 * [HomeActivity]的viewPager适配器
 */
class FragmentAdapter(private val mFragments: List<BaseFragment>, fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return mFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }
}