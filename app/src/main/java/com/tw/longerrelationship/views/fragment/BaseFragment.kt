package com.tw.longerrelationship.views.fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tw.longerrelationship.util.Constants
import com.tw.longerrelationship.util.logD

abstract class BaseFragment : Fragment() {
    val TAG: String = this.javaClass.simpleName

    val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    lateinit var activity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 缓存当前依附的activity
        activity = requireActivity()
        logD(TAG, "onAttach()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD(TAG, "onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logD(TAG, "onCreateView()")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun onCreateView(view: View): View {
        logD(TAG, "onCreateView()")
        return view
    }

    override fun onStart() {
        super.onStart()
        logD(TAG, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        logD(TAG, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        logD(TAG, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        logD(TAG, "onStop()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logD(TAG, "onDestroyView()")
    }

    override fun onDestroy() {
        super.onDestroy()
        logD(TAG, "onDestroy()")
    }

    override fun onDetach() {
        super.onDetach()
        logD(TAG, "onDetach()")
    }
}