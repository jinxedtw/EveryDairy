package com.tw.longerrelationship.views.activity

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.FavoriteItemAdapter
import com.tw.longerrelationship.databinding.ActivityFavoritesBinding
import com.tw.longerrelationship.help.GridItemDecoration
import com.tw.longerrelationship.logic.model.NotebookItem
import com.tw.longerrelationship.util.InjectorUtils
import com.tw.longerrelationship.util.dp2px
import com.tw.longerrelationship.util.showToast
import com.tw.longerrelationship.viewmodel.FavoritesViewModel

class FavoritesActivity : BaseActivity<ActivityFavoritesBinding>() {

    private lateinit var favoritesItemAdapter: FavoriteItemAdapter
    private val viewModel: FavoritesViewModel by lazy {
        ViewModelProvider(this, InjectorUtils.getFavoritesViewModelFactory()).get(FavoritesViewModel::class.java)
    }
    private val data: List<NotebookItem> = listOf(
        NotebookItem("全部", R.drawable.ic_sticker_0,10),
        NotebookItem("生活", R.drawable.ic_sticker_1,12),
        NotebookItem("日常", R.drawable.ic_sticker_9,14),
        NotebookItem("校园", R.drawable.ic_sticker_11,5),
        NotebookItem("啦啦啦啦啦", R.drawable.ic_sticker_9,3)
    )

    override fun init() {
        initBindingWithAppBar()

        setAppBarTitle("日记本")
        initView()
    }

    private fun initView() {
        favoritesItemAdapter = FavoriteItemAdapter(this, R.layout.item_favorite, data, R.layout.item_favorite_tail)
        favoritesItemAdapter.apply {
            notebookOnClick = {
                showToast("选中分类")
            }
            notebookOnAdd = {
                FavoritesAddActivity.openFavoritesAddActivity(this@FavoritesActivity)
            }
        }

        mBinding.rvFavorites.apply {
            adapter = favoritesItemAdapter
            layoutManager = GridLayoutManager(this@FavoritesActivity, 3)
            addItemDecoration(GridItemDecoration(3, dp2px(12)))
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_favorites

}