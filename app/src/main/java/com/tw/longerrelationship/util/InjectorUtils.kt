package com.tw.longerrelationship.util

import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.logic.AppDataBase
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.logic.viewModelFactory.DairyEditViewModelFactory
import com.tw.longerrelationship.logic.viewModelFactory.DairyInfoViewModelFactory
import com.tw.longerrelationship.logic.viewModelFactory.MainViewModelFactory
import com.tw.longerrelationship.logic.viewModelFactory.PictureInfoViewModelFactory
import com.tw.longerrelationship.logic.viewModelFactory.SearchViewModelFactory

/**
 * 用于注入各种活动和片段所需的类的静态方法
 *
 * Activity和Fragment通过该类获取到ViewModel工厂
 */
object InjectorUtils {

    private fun getMainRepository() = MainRepository.getInstance(
        AppDataBase.getInstance(MyApplication.context).dairyDao(),
    )

    fun getMainViewModelFactory() = MainViewModelFactory(getMainRepository())

    fun getDairyEditViewModelFactory(dairyId: Int) =
        DairyEditViewModelFactory(getMainRepository(), dairyId)

    fun getPictureInfoViewModelFactory() = PictureInfoViewModelFactory()

    fun getDairyInfoViewModelFactory(dairyId: Int) = DairyInfoViewModelFactory(getMainRepository(), dairyId)

    fun getSearchViewModelFactory()= SearchViewModelFactory(getMainRepository())
}