package com.tw.longerrelationship.views.activity

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.PictureItemAdapter
import com.tw.longerrelationship.databinding.ActivityAlbumBinding
import com.tw.longerrelationship.util.InjectorUtils
import com.tw.longerrelationship.viewmodel.AlbumViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 图库 */
class AlbumActivity : BaseActivity<ActivityAlbumBinding>() {

    private lateinit var pictureItemAdapter: PictureItemAdapter

    private val viewModel: AlbumViewModel by lazy {
        ViewModelProvider(this, InjectorUtils.getAlbumViewModelFactory()).get(AlbumViewModel::class.java)
    }

    override fun init() {
        initBindingWithAppBar("图库")
        initView()
    }

    private fun initView() {

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllDairyPicture()

            runOnUiThread {
                pictureItemAdapter = PictureItemAdapter(this@AlbumActivity, R.layout.item_picture_list, viewModel.pictureMap.keys.toList(), viewModel.pictureMap)
                mBinding.rvPicture.layoutManager = LinearLayoutManager(this@AlbumActivity)
                mBinding.rvPicture.adapter = pictureItemAdapter
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_album
}