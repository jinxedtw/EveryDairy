package com.tw.longerrelationship.util

import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.logic.AppDataBase
import com.tw.longerrelationship.logic.network.TotalNetwork
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.viewmodel.viewModelFactory.*

/**
 * 用于注入各种活动和片段所需的类的静态方法
 *
 * Activity和Fragment通过该类获取到ViewModel工厂
 */
object InjectorUtils {

    private fun getMainRepository() = MainRepository.getInstance(
        AppDataBase.getInstance(MyApplication.appContext).dairyDao(),
        AppDataBase.getInstance(MyApplication.appContext).toDoDao(),
        TotalNetwork.getInstance()
    )

    fun getMainViewModelFactory() = MainViewModelFactory(getMainRepository())

    fun getDairyEditViewModelFactory(dairyId: Int) =
        DairyEditViewModelFactory(getMainRepository(), dairyId)

    fun getPictureInfoViewModelFactory() = PictureInfoViewModelFactory()

    fun getDairyInfoViewModelFactory(dairyId: Int) = DairyInfoViewModelFactory(getMainRepository(), dairyId)

    fun getSearchViewModelFactory() = SearchViewModelFactory(getMainRepository())

    fun getToDoEditViewModelFactory(todoId: Int) = ToDoEditViewModelFactory(getMainRepository(), todoId)

    fun getAlbumViewModelFactory() = AlbumViewModelFactory(getMainRepository())
}