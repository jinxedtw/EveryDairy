package com.tw.longerrelationship.views.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.FavoriteAddAdapter
import com.tw.longerrelationship.databinding.ActivityFavoritesAddBinding
import com.tw.longerrelationship.help.GridItemDecoration
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.views.widgets.ToastWithImage

class FavoritesAddActivity : BaseActivity<ActivityFavoritesAddBinding>() {
    private lateinit var favoritesAddAdapter: FavoriteAddAdapter
    private var selectedImageId = -1

    companion object {
        fun openFavoritesAddActivity(activity: Activity) {
            runCatching {
                val intent = Intent(activity, FavoritesAddActivity::class.java)
                activity.startActivity(intent)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    override fun init() {
        initBindingWithAppBar()
        setAppBarTitle("添加日记本")
        addOnSoftKeyBoardVisibleListener()
        initView()
    }

    private fun initView() {
        favoritesAddAdapter = FavoriteAddAdapter(
            this,
            R.layout.item_favorite_add,
            listOf(
                R.drawable.ic_sticker_0,
                R.drawable.ic_sticker_1,
                R.drawable.ic_sticker_2,
                R.drawable.ic_sticker_3,
                R.drawable.ic_sticker_4,
                R.drawable.ic_sticker_5,
                R.drawable.ic_sticker_6,
                R.drawable.ic_sticker_7,
                R.drawable.ic_sticker_8,
                R.drawable.ic_sticker_9,
                R.drawable.ic_sticker_10,
                R.drawable.ic_sticker_11,
            )
        )
        favoritesAddAdapter.notebookCoverOnSelect = {
            selectedImageId = it
        }

        mBinding.rvNotebookCover.apply {
            adapter = favoritesAddAdapter
            addItemDecoration(GridItemDecoration(4, dp2px(10)))
            layoutManager = GridLayoutManager(this@FavoritesAddActivity, 4)
        }

        mBinding.etNotebookName.doOnTextChanged { text, _, _, _ ->
            mBinding.tvNameCount.text = String.format(getString(R.string.add_note_book_num), text?.length ?: 0)
        }

        setOnClickListeners(mBinding.flNotebookName, mBinding.btSave) {
            when (this) {
                mBinding.flNotebookName -> {
                    showKeyboard(mBinding.etNotebookName)
                    mBinding.etNotebookName.requestFocus()
                }
                mBinding.btSave -> {
                    if (mBinding.etNotebookName.text.isNullOrEmpty()){
                        ToastWithImage.showToast("请输入日记本名称",false)
                        return@setOnClickListeners
                    }
                    if (selectedImageId == -1){
                        ToastWithImage.showToast("请选择日记本封面",false)
                        return@setOnClickListeners
                    }
                    ToastWithImage.showToast("添加成功",true)
                }
            }
        }
    }

    /** 监听软键盘状态 */
    private fun addOnSoftKeyBoardVisibleListener() {
        val decorView: View = this.window.decorView
        decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            decorView.getWindowVisibleDisplayFrame(rect)
            // 如果decorView的高度小于原来的80%就说明弹出了软键盘
            if ((rect.bottom - rect.top).toDouble() / decorView.height < 0.8) {
                mBinding.rvNotebookCover.gone()
            } else {
                mBinding.rvNotebookCover.visible()
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_favorites_add
}