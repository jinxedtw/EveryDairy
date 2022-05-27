package com.tw.longerrelationship.views.activity

import android.app.AlertDialog
import android.widget.PopupMenu
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivityDoodleViewBinding
import com.tw.longerrelationship.util.dp2px
import com.tw.longerrelationship.views.widgets.doodle.DoodleView


class DoodleViewActivity : BaseActivity<ActivityDoodleViewBinding>() {
    override fun init() {
        initBinding()
        initView()
    }

    private fun initView() {
        mBinding.ivMore.setOnClickListener {
            showPopupMenu()
        }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, mBinding.ivMore)
        popupMenu.inflate(R.menu.menu_doodle_view)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.main_color -> showColorDialog()
                R.id.main_size -> showSizeDialog()
                R.id.main_action -> showShapeDialog()
                R.id.main_reset -> mBinding.doodleDoodleView.reset()
                R.id.main_save ->  mBinding.doodleDoodleView.saveBitmap()
            }
            true
        }
        popupMenu.show()
    }

    private fun showColorDialog() {
        AlertDialog.Builder(this)
            .setTitle("选择颜色")
            .setSingleChoiceItems(
                arrayOf("蓝色", "红色", "黑色"), 0
            ) { dialog, which ->
                when (which) {
                    0 -> mBinding.doodleDoodleView.setColor("#0000ff")
                    1 -> mBinding.doodleDoodleView.setColor("#ff0000")
                    2 -> mBinding.doodleDoodleView.setColor("#272822")
                    else -> {}
                }
                dialog.dismiss()
            }.create().show()
    }

    private fun showSizeDialog() {
        AlertDialog.Builder(this)
            .setTitle("选择画笔粗细")
            .setSingleChoiceItems(
                arrayOf("细", "中", "粗"), 0
            ) { dialog, which ->
                when (which) {
                    0 -> mBinding.doodleDoodleView.setSize(dp2px(5))
                    1 -> mBinding.doodleDoodleView.setSize(dp2px(10))
                    2 -> mBinding.doodleDoodleView.setSize(dp2px(15))
                    else -> {}
                }
                dialog.dismiss()
            }.create().show()
    }

    private fun showShapeDialog() {
        AlertDialog.Builder(this)
            .setTitle("选择形状")
            .setSingleChoiceItems(
                arrayOf("路径", "直线", "矩形", "圆形", "实心矩形", "实心圆"), 0
            ) { dialog, which ->
                when (which) {
                    0 -> mBinding.doodleDoodleView.setType(DoodleView.ActionType.Path)
                    1 -> mBinding.doodleDoodleView.setType(DoodleView.ActionType.Line)
                    2 -> mBinding.doodleDoodleView.setType(DoodleView.ActionType.Rect)
                    3 -> mBinding.doodleDoodleView.setType(DoodleView.ActionType.Circle)
                    4 -> mBinding.doodleDoodleView.setType(DoodleView.ActionType.FillEcRect)
                    5 -> mBinding.doodleDoodleView.setType(DoodleView.ActionType.FilledCircle)
                    else -> {}
                }
                dialog.dismiss()
            }.create().show()
    }

    override fun getLayoutId(): Int = R.layout.activity_doodle_view
}