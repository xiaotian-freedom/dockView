package com.storn.viewlib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs

/**
 * @Description:
 * @Author: TST
 * @CreateDate: 2021/6/29$ 11:00 下午$
 * @UpdateUser:
 * @UpdateDate: 2021/6/29$ 11:00 下午$
 * @UpdateRemark:
 * @Version: 1.0
 */
class BubbleTickView : AppCompatImageView {

    private val tag = "BUBBLE"

    //当前移动的x、y坐标
    private var mCurrX = 0.0f
    private var mCurrY = 0.0f

    //最后一次触摸屏幕的坐标
    private var mLastX = 0f
    private var mLastY = 0f

    //移动距离
    private var mDelX = 5
    private var mDelY = 5

    //边界检测最大值
    private var mMaxW = 0
    private var mMaxH = 0

    //view宽高
    private var mSelfW = 0
    private var mSelfH = 0

    //按下屏幕的时间
    private var mStartTime = 0L

    //手指拖动时最小阈值
    private val mMinMoveThreshold = 3

    //发呆秒数
    private var mDazeDuration = 5 * 1000L

    //是否可以运动
    private var mCanMove = false

    //是否处于自由运动中
    private var mInMoving = true

    //是否开始发呆
    private var mStartDaze = false

    //是否自动隐藏到屏幕右边
    private var mIsHideToSide = false

    //是否正在向右移动隐藏
    private var mIsMovingToSide = false

    //是否正在停靠在屏幕边缘
    private var mIsDocking = false

    //是否已停靠在屏幕边缘
    private var mIsDockedToSide = false

    //是否显示半边停靠drawable
    private var mShowDockDrawable = false

    //停靠边缘时替换的drawable
    private var mDockDrawable: Drawable? = null

    //原始drawable
    private var mOriginDrawable: Drawable? = null

