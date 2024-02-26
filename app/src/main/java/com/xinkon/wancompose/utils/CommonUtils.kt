package com.xinkon.wancompose.utils

import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.annotation.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import com.xinkon.wancompose.WanApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.regex.Pattern
import kotlin.math.roundToInt

/**
 * Created by linmaoxin on 2021/3/16
 */
val String.Companion.EMPTY: String get() = ""
val String.Companion.SPACE_SEPARATOR: String get() = " "
val String.Companion.ZERO: String get() = "0"
val String.Companion.AssetPreFix: String get() = "file:///android_asset/"

fun String.isFileExit() = !isNullOrEmpty() && FileUtils.isFileExist(this)
fun String.isAssetFile() = !isNullOrEmpty() && (startsWith("file:///android_asset"))
fun String.getAssetPath() = substring("file:///android_asset/".length, length)
fun String.asFile(): File = File(this)

val numericPattern: Pattern by lazy { Pattern.compile("^[-\\+]?[\\d]*$") }
fun String.isNumeric(): Boolean = run {
    takeIf { !isNullOrEmpty() }?.run { numericPattern.matcher(this).matches() } ?: false
}

fun isMainThread() = Thread.currentThread() == Looper.getMainLooper().thread

fun postMainRun(command: Runnable) {
    ContextCompat.getMainExecutor(getAppContext()).execute(command)
}

fun String.toIntSafe(def: Int = 0): Int = toIntOrNull() ?: def

@JvmOverloads
fun showProgressDialog(
    context: Context,
    cancelable: Boolean = true,
    dialog: (ProgressDialog.() -> Unit)?,
): ProgressDialog = ProgressDialog(context).apply {
    setCancelable(cancelable)
    dialog?.invoke(this)
    show()
}

inline fun <reified T : Activity> Fragment.startActivity(
    noinline intentUnit: (Intent.() -> Unit)? = null,
) {
    requireContext().also {
        val intent = Intent(it, T::class.java)
        intentUnit?.invoke(intent)
        startActivity(intent)
    }
}

inline fun <reified T : Activity> Fragment.startActivityForResult(
    requestCode: Int,
    noinline intentUnit: (Intent.() -> Unit)? = null,
) {
    requireContext().also {
        val intent = Intent(it, T::class.java)
        intentUnit?.invoke(intent)
        startActivityForResult(intent, requestCode)
    }
}


@JvmOverloads
inline fun <reified T : Activity> Context.startActivity(
    noinline intentUnit: (Intent.() -> Unit)? = null,
) {
    val intent = Intent(this, T::class.java)
    intentUnit?.invoke(intent)
    startActivity(intent)
}

@JvmOverloads
inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    noinline intentUnit: (Intent.() -> Unit)? = null,
) {
    val intent = Intent(this, T::class.java)
    intentUnit?.invoke(intent)
    startActivityForResult(intent, requestCode)
}

public fun registerReceiver(
    context: Context,
    receiver: BroadcastReceiver,
    vararg filterAction: String,
) = try {
    context.registerReceiver(receiver,
        IntentFilter().apply { filterAction.forEach { addAction(it) } })
} catch (e: Exception) {
}

public fun unregisterReceiver(context: Context, receiver: BroadcastReceiver) = try {
    context.unregisterReceiver(receiver)
} catch (e: Exception) {
}

/**
 * 跳转到某个外部链接
 */
fun openExternalBrowser(context: Context, url: String) {
    Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(this)
    }
}

/**
 * 跳转到应用商店
 */
