package endlessmusicplayer.ui.materialplaypause

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout

import ec.orangephi.endlessmusicplayer.R

class PlayPauseView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val mDrawable: PlayPauseDrawable
    private val mPaint = Paint()
    private val mPauseBackgroundColor: Int
    private var mPlayBackgroundColor: Int

    private var mAnimatorSet: AnimatorSet? = null
    private var mBackgroundColor : Int = 0
    private var color: Int
        set(value) {
            this.mBackgroundColor = value
            invalidate()
        }
        get() = mBackgroundColor
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    var playing : Boolean = true
    set(value) {
        if(value != playing) {
            toggle()
            field = value
        }
    }

    init {
        setWillNotDraw(false)
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mDrawable = PlayPauseDrawable(context)
        mDrawable.callback = this

        mPauseBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
        mPlayBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)

        playing = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawable.setBounds(0, 0, w, h)
        mWidth = w
        mHeight = h

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = object : ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
            clipToOutline = true
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return who === mDrawable || super.verifyDrawable(who)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = color
        val radius = Math.min(mWidth, mHeight) / 2f
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint)
        mDrawable.draw(canvas)
    }

    fun toggle() {
        if (mAnimatorSet != null) {
            mAnimatorSet!!.cancel()
        }

        mAnimatorSet = AnimatorSet()
        val isPlay = mDrawable.isPlay
        val colorAnim = ObjectAnimator.ofInt(this, COLOR, if (isPlay) mPauseBackgroundColor else mPlayBackgroundColor)
        colorAnim.setEvaluator(ArgbEvaluator())
        val pausePlayAnim = mDrawable.pausePlayAnimator
        mAnimatorSet!!.interpolator = DecelerateInterpolator()
        mAnimatorSet!!.duration = PLAY_PAUSE_ANIMATION_DURATION
        mAnimatorSet!!.playTogether(colorAnim, pausePlayAnim)
        mAnimatorSet!!.start()
    }

    companion object {

        private val COLOR = object : Property<PlayPauseView, Int>(Int::class.java, "color") {
            override fun get(v: PlayPauseView): Int? {
                return v.color
            }

            override fun set(v: PlayPauseView, value: Int?) {
                v.color = value!!
            }
        }

        private val PLAY_PAUSE_ANIMATION_DURATION: Long = 200
    }
}
