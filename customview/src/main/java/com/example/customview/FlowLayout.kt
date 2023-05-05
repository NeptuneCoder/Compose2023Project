package com.example.customview

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.max

/**
 * 第二节，如何测量和布局
 */
class FlowLayout(context: Context, attributeSet: AttributeSet) : ViewGroup(context, attributeSet) {

    //onMeasure 方法会被调用两次，为什么？为了进行二级测量优化；二级测量优化的原理在源码具体Draw时，如果当前是可见的，则调用scheduleTraversals()；
    //调用scheduleTraversals()；方法会导致onMeasure方法调用两次。因此在onMeasure中，如果当前为精确模式时则计算父控件的大小。否者进行子view大小的测量工作。
    private val childrenBounds = mutableListOf<Rect>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthUsed = 0
        var heightUsed = 0
        var lineWidthUsed = 0
        var lineMaxHeight = 0
        val specWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        for ((index, child) in children.withIndex()) {
            //测量孩子的宽高
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed)

            if (specWidthMode != MeasureSpec.UNSPECIFIED &&
                lineWidthUsed + child.measuredWidth > specWidthSize
            ) {//当前为Wrap或者MATCH模式
                lineWidthUsed = 0
                heightUsed += lineMaxHeight
                lineMaxHeight = 0
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed)
            }

            if (index >= childrenBounds.size) {
                childrenBounds.add(Rect())
            }
            val childBounds = childrenBounds[index]
            childBounds.set(
                lineWidthUsed,
                heightUsed,
                lineWidthUsed + child.measuredWidth,
                heightUsed + child.measuredHeight
            )

            lineWidthUsed += child.measuredWidth
            widthUsed = max(widthUsed, lineWidthUsed)
            lineMaxHeight = max(lineMaxHeight, child.measuredHeight)
        }
        val selfWidth = widthUsed
        val selfHeight = heightUsed + lineMaxHeight
        //设置当前控件本身的大小
        setMeasuredDimension(selfWidth, selfHeight)
    }


    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //TODO 进行布局
        for ((index, child) in children.withIndex()) {
            val childBounds = childrenBounds[index]
            child.layout(childBounds.left, childBounds.top, childBounds.right, childBounds.bottom)
        }
    }
}