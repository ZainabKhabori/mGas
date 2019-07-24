package om.webware.mgas.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import om.webware.mgas.R;

/**
 * Created by Zainab on 7/9/2019.
 */

public class CircularImageView extends android.support.v7.widget.AppCompatImageView {

    private int borderColor;
    private float borderThickness;
    private boolean highlightEnabled;
    private int highlightColor;

    private RectF bitmapDrawBounds;
    private Paint bitmapPaint;
    private Bitmap bitmap;
    private BitmapShader shader;
    private Matrix matrix;

    private RectF borderBounds;
    private Paint borderPaint;

    private boolean pressed;
    private Paint highlightPaint;

    private boolean initialized;

    public CircularImageView(Context context) {
        super(context);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        setupBitmap();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        setupBitmap();
    }

    @Override
    public void setImageBitmap(@Nullable Bitmap bm) {
        super.setImageBitmap(bm);
        setupBitmap();
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        setupBitmap();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updateCircleDrawBounds(bitmapDrawBounds);

        float halfBorderThickness = borderPaint.getStrokeWidth() / 2;
        borderBounds.set(bitmapDrawBounds);
        borderBounds.inset(halfBorderThickness, halfBorderThickness);

        updateBitmapSize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawOval(bitmapDrawBounds, bitmapPaint);

        if(borderPaint.getStrokeWidth() > 0f) {
            canvas.drawOval(borderBounds, borderPaint);
        }

        if(highlightEnabled && pressed) {
            canvas.drawOval(bitmapDrawBounds, highlightPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean processed = false;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!isInCircle(event.getX(), event.getY())) {
                    return false;
                }

                processed = true;
                pressed = true;

                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                processed = true;
                pressed = false;

                invalidate();

                if(!isInCircle(event.getX(), event.getY())) {
                    return false;
                }
                break;
        }

        return super.onTouchEvent(event) || processed;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, 0, 0);

        borderColor = typedArray.getColor(R.styleable.CircularImageView_borderColor, Color.TRANSPARENT);
        borderThickness = typedArray.getDimensionPixelSize(R.styleable.CircularImageView_borderThickness, 0);
        highlightEnabled = typedArray.getBoolean(R.styleable.CircularImageView_highlightEnabled, false);
        highlightColor = typedArray.getColor(R.styleable.CircularImageView_highlightColor, Color.TRANSPARENT);

        typedArray.recycle();

        bitmapDrawBounds = new RectF();
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        matrix = new Matrix();

        borderBounds = new RectF();
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderThickness);

        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setColor(highlightColor);
        highlightPaint.setStyle(Paint.Style.FILL);

        initialized = true;

        setupBitmap();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if(drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getMinimumHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void updateCircleDrawBounds(RectF bounds) {
        float contentWidth = getWidth() - getPaddingStart() - getPaddingEnd();
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        float top = getPaddingTop();
        float start = getPaddingStart();

        if(contentWidth > contentHeight) {
            start += (contentWidth - contentHeight) / 2f;
        } else {
            top += (contentHeight - contentWidth) / 2f;
        }

        float diameter = Math.min(contentWidth, contentHeight);
        bounds.set(start, top, start + diameter, top + diameter);
    }

    private void setupBitmap() {
        if(initialized && getDrawable() != null) {
            bitmap = getBitmapFromDrawable(getDrawable());
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            bitmapPaint.setShader(shader);

            updateBitmapSize();
        }
    }

    private void updateBitmapSize() {
        float dx;
        float dy;
        float factor;

        if(bitmap.getWidth() < bitmap.getHeight()) {
            factor = bitmapDrawBounds.width() / (float) bitmap.getWidth();
            dx = bitmapDrawBounds.left;
            dy = bitmapDrawBounds.top - ((bitmap.getHeight() * factor) / 2f) + (bitmapDrawBounds.width() / 2f);
        } else {
            factor = bitmapDrawBounds.height() / (float) bitmap.getHeight();
            dx = bitmapDrawBounds.left - ((bitmap.getWidth() * factor) / 2f) + (bitmapDrawBounds.width() / 2f);
            dy = bitmapDrawBounds.top;
        }

        matrix.setScale(factor, factor);
        matrix.postTranslate(dx, dy);
        shader.setLocalMatrix(matrix);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isInCircle(float x, float y) {
        double xSquare = Math.pow(bitmapDrawBounds.centerX() - x, 2);
        double ySquare = Math.pow(bitmapDrawBounds.centerY() - y, 2);
        double distance = Math.sqrt(xSquare + ySquare);

        return distance <= (bitmapDrawBounds.width() / 2);
    }

    @ColorInt
    public int getBorderColor() {
        return borderPaint.getColor();
    }

    public void setBorderColor(@ColorInt int borderColor) {
        borderPaint.setColor(borderColor);
        invalidate();
    }

    @Dimension
    public float getBorderThickness() {
        return borderPaint.getStrokeWidth();
    }

    public void setBorderThickness(@Dimension float borderThickness) {
        borderPaint.setStrokeWidth(borderThickness);
        invalidate();
    }

    public boolean isHighlightEnabled() {
        return highlightEnabled;
    }

    public void setHighlightEnabled(boolean highlightEnabled) {
        this.highlightEnabled = highlightEnabled;
        invalidate();
    }

    @ColorInt
    public int getHighlightColor() {
        return highlightPaint.getColor();
    }

    public void setHighlightColor(@ColorInt int highlightColor) {
        highlightPaint.setColor(highlightColor);
        invalidate();
    }
}