fun gotoMarket(context: Context) {
    val uri = Uri.parse("market://details?id=${getPackageName()}")
    val goToMarket = Intent("android.intent.action.VIEW", uri)
    try {
        if (!(context is Activity)) {
            goToMarket.addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(goToMarket)
    } catch (exception: ActivityNotFoundException) {
        exception.printStackTrace()
    }
}

fun openAppSettingIntent() {
    try {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).run {
            data = Uri.parse("package:${getAppContext().packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            getAppContext().startActivity(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun createSendActionIntent() = Intent().apply { action = Intent.ACTION_SEND }
fun sendImageAction(context: Context, uri: Uri, content: String? = null) {
    createSendActionIntent().apply {
        type = "image/*"
        putExtra(Intent.EXTRA_TEXT, content)
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(this)
    }
}

fun sendImageAction(context: Context, path: String, content: String? = null) {
    sendImageAction(context, Uri.parse(path), content)
}

fun checkPermissionGranted(permission: String): Boolean = ContextCompat.checkSelfPermission(
    getAppContext(),
    permission
) == PackageManager.PERMISSION_GRANTED

inline fun tryCatchBlock(block: () -> Unit, noinline catch: ((Exception) -> Unit)? = null) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        catch?.invoke(e)
    }
}

fun registerEventBus(obj: Any?) {
    obj ?: return
    if (!EventBus.getDefault().isRegistered(obj)) {
        EventBus.getDefault().register(obj)
    }
}

fun unregisterEventBus(obj: Any?) {
    obj ?: return
    if (EventBus.getDefault().isRegistered(obj)) {
        EventBus.getDefault().unregister(obj)
    }
}

fun postEvent(obj: Any) {
    EventBus.getDefault().post(obj)
}

fun postSticky(obj: Any) {
    EventBus.getDefault().postSticky(obj)
}

fun removeStick(obj: Any?) {
    EventBus.getDefault().removeStickyEvent(obj)
}

interface EventInterface<T> {
    fun onEvent(event: T)
}

var View.topMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).topMargin
    set(value) {
        val params = this.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = value
        this.layoutParams = params
    }

var View.bottomMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    set(value) {
        val params = this.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = value
        this.layoutParams = params
    }

var View.endMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
    set(value) {
        val params = this.layoutParams as ViewGroup.MarginLayoutParams
        params.marginEnd = value
        this.layoutParams = params
    }
var View.startMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).marginStart
    set(value) {
        val params = this.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = value
        this.layoutParams = params
    }

var View.layoutHeight: Int
    get() = (this.layoutParams as ViewGroup.LayoutParams).height
    set(value) {
        val params = this.layoutParams as ViewGroup.LayoutParams
        params.height = value
        this.layoutParams = params
    }
var View.layoutWidth: Int
    get() = (this.layoutParams as ViewGroup.LayoutParams).width
    set(value) {
        val params = this.layoutParams as ViewGroup.LayoutParams
        params.width = value
        this.layoutParams = params
    }

fun ImageView.addColorFilter(@ColorInt color: Int) {
    clearColorFilter()
    setColorFilter(color, PorterDuff.Mode.SRC_IN)
}

fun Int.percent(p: Float, max: Int= Int.MAX_VALUE, min:Int = Int.MIN_VALUE):Int {
    return (this * p).roundToInt().coerceAtMost(max).coerceAtLeast(min)
}


fun getAppContext(): Context = WanApplication.getAppContext()
fun getAppContentResolver(): ContentResolver = getAppContext().contentResolver
fun getResString(@StringRes id: Int) = getAppContext().getString(id)
fun getResInteger(@IntegerRes id: Int) = getAppContext().resources.getInteger(id)
fun getResDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(getAppContext(), id)
fun getResColor(@ColorRes id: Int) = ContextCompat.getColor(getAppContext(), id)
fun getResDimension(@DimenRes id: Int) = getAppContext().resources.getDimension(id)
fun getDipSize(dip: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
    dip,
    getAppContext().resources.displayMetrics).toInt()

fun getDipSizeF(dip: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
    dip,
    getAppContext().resources.displayMetrics)

fun getDipSize(dip: Int): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
    dip.toFloat(),
    getAppContext().resources.displayMetrics).toInt()
fun getPxSize(dip:Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,dip.toFloat(),
    getAppContext().resources.displayMetrics)

fun getSpSize(sp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
    sp,
    getAppContext().resources.displayMetrics).toInt()

fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.isShow() = visibility == View.VISIBLE
fun View.isGone() = visibility == View.GONE
fun View.isInVisible() = visibility == View.INVISIBLE
fun Float.dp(context: Context = getAppContext()): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics)

fun Int.dp(context: Context = getAppContext()): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics)

fun Int.px(): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
    this.toFloat(),
    getAppContext().resources.displayMetrics)
fun Float.px(): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
    this,
    getAppContext().resources.displayMetrics)
fun Int.dp2px(): Int {
    val scale = getAppContext().resources.displayMetrics.density
    return this*scale.roundToInt()
}

fun Int.px2dp(): Int {
    val scale = getAppContext().resources.displayMetrics.density
    return this / scale.roundToInt()
}

