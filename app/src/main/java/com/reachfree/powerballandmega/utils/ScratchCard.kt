package com.reachfree.powerballandmega.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.reachfree.powerballandmega.R


class ScratchCard : View {
    private var mScratchWidth = 0f
    private var mDrawable: Drawable? = null
    private var mBitmap: Bitmap? = null
    private lateinit var mCanvas: Canvas
    private lateinit var mPath: Path
    private lateinit var mInnerPaint: Paint
    private lateinit var mOuterPaint: Paint
    private lateinit var mListener: OnScratchListener

    interface OnScratchListener {
        fun onScratch(scratchCard: ScratchCard?, visiblePercent: Float)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        resolveAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        resolveAttr(context, attrs)
    }

    constructor(context: Context) : super(context) {
        resolveAttr(context, null)
    }

    private fun resolveAttr(context: Context, attrs: AttributeSet?) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchCard)
        mDrawable = typedArray.getDrawable(R.styleable.ScratchCard_scratchDrawable)
        mScratchWidth = typedArray.getDimension(
            R.styleable.ScratchCard_scratchWidth,
            AppUtils.dipToPx(context, 20)
        )
        typedArray.recycle()
    }

    fun setOnScratchListener(listener: OnScratchListener) {
        mListener = listener
    }

    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        mBitmap?.recycle()

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)

        mDrawable?.let {
            it.setBounds(0, 0, mBitmap!!.width, mBitmap!!.height)
            it.draw(mCanvas)
        } ?: mCanvas.drawColor(-0x138f9d)

        mPath = Path()

        mInnerPaint = Paint()
        mInnerPaint.isAntiAlias = true
        mInnerPaint.isDither = true
        mInnerPaint.style = Paint.Style.STROKE
        mInnerPaint.isFilterBitmap = true
        mInnerPaint.strokeJoin = Paint.Join.ROUND
        mInnerPaint.strokeCap = Paint.Cap.ROUND
        mInnerPaint.strokeWidth = mScratchWidth
        mInnerPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        mOuterPaint = Paint()

    }

    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val currentTouchX = event.x
        val currentTouchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath.reset()
                mPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(currentTouchX - mLastTouchX)
                val dy = Math.abs(currentTouchY - mLastTouchY)
                if (dx >= 4 || dy >= 4) {
                    val x1 = mLastTouchX
                    val y1 = mLastTouchY
                    val x2 = (currentTouchX + mLastTouchX) / 2
                    val y2 = (currentTouchY + mLastTouchY) / 2
                    mPath.quadTo(x1, y1, x2, y2)
                }
            }
            MotionEvent.ACTION_UP -> {
                mPath.lineTo(currentTouchX, currentTouchY)

                val width = mBitmap!!.width
                val height = mBitmap!!.height
                val total = width * height
                var count = 0
                var i = 0
                while (i < width) {
                    var j = 0
                    while (j < height) {
                        if (mBitmap!!.getPixel(i, j) == 0x00000000) count++
                        j += 3
                    }
                    i += 3
                }
                mListener.onScratch(this, count.toFloat() / total * 9)
            }
        }
        mCanvas.drawPath(mPath, mInnerPaint)
        mLastTouchX = currentTouchX
        mLastTouchY = currentTouchY
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mOuterPaint)
        super.onDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mBitmap?.let { bitmap ->
            bitmap.recycle()
            mBitmap = null
        }
    }
}