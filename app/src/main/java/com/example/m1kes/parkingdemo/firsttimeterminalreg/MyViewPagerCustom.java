package com.example.m1kes.parkingdemo.firsttimeterminalreg;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by clive on 01/12/2016.
 * custom
 */

public class MyViewPagerCustom extends ViewPager {

    private boolean isPagingEnabled = true;

    public MyViewPagerCustom(Context context) {
        super(context);
    }

    public MyViewPagerCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
}

