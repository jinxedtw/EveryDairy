package com.tw.longerrelationship.views.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.AlbumAdapter
import com.tw.longerrelationship.adapter.AlbumAdapter.Companion.GLIDE_LAYOUT_COUNT
import com.tw.longerrelationship.adapter.ThumbnailAdapter
import com.tw.longerrelationship.databinding.ActivityPhotoAlbumBinding
import com.tw.longerrelationship.help.SpacesItemDecoration
import com.tw.longerrelationship.logic.model.ImageFolder
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.util.Constants.INTENT_ALBUM_SELECT_IMAGES
import com.tw.longerrelationship.util.Constants.INTENT_ALBUM_SELECT_NUM
import com.tw.longerrelationship.util.Constants.INTENT_IMAGE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FilenameFilter


class PhotoAlbumActivity : BaseActivity<ActivityPhotoAlbumBinding>() {
    private val mDirPaths = mutableListOf<String>()                   // 所有图片父路径
    private val mImageFolders = mutableListOf<ImageFolder>()          // 相册集
    private var mImgs = mutableListOf<String>()                       // 所有图片地址信息
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var thumbnailAdapter: ThumbnailAdapter
    private var currentAlbum: Int = 0;                               // 当前选中相册
    private var isAlbumSelectorShow = false
    private lateinit var lastSelectCheckBox: CheckBox                  // 上一个选中的checkBox
    private lateinit var lastSelectImageUrl: String                     // 上一个选中的图片地址
    private val mSelectedImage = arrayListOf<String>()              // 已经选中的图片
    private val mSelectedNum by lazy { intent.getIntExtra(INTENT_ALBUM_SELECT_NUM, 0) }

    /** 跳转图片详情页面 [AlbumImageInfoActivity] */
    private val toAlbumImageInfoLauncher = registerForActivityResult(ToAlbumImageInfoResultContract()) {}

    override fun init() {
        initBinding()
        initView()
    }

    private fun initView() {
        mBinding.btComplete.text = String.format(getString(R.string.album_select_complete), mSelectedNum)
        initRecyclerView()
        getImages()
        setEvent()
    }

    private fun initRecyclerView() {
        mBinding.rvPhotos.layoutManager = GridLayoutManager(this, GLIDE_LAYOUT_COUNT)
        mBinding.rvPhotos.addItemDecoration(SpacesItemDecoration(10))
        mBinding.rvThumbnail.layoutManager = LinearLayoutManager(this)
    }

