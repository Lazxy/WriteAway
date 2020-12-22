package com.work.lazxy.writeaway.utils

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.StaticLayout
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.WriteAway
import com.work.lazxy.writeaway.ui.widget.ProgressDialog
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import kotlin.coroutines.Continuation

/**
 *
 * @author Lazxy
 * @date 2020/11/19
 */
class NoteSnapshotGenerator() {
    private lateinit var mContext: Context

    private var mConfig = Config()

    private var density: Float = ScreenUtils.getDensity(WriteAway.appContext)

    private var outPaddingValue: Int = 0

    private var innerPaddingValue: Int = 0

    private var titlePaddingExtra: Int = (24 * density).toInt()

    private var titleSeparateLineHeight: Int = (2 * density).toInt()

    private var contentPaddingExtra: Int = (16 * density).toInt()

    private var signAreaHeight: Int = 0

    private var calculateHeight: Int = 0

    private val DEFAULT_OUT_BG_COLOR: Int = 0xFF333333.toInt()

    private val TITLE_SEPARATE_LINE_COLOR: Int = 0xFFF44336.toInt()

    private val SIGN = "WRITE AWAY"

    //Todo 讲道理这里每一段的Layout应该都不一样 暂时只考虑纯文本的情况
    private lateinit var contentLayout: StaticLayout

    private lateinit var titleLayout: StaticLayout

    private lateinit var signLayout: StaticLayout


    private constructor(config: Config, context: Context) : this() {
        mConfig = config
        mContext = context
        val outPaddingScale: Float = 48 / 1080f
        val innerPaddingScale: Float = 48 / 1080f
        outPaddingValue = (outPaddingScale * config.width).toInt()
        innerPaddingValue = (innerPaddingScale * config.width).toInt()
    }

    /** 模仿一加 中心留下卡片区域绘制文字 包括加粗的标题 四周留边距 加底色，最下方加一个copyright
     * 问题有几个：怎么重新计算每行字的宽度 重新排版 - 用StaticLayout解决了
     * 怎么处理大图内存占用问题
     * 怎么平铺背景皮肤 - 不折腾皮肤了 用纯色好了
     * 生成的图片怎么存
     */

    /** 1.绘制相应的图片
     *  2.压缩绘制的图片
     *  3.存储输出的图片到相应位置
     */
    fun generate(): Boolean {
        pretreatmentContent();
        initTextLayout()
        //计算整张图片的高度 通过写一个特别设置过参数的TextView测量来实现
        calculateHeight = calculateContentHeight()
        val canvasBmp = Bitmap.createBitmap(mConfig.width, calculateHeight, Bitmap.Config.RGB_565)
        val canvas = Canvas(canvasBmp)
        //绘制内容之外的部分
        drawBackground(canvas)
        drawCopyright(canvas)
        val saveCount = canvas.save()
        //缩减画布区域，使之后的操作不影响外部边框部分
        canvas.clipRect(outPaddingValue + innerPaddingValue, outPaddingValue + innerPaddingValue,
                mConfig.width - outPaddingValue - innerPaddingValue,
                calculateHeight - signAreaHeight - outPaddingValue - innerPaddingValue)
        canvas.translate(outPaddingValue.toFloat() + innerPaddingValue, outPaddingValue.toFloat() + innerPaddingValue)
        //绘制内容部分
        drawTitle(canvas)
        drawContent(canvas, 0)
        canvas.restoreToCount(saveCount)
        return saveToFile(canvasBmp)
    }

    private fun initTextLayout() {
        val allWidth = mConfig.width
        //这里用布局的目的是简化对TextPaint的设置
        val templateRoot: View = LayoutInflater.from(WriteAway.appContext).inflate(R.layout.template_snapshot, null, false)
        val contentText = templateRoot.findViewById<TextView>(R.id.template_snapshot_tv_content)
        val titleText = templateRoot.findViewById<TextView>(R.id.template_snapshot_tv_title)
        val signText = templateRoot.findViewById<TextView>(R.id.template_snapshot_tv_sign)
        val availableWidth = allWidth - ((outPaddingValue + innerPaddingValue) * 2)

        contentLayout = StaticLayout(mConfig.texts[0], contentText.paint, availableWidth, Layout.Alignment.ALIGN_NORMAL,
                1.4f, 0f, false)

        titleLayout = StaticLayout(mConfig.title, titleText.paint, availableWidth, Layout.Alignment.ALIGN_NORMAL,
                1f, 24 * density, false)

        signLayout = StaticLayout(SIGN, signText.paint.also { it.color = 0xFFF1F1F1.toInt() }, availableWidth, Layout.Alignment.ALIGN_CENTER,
                1f, 0f, false)
    }