inline val Int.dp:Float get() = this.dp()
inline val Int.px:Float get() = this.px()
inline val Float.dp:Float get() = this.dp()
inline val Float.px:Float get() = this.px()
//返回对应的毫秒
inline val Int.seconds:Long get() = this * 1000L
inline val Int.minus:Long get() = 1.seconds * 60 * this
inline val Int.hours:Long get() = 1.minus * 60 * this
inline val Int.days:Long get() = 1.hours * 24 * this

@SuppressLint("Recycle")
fun View.alphaAnimation(
    start: Float, end: Float, duration: Long,
    interpolator: TimeInterpolator? = null,
    listener: AnimatorListenerAdapter? = null,
): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, "alpha", start, end).apply {
        interpolator?.also { this.interpolator = it }
        listener?.also { this.addListener(it) }
        this.duration = duration
    }
}

@SuppressLint("Recycle")
fun View.translationYAnimation(
    start: Float, end: Float, duration: Long,
    interpolator: TimeInterpolator? = null,
    listener: AnimatorListenerAdapter? = null,
    valueListener: ValueAnimator.AnimatorUpdateListener? = null,
): ObjectAnimator {
    return translationAnimation("translationY",
        start,
        end,
        duration,
        interpolator,
        listener,
        valueListener)
}

@SuppressLint("Recycle")
fun View.translationXAnimation(
    start: Float, end: Float, duration: Long,
    interpolator: TimeInterpolator? = null,
    listener: AnimatorListenerAdapter? = null,
    valueListener: ValueAnimator.AnimatorUpdateListener? = null,
): ObjectAnimator {
    return translationAnimation("translationXAnimation",
        start,
        end,
        duration,
        interpolator,
        listener,
        valueListener)
}

@SuppressLint("Recycle")
fun View.translationAnimation(
    propertyName: String, start: Float, end: Float, duration: Long,
    interpolator: TimeInterpolator? = null,
    listener: AnimatorListenerAdapter? = null,
    valueListener: ValueAnimator.AnimatorUpdateListener? = null,
): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, propertyName, start, end).apply {
        interpolator?.also { this.interpolator = it }
        listener?.also { this.addListener(it) }
        valueListener?.also { this.addUpdateListener(it) }
        this.duration = duration
    }
}

fun isSupportRtl(): Boolean {
    return (getAppContext().applicationInfo.flags and ApplicationInfo.FLAG_SUPPORTS_RTL) != 0
    // TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == AppCompatImageView.LAYOUT_DIRECTION_RTL
}

fun View.isRTL(): Boolean = layoutDirection == View.LAYOUT_DIRECTION_RTL

fun getPackageName() = getAppContext().packageName!!
fun getPackageVersionCode() = com.blankj.utilcode.util.AppUtils.getAppVersionCode()
fun getCurVersion(context: Context): Int {
    try {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return 0
}

fun View.applyParams(apply: (ConstraintLayout.LayoutParams) -> Boolean) {
    val lp = layoutParams as ConstraintLayout.LayoutParams
    if (apply(lp)) {
        layoutParams = lp
    }
}

fun Context.inflater(@LayoutRes layout: Int): View =
    LayoutInflater.from(this).inflate(layout, null, false)

fun View.getConstraintLP(): ConstraintLayout.LayoutParams =
    layoutParams as ConstraintLayout.LayoutParams


internal inline fun LifecycleOwner.launchCountDownCoroutines(
    total: Int,
    crossinline onTick: (Int) -> Unit,
    crossinline onFinish: (cause: Throwable?) -> Unit,
    scope: CoroutineScope = lifecycle.coroutineScope,
): Job {
    return flow {
        val range = total downTo 0
        for (i in range) {
            emit(i)
            delay(1000)
        }
    }.flowOn(Dispatchers.Default)
        .onCompletion { cause -> onFinish.invoke(cause) }
        .onEach { onTick.invoke(it) }
        .flowOn(Dispatchers.Main)
        .launchIn(scope)
}

suspend inline fun Context.loadImage(
    data: Any?,
    imageLoader: ImageLoader = this.imageLoader,
    builder: ImageRequest.Builder.() -> Unit = {},
): ImageResult {
    val request = ImageRequest.Builder(this)
        .apply(builder)
        .data(data)
        .allowHardware(false)
        .build()
    return imageLoader.execute(request)
}


/** 添加点击缩放效果
 * @param isNeedCanClick 是否允许连续多次点击 */
@SuppressLint("ClickableViewAccessibility")
fun View.addClickScale(
    scale: Float = 0.9f,
    duration: Long = 150,
    isNeedCanClick: Boolean = false,
    onClick: (View.() -> Unit)? = null,
) {
    this.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                this.animate().scaleX(scale).scaleY(scale).setDuration(duration).start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                this.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
            }
        }
        // 点击事件处理，交给View自身
        this.onTouchEvent(event)
    }
    onClick?.also { c ->
        this.setOnClickListener {
            takeIf { if (isNeedCanClick) true else ClickHelper.getHelper().isCanClick }?.run {
                c.invoke(it)
            }
        }
    }
}

