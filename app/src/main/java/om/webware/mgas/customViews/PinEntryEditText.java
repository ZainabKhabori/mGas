package om.webware.mgas.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import om.webware.mgas.R;

@SuppressWarnings("ConstantConditions")
public class PinEntryEditText extends androidx.appcompat.widget.AppCompatEditText implements View.OnClickListener,
        ActionMode.Callback {

    private float spaceSize;  // Tied to spaceSize custom attribute, default value 24
    private boolean autoSpace;  // Tied to autoSpace custom attribute, default value false
    private float textElevation;  // Tied to textElevation custom attribute, default value 8

    private float numChars;  // Tied to the maxLength attribute, default value is 4

    private Paint linesPaint;
    private Paint textPaint;
    private ColorStateList colorStateList;

    private final static String XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";
    private final static String XML_NAMESPACE_MGAS = "http://schemas.android.com/apk/res-auto";

    public PinEntryEditText(Context context) {
        super(context);
    }

    public PinEntryEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PinEntryEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int availableWidth;
        int startX;
        if (getPaddingStart() > 0 || getPaddingEnd() > 0) {
            availableWidth = getWidth() - getCompoundPaddingStart() - getCompoundPaddingEnd();
            startX = getCompoundPaddingStart();
        } else {
            availableWidth = getWidth() - getCompoundPaddingRight() - getCompoundPaddingLeft();

            if(getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                startX = getCompoundPaddingRight();
            } else {
                startX = getCompoundPaddingLeft();
            }
        }

        float dashSize; // The size of the dash for the input spaceSize
        if(autoSpace) {
            dashSize = availableWidth / ((numChars * 2) - 1);
        } else {
            dashSize = (availableWidth - (spaceSize * (numChars - 1))) / numChars;
        }

        int bottom = getHeight() - getCompoundPaddingBottom();

        String input = getText().toString();
        int inputLength = input.length();
        float[] charWidths = new float[inputLength];
        getPaint().getTextWidths(input, 0, inputLength, charWidths);

        for(int i = 0; i < numChars; i++) {
            updateColorForLines(i == inputLength);
            canvas.drawLine(startX, bottom, startX + dashSize, bottom, linesPaint);

            if(input.length() > i) {
                float center = startX + (dashSize / 2);
                float textStart = center - (charWidths[0] / 2);
                canvas.drawText(input, i, i + 1, textStart, bottom - textElevation, textPaint);
            }

            if(autoSpace) {
                startX += (dashSize * 2);
            } else {
                startX += dashSize + spaceSize;
            }
        }
    }

    @Override
    public void onClick(View v) {
        setSelection(getText().length());
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //
    }

    private void init(Context context, AttributeSet attrs) {
        int bg = attrs.getAttributeResourceValue(XML_NAMESPACE_ANDROID, "background", 0);
        int tint = attrs.getAttributeResourceValue(XML_NAMESPACE_ANDROID, "backgroundTint",
                android.R.color.transparent);

        setBackgroundResource(bg);
        setBackgroundTintList(ContextCompat.getColorStateList(context, tint));

        int maxLength = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", 4);
        setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        numChars = maxLength;

        spaceSize = attrs.getAttributeFloatValue(XML_NAMESPACE_MGAS, "spaceSize", 24);
        autoSpace = attrs.getAttributeBooleanValue(XML_NAMESPACE_MGAS, "autoSpace", false);
        textElevation = attrs.getAttributeFloatValue(XML_NAMESPACE_MGAS, "textElevation", 8);

        if(tint != 0) {
            setBackgroundTintList(ContextCompat.getColorStateList(context, tint));
        }

        float density = context.getResources().getDisplayMetrics().density;
        spaceSize *= density;
        textElevation *= density;

        int[][] states = new int[][] {
                new int[] {android.R.attr.state_selected},
                new int[] {android.R.attr.state_focused},
                new int[] {-android.R.attr.state_focused}
        };

        int[] colours = new int[3];
        colours[0] = ContextCompat.getColor(context, R.color.colorAccent);
        colours[1] = ContextCompat.getColor(context, R.color.colorPrimary);
        colours[2] = ContextCompat.getColor(context, R.color.colorPrimary);

        colorStateList = new ColorStateList(states, colours);

        linesPaint = new Paint(getPaint());
        textPaint = new Paint(getPaint());

        linesPaint.setStrokeWidth(density * 2);

        int textColor = attrs.getAttributeResourceValue(XML_NAMESPACE_ANDROID, "textColor", Color.BLACK);

        if(textColor == Color.BLACK) {
            textPaint.setColor(textColor);
        } else {
            textPaint.setColor(ContextCompat.getColor(context, textColor));
        }

        super.setCustomSelectionActionModeCallback(this);
        super.setOnClickListener(this);
    }

    public void setSpaceSize(float spaceSize) {
        this.spaceSize = spaceSize;
    }

    public float getSpaceSize() {
        return spaceSize;
    }

    public void setAutoSpace(boolean autoSpace) {
        this.autoSpace = autoSpace;
    }

    public boolean isAutoSpace() {
        return autoSpace;
    }

    public float getTextElevation() {
        return textElevation;
    }

    public void setTextElevation(float textElevation) {
        this.textElevation = textElevation;
    }

    private int getColorForState(int... states) {
        return colorStateList.getColorForState(states, Color.GRAY);
    }

    private void updateColorForLines(boolean next) {
        if (isFocused()) {
            linesPaint.setColor(getColorForState(android.R.attr.state_focused));
            if (next) {
                linesPaint.setColor(getColorForState(android.R.attr.state_selected));
            }
        } else {
            linesPaint.setColor(getColorForState(-android.R.attr.state_focused));
        }
    }
}
