package fr.insapp.insapp.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import fr.insapp.insapp.R;
import fr.insapp.insapp.models.ImageSize;

/**
 * Created by thomas on 23/07/2017.
 *
 * Based on https://gist.github.com/JakeWharton/2856179
 */

public class RatioImageView extends AppCompatImageView {

    private static final String TAG = "RatioImageView";

    private static final int FIXED_WIDTH = 0;
    private static final int FIXED_HEIGHT = 1;

    private static final int DEFAULT_FIXED_DIMENSION = FIXED_WIDTH;
    private static final float DEFAULT_ASPECT_RATIO = 1.0f;

    private ImageSize imageSize;
    private int fixedDimension;

    public RatioImageView(Context context) {
        super(context);

        fixedDimension = DEFAULT_FIXED_DIMENSION;
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // get attributes

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        fixedDimension = array.getInt(R.styleable.RatioImageView_fixedDimension, DEFAULT_FIXED_DIMENSION);
        array.recycle();
    }

    public void setImageSize(ImageSize imageSize) {
        this.imageSize = imageSize;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // get aspect ratio from ImageSize

        float aspectRatio = DEFAULT_ASPECT_RATIO;
        if (imageSize != null) {
            aspectRatio = ((float) imageSize.getWidth() / imageSize.getHeight());
        }

        // set dimensions according to aspect ratio

        if (fixedDimension == FIXED_WIDTH) {
            final int width = getMeasuredWidth();
            final int height = (int) (width / aspectRatio);

            setMeasuredDimension(width, height);
        }
        else if (fixedDimension == FIXED_HEIGHT) {
            final int height = getMeasuredHeight();
            final int width = (int) (height * aspectRatio);

            setMeasuredDimension(width, height);
        }
        else {
            Log.d(TAG, "Invalid fixedDimension attribute, must be 0 for 'width' or 1 for 'height'.");
        }
    }
}