package com.tw.longerrelationship.views.activity

import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.tw.longerrelationship.MyApplication.Companion.context
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivityToDoEditBinding
import com.tw.longerrelationship.logic.model.EmergencyLevel
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.viewmodel.ToDoEditViewModel
import com.tw.longerrelationship.views.fragment.ToDoFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * [ToDoFragment]点击编辑跳转至该activity
 */
class ToDoEditActivity : BaseActivity() {
    private lateinit var mBinding: ActivityToDoEditBinding
    private val todoId by lazy {
        intent.getIntExtra(TODO_ID, -1)
    }
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getToDoEditViewModelFactory(todoId)
        ).get(ToDoEditViewModel::class.java)
    }

    override fun init(): View {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.viewModel = viewModel
        initView()
        initEditText()
        observer()
        return mBinding.root
    }

    private fun observer() {
        viewModel.todoContent.observe(this) {
            mBinding.tvContentNum.text =
                String.format(getString(R.string.todo_num), it?.length ?: 0)
        }

        viewModel.toDoData.observe(this) {
            viewModel.apply {
                createTime = it.createTime
                todoContent.value = it.content
            }
            mBinding.apply {
                etTodo.setText(it.content)
                etTodo.setSelection(it.content.length)
            }
            setTodoType(it.emergencyLevel.level)
        }
    }

    private fun initView() {
        mBinding.etTodo.requestFocus()

        mBinding.commonBar.setRightClickAction {
            if (viewModel.todoContent.value == null) {
                showToast(this, "请输入事项内容")
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.saveToDo()
                }
                finishAndTryCloseSoftKeyboard()
            }
        }

        setOnClickListeners(mBinding.tvSelectTodo, mBinding.ivArrowDown) {
            showPopupMenu()
        }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, mBinding.ivArrowDown)
        popupMenu.inflate(R.menu.menu_emergence_level)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            setTodoType(
                when (menuItem.itemId) {
                    R.id.importantAndUrgent -> 4
                    R.id.importantNoUrgent -> 3
                    R.id.noImportantUrgent -> 2
                    R.id.noImportantNoUrgent -> 1
                    else -> 0
                }
            )
        }
        popupMenu.show()
    }

    private fun initEditText() {
        mBinding.etTodo.doOnTextChanged { text, _, _, _ ->
            viewModel.todoContent.value = text.toString()
        }
    }

    /**
     * 设置该待办事项的待办类型
     */
    private fun setTodoType(type: Int): Boolean {
        when (type) {
            4 -> {
                mBinding.tvSelectTodo.text = "重要紧急"
                mBinding.tvSelectTodo.setColorForText(R.color.importantAndUrgent)
                mBinding.etTodo.background = ContextCompat.getDrawable(
                    context,
                    (R.drawable.shape_todo_edit_bg_level4)
                )
                viewModel.emergencyLevel = EmergencyLevel.ImportantAndUrgent
                return true
            }
            3 -> {
                mBinding.tvSelectTodo.text = "重要不紧急"
                mBinding.tvSelectTodo.setColorForText(R.color.importantAndNoUrgent)
                mBinding.etTodo.background = ContextCompat.getDrawable(
                    context,
                    (R.drawable.shape_todo_edit_bg_level3)
                )
                viewModel.emergencyLevel = EmergencyLevel.ImportantNoUrgent
                return true
            }
            2 -> {
                mBinding.tvSelectTodo.text = "不重要紧急"
                mBinding.tvSelectTodo.setColorForText(R.color.noImportantAndUrgent)
                mBinding.etTodo.background = ContextCompat.getDrawable(
                    context,
                    (R.drawable.shape_todo_edit_bg_level2)
                )
                viewModel.emergencyLevel = EmergencyLevel.NoImportantUrgent
                return true
            }
            1 -> {
                mBinding.tvSelectTodo.text = "不重要不紧急"
                mBinding.tvSelectTodo.setColorForText(R.color.noImportantAndNoUrgent)
                mBinding.etTodo.background = ContextCompat.getDrawable(
                    context,
                    (R.drawable.shape_todo_edit_bg_level1)
                )
                viewModel.emergencyLevel = EmergencyLevel.NoImportantNoUrgent
                return true
            }
        }
        return false
    }


    override fun getLayoutId(): Int = R.layout.activity_to_do_edit
}