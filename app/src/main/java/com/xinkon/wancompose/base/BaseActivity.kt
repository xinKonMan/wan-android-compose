package com.xinkon.wancompose.base

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.BarUtils
import com.xinkon.wancompose.utils.logi

/**
 * Created by linmaoxin on 2022/3/28
 */
abstract class BaseActivity<VB : ViewBinding>(private val inflate: (LayoutInflater) -> VB) :
    AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
    }

    lateinit var binding: VB
    private val simpleName by lazy { javaClass.simpleName }
    var isPaused = false
        private set

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        logi(TAG, "$simpleName -> onCreate")
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)

        if (fullScreenWindow()) {
            val decorView = window.decorView //布局显示在状态栏上面
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT

            getToolbarView().takeIf { it != null }?.also {
                runCatching {
                    (it.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                        BarUtils.getStatusBarHeight()
                }
            }
        }
        window.navigationBarColor = getNavigationColor()
        setContentView(binding.root)
    }


    /**设置状态栏颜色
     * @param isBlackColor 是否黑色 否则白色
     * */
    fun statusBarColor(isBlackColor: Boolean = false) {
        val root = binding.root
        val windowInsetsController = ViewCompat.getWindowInsetsController(root)
        windowInsetsController?.isAppearanceLightStatusBars = isBlackColor
    }

    /**
     * 修改状态栏字体颜色（原生）
     */
    fun setStatusBarFontColor(activity: Activity, isDark: Boolean) {
        var decorView = activity.window.decorView
        if (isDark)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        else
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    @CallSuper
    override fun onStart() {
        logi(TAG, "$simpleName -> onStart")
        super.onStart()
    }

    @CallSuper
    override fun onResume() {
        logi(TAG, "$simpleName -> onResume")
        isPaused = false
        super.onResume()
    }

    @CallSuper
    override fun onPause() {
        logi(TAG, "$simpleName -> onPause")
        isPaused = true
        super.onPause()
    }

    @CallSuper
    override fun onDestroy() {
        logi(TAG, "$simpleName -> onDestroy")
        super.onDestroy()
    }

    @CallSuper
    override fun onNewIntent(intent: Intent?) {
        logi(TAG, "$simpleName -> onNewIntent")
        super.onNewIntent(intent)
    }

    open fun getToolbarView(): View? = null

    @ColorInt
    open fun getNavigationColor(): Int = Color.WHITE

    /**是否全屏*/
    open fun fullScreenWindow(): Boolean = true

    /**全屏情况下view是否需要padding*/
    open fun isStatusBarsBlackColor(): Boolean = false
}

fun FragmentActivity.isGrantPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Fragment.isGrantPermission(permission: String) =
    ContextCompat.checkSelfPermission(this.requireContext(),
        permission) == PackageManager.PERMISSION_GRANTED

fun FragmentActivity.isShouldShowRequestPermissionRationale(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun Fragment.isShouldShowRequestPermissionRationale(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)
