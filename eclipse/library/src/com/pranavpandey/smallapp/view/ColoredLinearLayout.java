/*
 * Copyright (C) 2016 Pranav Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pranavpandey.smallapp.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.pranavpandey.smallapp.theme.SmallTheme;
import com.pranavpandey.smallapp.R;

/**
 * A LinearLayout to change background according to the
 * supplied {@link com.pranavpandey.smallapp.R.attr#colorType}.
 */
public class ColoredLinearLayout extends LinearLayout {

    /**
     * Color type applied to this view.
     *
     * @see {@link com.pranavpandey.smallapp.theme.SmallTheme.ColorType}.
     */
    private int mColorType;

    /**
     * Background alpha for this view ranging from 0 - 255.
     */
    private int mColorAlpha;

    public ColoredLinearLayout(Context context) {
        super(context);

        init();
    }

    public ColoredLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColoredLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorAtrributes);
        mColorType = a.getInt(R.styleable.ColorAtrributes_colorType, 0);
        mColorAlpha = a.getInt(R.styleable.ColorAtrributes_colorAlpha, 255);
        a.recycle();

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColoredLinearLayout(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorAtrributes);
        mColorType = a.getInt(R.styleable.ColorAtrributes_colorType, 0);
        mColorAlpha = a.getInt(R.styleable.ColorAtrributes_colorAlpha, 255);
        a.recycle();

        init();
    }

    /**
     * Initialize this view by setting color type and
     * background alpha.
     *
     * @see {@link #mColorType}, {@link #mColorAlpha}.
     */
    private void init() {
        if (mColorType != 0) {
            int color = SmallTheme.getInstance().getColorFromType(mColorType);
            setBackgroundColor(Color.argb(mColorAlpha, Color.red(color),
                    Color.green(color), Color.blue(color)));
        }
    }

    /**
     * @return The value of {@link #mColorType}.
     */
    public int getColorType() {
        return mColorType;
    }

    /**
     * Set the value of {@link #mColorType} and
     * re-initialize this view.
     *
     * @param coloType for this view.
     */
    public void setColorType(int colorType) {
        this.mColorType = colorType;

        init();
    }

    /**
     * @return The value of {@link #mColorAlpha}.
     */
    public int getColorAlpha() {
        return mColorAlpha;
    }

    /**
     * Set the value of {@link #mColorAlpha} and
     * re-initialize this view.
     */
    public void setColorAlpha(int colorAlpha) {
        this.mColorAlpha = colorAlpha;

        init();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (getVisibility() == View.GONE) {
            return false;
        }

        if (isEnabled() && isClickable()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setAlpha(0.7f);
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                    || event.getAction() == MotionEvent.ACTION_UP) {
                setAlpha(1.0f);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                performClick();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
