package com.tw.longerrelationship.views.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import com.haibin.calendarview.CalendarView.OnYearChangeListener
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.DairyAdapter
import com.tw.longerrelationship.databinding.ActivityCalendarBinding
import com.tw.longerrelationship.util.InjectorUtils
import com.tw.longerrelationship.util.setOnClickListeners
import com.tw.longerrelationship.viewmodel.CalendarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class CalendarActivity : BaseActivity<ActivityCalendarBinding>(), OnCalendarSelectListener, OnYearChangeListener {
    private var mYear = 0
    private var dairyAdapter: DairyAdapter = DairyAdapter(this, isHomeActivity = false)

    private val viewModel: CalendarViewModel by lazy {
        ViewModelProvider(this, InjectorUtils.getCalendarViewModelFactory()).get(CalendarViewModel::class.java)
    }

    override fun init() {
        initBinding()
        initView()
        initData()
    }


    @SuppressLint("SetTextI18n")
    private fun initView() {
        setOnClickListeners(mBinding.tvMonthDay, mBinding.flCurrent) {
            when (this) {
                mBinding.tvMonthDay -> {
                    if (!mBinding.calendarLayout.isExpand) {
                        mBinding.calendarLayout.expand()
                        return@setOnClickListeners
                    }
                    mBinding.calendarView.showYearSelectLayout(mYear)
                    mBinding.tvLunar.visibility = View.GONE
                    mBinding.tvYear.visibility = View.GONE
                    mBinding.tvMonthDay.text = mYear.toString()
                }
                mBinding.flCurrent -> {
                    mBinding.calendarView.scrollToCurrent()
                }
            }
        }

        mBinding.calendarView.setOnCalendarSelectListener(this)
        mBinding.calendarView.setOnYearChangeListener(this)

        mYear = mBinding.calendarView.curYear
        mBinding.tvYear.text = mBinding.calendarView.curYear.toString()
        mBinding.tvMonthDay.text = mBinding.calendarView.curMonth.toString() + "月" + mBinding.calendarView.curDay + "日"
        mBinding.tvLunar.text = "今日"
        mBinding.tvCurrentDay.text = mBinding.calendarView.curDay.toString()

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CalendarActivity)
            adapter = dairyAdapter
        }
    }

    private fun initData() {
        getSelectDayDiary((Date().time+ 28800000) / 86_400_000)

        lifecycleScope.launch(Dispatchers.IO) {
            val dateList = viewModel.getAllDairyDate()
            val map: MutableMap<String, Calendar> = HashMap()
            var calendar: Calendar

            dateList.forEach {
                java.util.Calendar.getInstance().apply {
                    time = it
                    calendar = getSchemeCalendar(
                        get(java.util.Calendar.YEAR),
                        get(java.util.Calendar.MONTH) + 1,
                        get(java.util.Calendar.DAY_OF_MONTH),
                        ContextCompat.getColor(this@CalendarActivity, R.color.colorPrimary_70)
                    )
                    map[calendar.toString()] = calendar
                }
            }
            mBinding.calendarView.setSchemeDate(map)
        }
    }

    private fun getSchemeCalendar(year: Int, month: Int, day: Int, color: Int): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.schemeColor = color //如果单独标记颜色、则会使用这个颜色
        return calendar
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
    }


    @SuppressLint("SetTextI18n")
    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        mBinding.tvLunar.visibility = View.VISIBLE
        mBinding.tvYear.visibility = View.VISIBLE
        mBinding.tvMonthDay.text = calendar.month.toString() + "月" + calendar.day + "日"
        mBinding.tvYear.text = calendar.year.toString()
        mBinding.tvLunar.text = calendar.lunar
        mYear = calendar.year
        getSelectDayDiary((calendar.timeInMillis + 28800000) / 86400000)
    }

    override fun onYearChange(year: Int) {
        mBinding.tvMonthDay.text = year.toString()
    }

    override fun getLayoutId(): Int = R.layout.activity_calendar

    private fun getSelectDayDiary(day: Long) {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getDiaryByDate(day).collect {
                dairyAdapter.submitData(it)
            }
        }
    }
}