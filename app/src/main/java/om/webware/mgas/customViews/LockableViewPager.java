package om.webware.mgas.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Zainab on 5/4/2019.
 */

public class LockableViewPager extends ViewPager {

    private boolean locked;

    public LockableViewPager(@NonNull Context context) {
        super(context);
    }

    public LockableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        locked = true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!locked) {
            return super.onTouchEvent(ev);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!locked) {
            return super.onInterceptTouchEvent(ev);
        }

        return false;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
