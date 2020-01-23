package com.freesith.jsonview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.freesith.jsonview.bean.JsonElement

class JsonPointerView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    val rects = arrayListOf<List<Int>>()
    var transX = 0
    var transY = 0
    var jsonElements: MutableList<JsonElement<*>>? = null

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = Color.RED
        textPaint.color = Color.WHITE
        textPaint.textSize = 30f
    }

    //1,先显示下拉箭头
    //2.点击箭头,再解析第一层
    //3.手指按滑哪里,解析哪里的第二层...
    //4.双指按下为拖动
    //5.jsonView需要根据pointer的范围显示
    fun setJson(jsonElements: MutableList<JsonElement<*>>) {
        this.jsonElements = jsonElements
        rects.clear()

        val list = arrayListOf<Int>()
        val size = jsonElements.size
        var index = 0
        while (index < size) {
            val jsonElement = jsonElements[index]
            if (jsonElement.hasChild) {
                list.add(index)
                index = jsonElement.childEnd
            } else {
                list.add(index)
                index++
            }
        }
        rects.add(list)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!rects.isNullOrEmpty()) {
            rects.forEachIndexed { index, list ->

                list.forEachIndexed { index, i ->

                    val jsonElement = jsonElements!!.get(i)

                    canvas?.drawRect(0f, index * 100f, 200f, (index + 1) * 100f, paint)
                    canvas?.drawText(
                        jsonElement.name!!,
                        0f,
                        (index * 100 + 50).toFloat(),
                        textPaint
                    )
                }
            }
        }
    }
}