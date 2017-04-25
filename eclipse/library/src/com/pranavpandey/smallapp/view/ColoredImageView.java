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

import com.pranavpandey.smallapp.R;
import com.pranavpandey.smallapp.theme.DynamicTheme;
import com.pranavpandey.smallapp.theme.SmallTheme;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An ImageView to apply color filter according to the
 * supplied {@link com.pranavpandey.smallapp.R.attr#colorType}.
 */
public class ColoredImageView extends ImageView {

    /**
     * Color type applied to this view.
     *
     * @see com.pranavpandey.smallapp.theme.SmallTheme.ColorType
     */
    private int mColorType;

    /**
     * <code>true</code> if this view will change its color according
     * to the background. It was introduced to provide better legibility for
     * colored images and to avoid dark image on dark background like situations.
     *
     * <p>If this boolean is set then, it will check for the contrast color and
     * do color calculations according to that color so that this image view will
     * always be visible on that background. If no contrast color is found then,
     * it will take default background color.</p>
     *
     * @see #mContrastWith
     */
    private boolean mBackgroundAware;

    /**
     * Background color for this view so that it will remain in
     * contrast with this color.
     */
    private @ColorInt int mContrastWith;

    public ColoredImageView(Context context) {
        super(context);

        init();
    }

    public ColoredImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColoredImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorAtrributes);
        mColorType = a.getInt(R.styleable.ColorAtrributes_colorType, 0);
        mBackgroundAware = a.getBoolean(
                R.styleable.ColorAtrributes_backgroundAware, false);
        mContrastWith = a.getColor(R.styleable.ColorAtrributes_contrastWith,
                ContextCompat.getColor(getContext(), R.color.sas_default_color_contrast_with));
        a.recycle();

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColoredImageView(Context context, AttributeSet attrs, int defStyleAttr,
                            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorAtrributes);
        mColorType = a.getInt(R.styleable.ColorAtrributes_colorType, 0);
        mBackgroundAware = a.getBoolean(
                R.styleable.ColorAtrributes_backgroundAware, false);
        mContrastWith = a.getColor(R.styleable.ColorAtrributes_contrastWith,
                ContextCompat.getColor(getContext(), R.color.sas_default_color_contrast_with));
        a.recycle();

        init();
    }

    /**
     * Initialize this view by setting color type. If it is background
     * aware then, background color will also taken into account while
     * setting the color filter.
     *
     * @see #mColorType 
     * @see #mBackgroundAware
     */
    private void init() {
        if (mColorType != 0) {
            int filterColor = SmallTheme.getInstance().getColorFromType(mColorType);

            if (mBackgroundAware) {
                filterColor = DynamicTheme.getContrastColor(filterColor, mContrastWith);
            }

            setColorFilter(filterColor);
        } else {
            clearColorFilter();
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
     * @return <code>true</code> if this view changes color according
     * to the background.
     */
    public boolean isBackgroundAware() {
        return mBackgroundAware;
    }

    /**
     * Set the value of {@link #mBackgroundAware} and
     * re-initialize this view.
     *
     * @param backgroundAware <code>true</code> to make it background
     * aware.
     */
    public void setBackgroundAware(boolean backgroundAware) {
        this.mBackgroundAware = backgroundAware;

        init();
    }

    /**
     * @return The value of {@link #mContrastWith}.
     */
    public @ColorInt int getContrastWith() {
        return mContrastWith;
    }

    /**
     * Set the value of {@link #mContrastWith} and
     * re-initialize this view.
     *
     * @param contrastWith Color which should remain in contrast
     * with the image filter.
     */
    public void setContrastWith(@ColorInt int contrastWith) {
        this.mContrastWith = contrastWith;

        init();
    }
}