    private fun pretreatmentContent() {
        //预处理内容文本，段落间增加空行
        val textsAfterTreat = ArrayList<CharSequence>()
        for (text in mConfig.texts) {
            if (text is String) {
                val formattedText = text.replace("\n", "\n\n")
                val spannable: Spannable = SpannableString(formattedText)
                val matcher = Pattern.compile("\n\n").matcher(formattedText)
                //修改空行的字体大小，以达到控制段落间距不过大的效果
                while (matcher.find()) {
                    spannable.setSpan(AbsoluteSizeSpan(8, true), matcher.start() + 1, matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                textsAfterTreat.add(spannable)
            }
        }
        mConfig.texts = textsAfterTreat
    }

    private fun calculateContentHeight(): Int {
        val extraPadding = (outPaddingValue + innerPaddingValue) * 2
        val titleHeight = titleLayout.height + titlePaddingExtra * 2
        val contentHeight = contentLayout.height + contentPaddingExtra
        //这里设置签名的底边距和外边距一样
        signAreaHeight = signLayout.height + outPaddingValue
        return extraPadding + titleHeight + titleSeparateLineHeight + contentHeight + signAreaHeight
    }

    private fun drawBackground(canvas: Canvas) {
        //绘制底层背景和圆角背景
        val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL_AND_STROKE
        }
        val radius = 8 * density
        //随便画个底色
        canvas.drawColor(DEFAULT_OUT_BG_COLOR)
        canvas.drawRoundRect(RectF(outPaddingValue.toFloat(), outPaddingValue.toFloat(),
                (mConfig.width - outPaddingValue).toFloat(), (calculateHeight - outPaddingValue - signAreaHeight).toFloat()), radius, radius, paint)
    }

    private fun drawCopyright(canvas: Canvas) {
        canvas.apply {
            save()
            translate((outPaddingValue + innerPaddingValue).toFloat(), (calculateHeight - signAreaHeight).toFloat())
            signLayout.draw(this)
            restore()
        }
    }

    private fun drawTitle(canvas: Canvas) {
        val separatePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = TITLE_SEPARATE_LINE_COLOR
            strokeWidth = titleSeparateLineHeight.toFloat()
        }
        canvas.apply {
            translate(0f, titlePaddingExtra.toFloat())
            titleLayout.draw(this)
            translate(0f, titleLayout.height.toFloat() + titlePaddingExtra)
            drawLine(0f, 0f, 4f / 15 * this.width, 0f, separatePaint)
        }
    }

    private fun drawContent(canvas: Canvas, index: Int) {
        canvas.translate(0f, contentPaddingExtra.toFloat())
        contentLayout.draw(canvas)
    }

    private fun saveToFile(src: Bitmap): Boolean {
        var isSuccess = false
        var outFile: OutputStream? = null
        try {
            val path = mConfig.targetPath
            outFile = with(File(path, mConfig.title + "_" + SimpleDateFormat("yyyy:MM:dd:HH:mm:ss")
                    .format(System.currentTimeMillis()) + ".jpg")) {
                if (!exists()) createNewFile()
                FileOutputStream(this.path)
            }
            src.compress(Bitmap.CompressFormat.JPEG, 80, outFile)
            Log.i("Temp Log", "生成图片完成 地址是${path}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            src.recycle()
            outFile?.close()
            isSuccess = true
        }
        return isSuccess
    }

    class Builder {
        private var config = Config()

        fun setText(text: String): Builder {
            config.texts.add(text)
            return this
        }

        fun setTitle(title: String): Builder {
            config.title = title
            return this
        }

        fun setWidth(width: Int): Builder {
            config.width = width
            return this
        }

        fun build(context: Context): NoteSnapshotGenerator {
            return NoteSnapshotGenerator(config, context)
        }
    }

    class Config {
        var width: Int = ScreenUtils.getScreenWidth(WriteAway.appContext)

        var texts: ArrayList<CharSequence> = ArrayList()

        var title: String = ""

        var targetPath: String? = WriteAway.appContext.externalCacheDir?.path
    }
}