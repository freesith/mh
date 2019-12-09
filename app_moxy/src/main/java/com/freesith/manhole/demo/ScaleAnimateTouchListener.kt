package com.freesith.manhole.demo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import java.lang.ref.WeakReference

class ScaleAnimateTouchListener : View.OnTouchListener {

    //是否可以播放放大恢复动画
    var canScaleBack = false
    var click = false
    var viewRef: WeakReference<View>? = null
    //是否移出view范围
    var out = false
    var downTime = 0L

    private val animatorListener: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            if (canScaleBack) {
                //canScaleBack为true,说明是快按,此时手指已经抬起
                viewRef?.get()?.animate()?.setListener(if (click) clickListener else null)
                viewRef?.get()?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(120)?.start()
            } else {
                //canScaleBack为false,说明是长按,手指还没有抬起
                canScaleBack = true
            }
        }
    }

    /**
     * 放大动画的监听,动画播完,再触发点击回调
     */
    private val clickListener: AnimatorListenerAdapter by lazy {
        object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animation?.removeAllListeners()
                viewRef?.get()?.performClick()
            }
        }
    }


    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (view != null && event != null) {
            Log.d("xxx","action = " + MotionEvent.actionToString(event.action))
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downTime = SystemClock.elapsedRealtime()
                    canScaleBack = false
                    out = false
                    click = false
                    if (viewRef == null) {
                        viewRef = WeakReference(view)
                    }
                    view.animate().setListener(animatorListener)
                    view.animate().scaleX(0.94f).scaleY(0.94f).setDuration(120).start()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (out || click) {
                        return false
                    }
                    //没有标记为点击,接收到点击事件,拦截系统的点击,等待动画播放完毕
                    if (!click && event.action == MotionEvent.ACTION_UP) {
                        //如果view设置了长按,且时间超过长按触发时间
                        //如果view.setLongClickable(true),但是没有设置长按回调的话,此时也不会触发单击事件
                        if (view.isLongClickable && SystemClock.elapsedRealtime() - downTime > ViewConfiguration.getLongPressTimeout()) {
                            return false
                        }
                        click = true
                    }
                    if (canScaleBack) {
                        //canScaleBack为true,说明是长按,缩小动画已经播放完了,直接播放放大就行
                        view.animate().setListener(if (click) clickListener else null)
                        view.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                    } else {
                        //canScaleBack为false,说明是快按,缩小动画还没有播放完,把变量置为true,播放完缩小直接播放放大
                        canScaleBack = true
                    }
                    if (click) {
                        //防止下次点击触发长按事件
                        event.action = MotionEvent.ACTION_CANCEL
                        view.dispatchTouchEvent(event)
                        return true
                    }

                }
                MotionEvent.ACTION_MOVE -> {
                    //移出过,不再处理
                    if (out) {
                        return false
                    }
                    if (event.x < 0 || event.y < 0 || event.x > viewRef?.get()?.width ?: 0 || event.y > viewRef?.get()?.height ?: 0) {
                        //手指移出,点击已无法触发,播放放大动画
                        out = true
                        if (canScaleBack) {
                            view.animate().setListener(null)
                            view.animate().scaleX(1f).scaleY(1f).setDuration(60).start()
                        } else {
                            canScaleBack = true
                        }
                    }
                }
                else -> {

                }
            }
        }
        return false
    }

}