    private fun setEvent() {
        setOnClickListeners(mBinding.clAppBar, mBinding.ivDropDown, mBinding.viewCover, mBinding.btComplete) {
            when (this) {
                mBinding.clAppBar, mBinding.ivDropDown -> {
                    if (isAlbumSelectorShow) {
                        hideAlbumsSelector()
                    } else {
                        showAlbumsSelector()
                    }
                }
                mBinding.viewCover -> {
                    hideAlbumsSelector()
                }
                mBinding.btComplete -> {
                    val intent = Intent().apply { putStringArrayListExtra(INTENT_ALBUM_SELECT_IMAGES, mSelectedImage) }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

        }
    }

    /** 设置适配器数据 **/
    private fun setAdapterData() {
        if (mImageFolders.isEmpty()) {
            showToast("没有查询到图片")
            return
        }

        // 手机的所有图片
        mImageFolders.forEach {
            mImgs.addAll(it.imageUrl)
        }

        // 增加全部图片项
        mImageFolders.add(0, ImageFolder(mImgs.size, mImageFolders[0].firstImagePath, mImageFolders[0].dir, "全部图片", mImgs))

        albumAdapter = AlbumAdapter(this, mImgs)
        mBinding.rvPhotos.adapter = albumAdapter
        thumbnailAdapter = ThumbnailAdapter(this, R.layout.item_thumbnail, mImageFolders)
        mBinding.rvThumbnail.adapter = thumbnailAdapter

        thumbnailAdapter.onItemClick = { imageFolder, i ->
            if (i == currentAlbum) {
                hideAlbumsSelector()
            } else {
                currentAlbum = i
                setAppBarTitle(imageFolder.name)
                if (i == 0) {
                    albumAdapter.data = mImgs
                } else {
                    albumAdapter.data = imageFolder.imageUrl
                }
                albumAdapter.notifyDataSetChanged()
                hideAlbumsSelector()
            }
        }

        albumAdapter.onItemClick = { url, checkBox ->
            val bundle: Bundle = Bundle().apply {
                putString(INTENT_IMAGE_URL, url)
            }
            toAlbumImageInfoLauncher.launch(bundle)
            lastSelectCheckBox = checkBox
            lastSelectImageUrl = url
        }
        albumAdapter.onCheckBoxClick = { url, checkState ->
            onCheckNumChanged(checkState, url)
        }
    }

    //获取图片的路径和父路径 及 图片size
    private fun getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            showToast("检测到没有内存卡")
            return
        }
        lifecycleScope.launch {
            runTimeLog(message = "搜索数据库") {
                withContext(Dispatchers.IO) {
                    val mCursor = contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=?", arrayOf("image/jpeg", "image/png", "image/jpg"),
                        MediaStore.Images.Media.DATE_TAKEN + " DESC"
                    ) //获取图片的cursor
                    while (mCursor!!.moveToNext()) {
                        val path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)) // 获取第一张图片的路径
                        val parentFile = File(path).parentFile ?: continue
                        val parentPath = parentFile.absolutePath                                           // 获取图片的文件夹信息
                        var imageFolder: ImageFolder

                        // 缓存一份,提高查询速度
                        if (mDirPaths.contains(parentPath)) {
                            continue
                        } else {
                            mDirPaths.add(parentPath)                            //将父路径添加到集合中
                            imageFolder = ImageFolder(firstImagePath = path, dir = parentPath, name = parentFile.name)
                        }
                        // TODO: 2021/12/13 这个方法耗时大概2s,需要进行优化
                        imageFolder.imageUrl = parentFile.listFiles(getFileFilterImage())!!.map {
                            it.absolutePath
                        }.reversed()
                        imageFolder.count = imageFolder.imageUrl.size           //传入每个相册的图片个数
                        mImageFolders.add(imageFolder)                          //添加每一个相册
                    }
                    mCursor.close()
                }
            }
            mBinding.progressBar.gone()
            // 更新操作
            setAdapterData()
        }
    }

    /** 图片筛选器，过滤无效图片 */
    private fun getFileFilterImage(): FilenameFilter {
        return FilenameFilter { _, filename ->
            (filename.endsWith(".jpg")
                    || filename.endsWith(".png")
                    || filename.endsWith(".jpeg")
                    || filename.endsWith(".gif"))
        }
    }

    private fun showAlbumsSelector() {
        isAlbumSelectorShow = true

        startValueAnim(0, selectorHeight.toInt(), {
            val lp: ViewGroup.LayoutParams = mBinding.rvThumbnail.layoutParams
            val size: Int = Integer.valueOf(it.animatedValue.toString())
            lp.height = size
            mBinding.rvThumbnail.layoutParams = lp
            mBinding.rvThumbnail.requestLayout()
            mBinding.viewCover.requestLayout()
        }, onAnimStart = {
            mBinding.viewCover.visible()
        })

        ObjectAnimator.ofFloat(mBinding.ivDropDown, "rotation", 0f, 180f).start()
    }

    private fun hideAlbumsSelector() {
        isAlbumSelectorShow = false

        startValueAnim(selectorHeight.toInt(), 0, {
            val lp: ViewGroup.LayoutParams = mBinding.rvThumbnail.layoutParams
            val size: Int = Integer.valueOf(it.animatedValue.toString())
            lp.height = size
            mBinding.rvThumbnail.layoutParams = lp
            mBinding.rvThumbnail.requestLayout()
            mBinding.viewCover.requestLayout()
        }, onAnimEnd = {
            mBinding.viewCover.gone()
        })

        ObjectAnimator.ofFloat(mBinding.ivDropDown, "rotation", 180f, 0f).start()
    }

    /**
     * 当有新的图片被选中或者取消选中时调用此方法
     * @param isChecked 是否被选中
     * @param url 操作的图片地址
     */
    private fun onCheckNumChanged(isChecked: Boolean, url: String) {
        if (isChecked && mSelectedImage.size + mSelectedNum >= 9) {
            showToast("最多选中9张图片")
            return
        }

        if (isChecked) {
            if (!mSelectedImage.contains(url)) {
                mSelectedImage.add(url)
            }
        } else {
            mSelectedImage.remove(url)
        }
        mBinding.btComplete.text = String.format(getString(R.string.album_select_complete), mSelectedImage.size + mSelectedNum)
    }

    override fun getLayoutId(): Int = R.layout.activity_photo_album

    inner class ToAlbumImageInfoResultContract : ActivityResultContract<Bundle, Unit>() {
        override fun createIntent(context: Context, input: Bundle): Intent {
            return Intent(context, AlbumImageInfoActivity::class.java).apply {
                putExtras(input)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?) {
        }
    }

    companion object {
        val selectorHeight = getScreenHeight() * 0.52
    }
}