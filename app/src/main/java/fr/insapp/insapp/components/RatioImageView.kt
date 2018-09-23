package fr.insapp.insapp.components

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.Log
import fr.insapp.insapp.R
import fr.insapp.insapp.models.ImageSize

/**
 * Created by thomas on 23/07/2017.
 *
 * Based on https://gist.github.com/JakeWharton/2856179
 */

class RatioImageView : AppCompatImageView {

    private var imageSize: ImageSize? = null
    private var fixedDimension: Int = 0

    constructor(context: Context) : super(context) {

        fixedDimension = DEFAULT_FIXED_DIMENSION
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        // get attributes

        val array = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView)
        fixedDimension = array.getInt(R.styleable.RatioImageView_fixedDimension, DEFAULT_FIXED_DIMENSION)
        array.recycle()
    }

    fun setImageSize(imageSize: ImageSize) {
        this.imageSize = imageSize
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // get aspect ratio from ImageSize

        var aspectRatio = DEFAULT_ASPECT_RATIO
        if (imageSize != null) {
            aspectRatio = imageSize!!.width.toFloat() / imageSize!!.height
        }

        // set dimensions according to aspect ratio

        when (fixedDimension) {
            FIXED_WIDTH -> {
                val width = measuredWidth
                val height = (width / aspectRatio).toInt()

                setMeasuredDimension(width, height)
            }
            FIXED_HEIGHT -> {
                val height = measuredHeight
                val width = (height * aspectRatio).toInt()

                setMeasuredDimension(width, height)
            }
            else -> Log.d(TAG, "Invalid fixedDimension attribute, must be 0 for 'width' or 1 for 'height'.")
        }
    }

    companion object {

        private const val TAG = "RatioImageView"

        private const val FIXED_WIDTH = 0
        private const val FIXED_HEIGHT = 1

        private const val DEFAULT_FIXED_DIMENSION = FIXED_WIDTH
        private const val DEFAULT_ASPECT_RATIO = 1.0f
    }
}