    //移动到屏幕边缘倒计时
    private var mDozeTimer: CountDownTimer? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if (mCanMove && !mStartDaze && !mIsHideToSide && !mIsDockedToSide) {
                updateParam()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    mStartTime = System.currentTimeMillis()
                    if (mIsMovingToSide) {//正在移动到边缘时不做任何处理
                        return false
                    }
//                    if (mIsHideToSide) {//隐藏到边缘后停止GIF播放
//                        return false
//                    }
//                    if (mIsDockedToSide) {//停靠在边缘时禁止移动
//                        return false
//                    }
                    mCanMove = false
                    mLastX = it.rawX
                    mLastY = it.rawY
                }
                MotionEvent.ACTION_UP -> {
                    if (mIsMovingToSide) {//正在移动到边缘时不做任何处理
                        return false
                    }
                    if (mIsHideToSide) {//隐藏到边缘后停止GIF播放
                        return false
                    }
                    val upTime = System.currentTimeMillis()
                    if (upTime - mStartTime < 300) {//点击了下图片，执行点击事件
                        performClick()
                        return false
                    }
                    if (mIsDockedToSide) {//停靠在边缘时禁止移动
                        return false
                    }
                    mCanMove = true
                    startPlayGIF()
                }
                MotionEvent.ACTION_MOVE -> {
                    mCanMove = false
                    if (mIsMovingToSide) {//正在移动到边缘时不做任何处理
                        return false
                    }
                    if (mIsDockedToSide) {//停靠在边缘时禁止移动
                        return false
                    }
                    if (mIsHideToSide) {//隐藏到边缘后停止移动事件
                        return false
                    }
                    //如果是动图，在移动时需要停止GIF动效
                    stopPlayGIF()

                    val deltaX = it.rawX - mLastX
                    val deltaY = it.rawY - mLastY
                    if (abs(deltaX) < mMinMoveThreshold && abs(deltaY) < mMinMoveThreshold) {//开始发呆计时
                        if (!mStartDaze) {
                            mStartDaze = true
                            startDazeTimeCount()
                        }
                        return false
                    }
                    mStartDaze = false
                    mDozeTimer?.cancel()
                    Log.d(tag, "发呆取消")
                    updateParam(deltaX, deltaY)
                    mLastX = it.rawX
                    mLastY = it.rawY
                }
            }
        }
        return true
    }

    /**
     * 自动更新坐标信息
     */
    private fun updateParam() {
        mCurrX += mDelX
        mCurrY += mDelY
        if (mCurrX + mSelfW >= mMaxW) {
            mDelX = -mDelX
            mCurrX = (mMaxW - mSelfW).toFloat()
        }
        if (mCurrX < 0) {
            mDelX = -mDelX
            mCurrX = 0f
        }
        if (mCurrY + mSelfH >= mMaxH) {
            mDelY = -mDelY
            mCurrY = (mMaxH - mSelfH).toFloat()
        }
        if (mCurrY < 0) {
            mDelY = -mDelY
            mCurrY = 0f
        }
        val lp = FrameLayout.LayoutParams(mSelfW, mSelfH)
        lp.setMargins(mCurrX.toInt(), mCurrY.toInt(), 0, 0)
        layoutParams = lp
        requestLayout()
    }

    /**
     * 跟随手指移动更新坐标信息
     */
    private fun updateParam(dx: Float, dy: Float) {
        mCurrX = dx + left
        mCurrY = dy + top

        if (mCurrX < 0) {
            mCurrX = 0f
        }
        if (mCurrX + mSelfW >= mMaxW) {
            mCurrX = (mMaxW - mSelfW).toFloat()
        }
        if (mCurrY < 0) {
            mCurrY = 0f
        }
        if (mCurrY + mSelfH >= mMaxH) {
            mCurrY = (mMaxH - mSelfH).toFloat()
        }

        val lp = layoutParams as FrameLayout.LayoutParams
        lp.width = mSelfW
        lp.height = mSelfH
        lp.setMargins(mCurrX.toInt(), mCurrY.toInt(), 0, 0)
        layoutParams = lp
        requestLayout()
    }

    /**
     * 开始发呆计时
     */
    private fun startDazeTimeCount() {
        Log.d(tag, "开始发呆")
        if (mIsMovingToSide) return
        if (mDozeTimer == null) {
            mDozeTimer = object : CountDownTimer(mDazeDuration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Log.d(tag, "发呆剩余时间-》${millisUntilFinished / 1000}s")
                }

                override fun onFinish() {
                    if (!mStartDaze) return
                    if (!mIsMovingToSide) {
                        mIsMovingToSide = true
                        stopPlayGIF()
                        innerHideToSide()
                    }
                }
            }
        }
        mDozeTimer!!.start()
    }

    /**
     * 隐藏到屏幕右边
     * 分为全隐藏和半隐藏
     * 半隐藏状态下替换为dockDrawable
     */
    private fun innerHideToSide() {
        if (!mIsHideToSide) {
            mCurrX += 10
            if (mShowDockDrawable && mDockDrawable != null) {
                if (mCurrX >= mMaxW - mSelfW / 2) {
                    mCurrX = (mMaxW - mSelfW / 2).toFloat()
                    mIsHideToSide = true
                    mIsDocking = false
                    mIsDockedToSide = false
                    mInMoving = false
                }
            } else {
                if (mCurrX >= mMaxW) {
                    mIsHideToSide = true
                    mIsDocking = false
                    mIsDockedToSide = false
                    mInMoving = false
                }
            }

            val right: Int = if (mCurrX + mSelfW < mMaxW) {
                0
            } else {
                -(mCurrX + mSelfW - mMaxW).toInt()
            }

            val lp = layoutParams as FrameLayout.LayoutParams
            lp.setMargins(mCurrX.toInt(), mCurrY.toInt(), right, 0)
            layoutParams = lp
            invalidate()

            postDelayed({
                if (!mIsHideToSide) {
                    innerHideToSide()
                } else {
                    mIsMovingToSide = false
                    if (mShowDockDrawable && mDockDrawable != null) {
                        setImageDrawable(mDockDrawable)
                    }

                    Log.d(tag, "isHideToSide")
                }
            }, 5)
        }
    }

    /**
     * 滑动列表后自动停靠在屏幕边缘
     */
    fun dockToSide() {
        if (!mIsHideToSide || mIsDocking || mIsDockedToSide) return
        mIsDocking = true
        animToDock()
        startPlayGIF()
    }

    /**
     * 外部调用方法
     */
    fun hideToSide() {
        mStartDaze = true
        stopPlayGIF()
        innerHideToSide()
    }

    /**
     * 重新开始游动
     */
    fun resetSwimming() {
        mCanMove = true
        mStartDaze = false
        mIsHideToSide = false
        mIsDockedToSide = false
        postInvalidate()
    }

    /**
     * 是否停靠在边栏
     */
    fun isDocked(): Boolean {
        return mIsHideToSide
    }

    /**
     * 是否正在自由移动
     */
    fun isInMoving(): Boolean {
        return mInMoving
    }

    /**
     * 执行动画显示在边缘位置
     */
    private fun animToDock() {
        if (mCurrX + mSelfW > mMaxW) {
            mCurrX -= 5
            if (mCurrX + mSelfW <= mMaxW) {
                mCurrX = (mMaxW - mSelfW).toFloat()
                mIsDockedToSide = true
                mIsDocking = false
                mIsHideToSide = false
                mInMoving = false
            }
            val lp = layoutParams as FrameLayout.LayoutParams
            lp.setMargins(mCurrX.toInt(), mCurrY.toInt(), 0, 0)
            layoutParams = lp
            invalidate()

            postDelayed({
                if (!mIsDockedToSide) {
                    animToDock()
                } else {//是否替换dockDrawable
                    if (mShowDockDrawable && mDockDrawable != null && mOriginDrawable != null) {
                        setImageDrawable(mOriginDrawable)
                        postDelayed({
                            startPlayGIF()
                        }, 1000)
                    }
                }
            }, 5)
        }
    }

    /**
     * 停止播放GIF动画
     */
    private fun stopPlayGIF() {
        if (drawable is Animatable) {
            if ((drawable as Animatable).isRunning) {
                (drawable as Animatable).stop()
            }
        }
    }

    /**
     * 开始播放GIF动画
     */
    private fun startPlayGIF() {
        if (drawable is Animatable) {
            if (!(drawable as Animatable).isRunning) {
                (drawable as Animatable).start()
            }
        }
    }

    /**
     * 外界通知开始动画
     * 在图片资源加载完成后
     * 注入view当前所在的坐标信息
     */
    inner class Builder {

        fun setX(x: Float): Builder {
            mCurrX = x
            return this
        }

        fun setY(y: Float): Builder {
            mCurrY = y
            return this
        }

        fun setWidth(w: Int): Builder {
            mSelfW = w
            return this
        }

        fun setHeight(h: Int): Builder {
            mSelfH = h
            return this
        }

        fun setMaxWidth(w: Int): Builder {
            mMaxW = w
            return this
        }

        fun setMaxHeight(h: Int): Builder {
            mMaxH = h
            return this
        }

        fun setDozeDuration(duration: Int): Builder {
            mDazeDuration = duration * 1000L
            return this
        }

        fun setDockDrawable(drawable: Drawable): Builder {
            mDockDrawable = drawable
            return this
        }

        fun setShowDockDrawable(show: Boolean): Builder {
            mShowDockDrawable = show
            return this
        }

        fun setOriginDrawable(drawable: Drawable): Builder {
            mOriginDrawable = drawable
            return this
        }

        fun move() {
            mCanMove = true
            postInvalidate()
        }
    }
}