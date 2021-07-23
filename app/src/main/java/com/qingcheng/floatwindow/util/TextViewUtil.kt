package com.qingcheng.floatwindow.util

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.TextView

//0 122 255
//255 187 51
@SuppressLint("StaticFieldLeak")
class TextViewUtil(private val textView: TextView) {
    var isPrinting = false
        private set
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var mTexts: Array<out CharSequence> = arrayOf("暂时没有提示，点点其他地方吧")
    private var index: Int = 0

    fun setText(vararg texts: CharSequence): TextViewUtil {
        mTexts = texts
        return this
    }

    fun printNextText() {
        cancelPrint()
        textView.text = null
        var i = 0
        runnable = object : Runnable {
            override fun run() {
                textView.append(mTexts[index][i].toString())
                i++
                when {
                    i < mTexts[index].length -> {
                        isPrinting = true
                        handler.postDelayed(this, 100)
                    }
                    index < mTexts.size - 1 -> {
                        index++
                        isPrinting = false
                    }
                    else -> {
                        index = 0
                        isPrinting = false
                    }
                }
            }
        }
        handler.post(runnable!!)
    }

    fun cancelPrint() {
        runnable?.let { handler.removeCallbacks(it) }
    }

    fun printToEnd() {
        cancelPrint()
        textView.text = mTexts[index]
        if (index < mTexts.size - 1) index++
        else index = 0
        isPrinting = false
    }

//    fun colorText(text: String): SpannableStringBuilder {
//        val spannableStringBuilder = SpannableStringBuilder()
//        var j = 0
//        for (i in text) {
//            val r = 0 + (j % 9) * (255 / 8)
//            val g = 0
//            val b = 255 - (j % 9) * (255 / 8)
//            spannableStringBuilder.append(
//                SpannableString(i.toString()).apply {
//                    setSpan(
//                        ForegroundColorSpan(Color.rgb(r, g, b)),
//                        0,
//                        1,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                })
//            j++
//        }
//        return spannableStringBuilder
//    }
}

