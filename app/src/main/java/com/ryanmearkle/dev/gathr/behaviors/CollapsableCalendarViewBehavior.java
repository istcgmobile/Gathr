package com.ryanmearkle.dev.gathr.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CollapsableCalendarViewBehavior extends AppBarLayout.Behavior {

        public CollapsableCalendarViewBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
            return false;/*super.onInterceptTouchEvent(parent, child, ev);*/
        }
}