fun View.addClick(onClick: View.() -> Unit) {
    this.setOnClickListener {
        takeIf { ClickHelper.getHelper().isCanClick }?.run {
            onClick(it)
        }
    }
}

fun checkNetwork(context: Context): Boolean {
    var isNetworkUsable = false
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nc = manager.getNetworkCapabilities(manager.activeNetwork)
    if (nc != null) {
        isNetworkUsable = nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    return isNetworkUsable
}

fun Fragment.onBackPress(block: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        block.invoke()
    }
}

private fun to32BitString(plainText: String, charset: String?): String? {
    return try {
        val md = MessageDigest.getInstance("MD5")
        md.reset()
        if (charset != null) {
            md.update(plainText.toByteArray(charset(charset)))
        } else {
            md.update(plainText.toByteArray())
        }
        val byteArray = md.digest()
        val md5Buf = StringBuffer()
        val var6 = byteArray.size
        for (var7 in 0 until var6) {
            val b = byteArray[var7]
            if (Integer.toHexString(255 and b.toInt()).length == 1) {
                md5Buf.append("0").append(Integer.toHexString(255 and b.toInt()))
            } else {
                md5Buf.append(Integer.toHexString(255 and b.toInt()))
            }
        }
        md5Buf.toString()
    } catch (var9: java.lang.Exception) {
        var9.printStackTrace()
        null
    }
}

fun MD5generator(s: String): String? {
    return to32BitString(s, null as String?)
}

fun InputStream.toFile(path: String, childName: String) {
    val TAG = "InputStream 2 file"
    kotlin.runCatching {
        loge(TAG, "toFile: $path ")
        if (!File(path).exists()) {
            File(path).mkdirs()
        }
        val file = File(path, childName)
        file.createNewFile()
        val outputStream = file.outputStream()
        val buffer = ByteArray(8192)
        while (true) {
            val len = this.read(buffer)
            if (len < 0) break
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush()
        this.close()
        outputStream.close()
        file
    }.onFailure {
        loge(TAG, it,"failed")
    }.onSuccess { loge(TAG, "suc ${it.absolutePath}") }
}

/**保存asset文件到缓存*/
internal fun createAssetFile(cacheDirName: String, filePath: String, fileName: String): String {
    runCatching {
        val cacheDir = cacheDirName.asFile()
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val outFile = File(cacheDir, fileName)
        if (!outFile.exists()) {
            val res = outFile.createNewFile()
            if (!res) return ""
        } else {
            if (outFile.length() > 10) return outFile.absolutePath
        }
        val inputSystem = getAppContext().assets.open(filePath)
        val fos = FileOutputStream(outFile)
        val buffer = ByteArray(1024 * 4)
        var byteCount: Int
        while (inputSystem.read(buffer).also { byteCount = it } != -1) {
            fos.write(buffer, 0, byteCount)
        }
        fos.flush()
        inputSystem.close()
        fos.close()
        outFile.absolutePath
    }.onFailure {
        loge("animalProps", it,"createAssetFile: failed")
    }.onSuccess {
        return it
    }
    return ""
}

fun timeStamp2HMS(time: Long): String {
    val h = time / 1000 / (60 * 60)
    val m = time / 1000 / 60
    val s = time / 1000
    val strH = if (h < 10) "0$h" else h.toString()
    val strM = if (m < 10) "0$h" else m.toString()
    val strS = if (s < 10) "0$s" else s.toString()
    return "$strH:$strM:$strS"
}

fun raw2File(id: Int, path: String,childName: String = "hair") {
    val inputStream = getAppContext().resources.openRawResource(id)
    inputStream.toFile(path, childName)
}

fun View.coverToBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(Color.TRANSPARENT)
    val canvas = Canvas(bitmap)
    draw(canvas)
    canvas.drawARGB(128, 255, 255, 255)
    return bitmap
}

