/*
 * Copyright 2016 Pranav Pandey
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

package com.pranavpandey.smallapp.theme;

import java.util.Locale;

import com.pranavpandey.smallapp.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * A helper class to change colors dynamically.
 */
public class DynamicTheme {

    /**
     * Calculate tint based on a given color for better readability.
     *
     * @param color whose tint to be calculated.
     *
     * @return Tint of color.
     */
    public static @ColorInt int getTintColor(@ColorInt int color) {
        int finalColor;
        double tint;
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        double colorDarkness = getColorDarkness(color);
        boolean isColorDark;

        if (isColorDark = isColorDark(color)) {
            if (colorDarkness >= 0.5f && colorDarkness < 0.6f) {
                tint = 0.9f;
            } else if (colorDarkness >= 0.6f && colorDarkness < 0.65f) {
                tint = 0.8f;
            } else {
                tint = 0.7f;
            }
            finalColor = Color.argb(a, (int) (r + (tint * (255 - r))),
                    (int) (g + (tint * (255 - g))),
                    (int) (b + (tint * (255 - b))));
        } else {
            if (colorDarkness < 0.5f && colorDarkness >= 0.4f) {
                tint = 0.4f;
            } else if (colorDarkness < 0.4f && colorDarkness >= 0.3f) {
                tint = 0.5f;
            } else {
                tint = 0.6f;
            }
            finalColor = Color.argb((int) Math.min(1.33 * a, 255),
                    (int) (r * tint), (int) (g * tint), (int) (b * tint));
        }

        if (calculateContrast(color, finalColor) < 0.3f) {
            int newTint = 25;
            if (isColorDark) {
                newTint = 225;
            }
            finalColor = Color.argb(a, newTint, newTint, newTint);
        }
        return finalColor;
    }

    /**
     * Calculate accent based on a given color for dynamic theme generation.
     * Still in beta so, sometimes may be inaccurate colors.
     *
     * @param color whose accent to be calculated.
     *
     * @return Accent based on given color.
     */
    public static @ColorInt int getAccentColor(@ColorInt int color) {
        int finalColor;
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        double Y = ((r * 299) + (g * 587) + (b * 114)) / 1000;

        int rc = b ^ 0x55;
        int gc = g & 0xFA;
        int bc = r ^ 0x55;

        finalColor = Color.argb(a, rc, gc, bc);
        int r1 = Color.red(finalColor);
        int g1 = Color.green(finalColor);
        int b1 = Color.blue(finalColor);
        double YC = ((r1 * 299) + (g1 * 587) + (b1 * 114)) / 1000;

        int CD = (Math.max(r, r1) - Math.min(r, r1)) + (Math.max(g, g1) - Math.min(g, g1))
                + (Math.max(b, b1) - Math.min(b, b1));
        if ((Y - YC <= 50) && CD <= 200) {
            rc = b ^ 0xFA;
            gc = g & 0x55;
            bc = r ^ 0x55;
        }

        finalColor = Color.argb(a, rc, gc, bc);
        return finalColor;
    }

    /**
     * Calculate contrast of a color based on the give base color so
     * that it will be visible always on top of the base color.
     *
     * @param color whose contrast to be calculated.
     * @param color Background color to calculate contrast.
     *
     * @return Contrast of the given color according to the base color.
     */
    public static @ColorInt int getContrastColor(@ColorInt int color,
                                                 @ColorInt int contrastWith) {
        if (calculateContrast(contrastWith, color) < 0.3f) {
            return DynamicTheme.getTintColor(color);
        }

        return color;
    }

    /**
     * Detect light or dark color.
     *
     * @param color whose darkness to be calculated.
     *
     * @return <code>true</code> if color is dark.
     */
    public static boolean isColorDark(@ColorInt int color) {
        return getColorDarkness(color) >= 0.5;
    }

    /**
     * Calculate darkness of a color.
     *
     * @param color whose darkness to be calculated.
     *
     * @return Darkness of color. Less that or equal to 1. 0 for white and 1 for black.
     */
    public static double getColorDarkness(@ColorInt int color) {
        return 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color)
                + 0.114 * Color.blue(color)) / 255;
    }

    /**
     * Calculate luma value according to XYZ color space of a color.
     *
     * @param color whose XyzLuma to be calculated.
     *
     * @return luma value according to to XYZ color space in the range 0.0 - 1.0.
     */
    private static float calculateXyzLuma(@ColorInt int color) {
        return (0.2126f * Color.red(color) +
                0.7152f * Color.green(color) +
                0.0722f * Color.blue(color)) / 255f;
    }

    /**
     * Calculate color contrast difference between two colors based
     * on luma value according to XYZ color space.
     *
     * @param color1 First color to calculate contrast difference.
     * @param color2 Second color to calculate contrast difference.
     *
     * @return color contrast between the two colors.
     *
     * @see #calculateXyzLuma(int)
     */
    public static float calculateContrast(int color1, int color2) {
        return Math.abs(calculateXyzLuma(color1) - calculateXyzLuma(color2));
    }

    /**
     * Colorize and return the mutated drawable so that, all other references
     * do not change.
     *
     * @param drawable to be colorized.
     * @param color to colorize the drawable.
     *
     * @return Colorized drawable.
     */
    public static Drawable colorizeDrawable(Drawable drawable, @ColorInt int color) {
        drawable.mutate().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        return drawable;
    }

    /**
     * Colorize and return the mutated drawable so that, all other references
     * do not change.
     *
     * @param context to retrieve drawable resource.
     * @param drawableId Id of the drawable to be colorized.
     * @param color to colorize the drawable.
     *
     * @return Colorized drawable.
     */
    public static Drawable colorizeDrawableRes(Context context, @DrawableRes int drawableId,
                                               @ColorInt int color) {
        Drawable coloredDrawable = ContextCompat.getDrawable(context, drawableId);
        return colorizeDrawable(coloredDrawable, color);
    }

    /**
     * Highlight the query text within a TextView. Suitable for notifying user about the
     * searched query found in the adapter. TextView should not be empty. Please set your
     * default text first then, highlight the query text by using this function.
     *
     * @param query String to be highlighted.
     * @param textView to set the highlighted text.
     * @param color of the highlighted text.
     *
     * @see android.text.Spannable
     */
    public static void highlightQueryTextColor(@NonNull String query,
                                               @NonNull TextView textView, @ColorInt int color) {
        final String stringText = textView.getText().toString();
        if (!stringText.isEmpty() && stringText.toLowerCase(Locale.getDefault()).contains(query)) {
            final int startPos = stringText.toLowerCase(Locale.getDefault()).indexOf(query);
            final int endPos = startPos + query.length();

            Spannable spanText = Spannable.Factory.getInstance().newSpannable(stringText);
            spanText.setSpan(new ForegroundColorSpan(color), startPos, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            textView.setText(spanText, TextView.BufferType.SPANNABLE);
        }
    }

    /**
     * Highlight the query text within a TextView. Suitable for notifying user about the
     * searched query found in the adapter. TextView should not be empty. Please set your
     * default text first then, highlight the query text by using this function.
     *
     * @param query String to be highlighted.
     * @param textView to set the highlighted text.
     * @param colorId Color id of the highlighted text.
     *
     * @see android.text.Spannable
     */
    public static void highlightQueryTextColorRes(@NonNull String query,
                                                  @NonNull TextView textView,
                                                  @ColorRes int colorId) {
        final String stringText = textView.getText().toString();
        if (!stringText.isEmpty() && stringText.toLowerCase(Locale.getDefault()).contains(query)) {
            final int startPos = stringText.toLowerCase(Locale.getDefault()).indexOf(query);
            final int endPos = startPos + query.length();

            Spannable spanText = Spannable.Factory.getInstance().newSpannable(stringText);
            spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(
                    textView.getContext(), colorId)),
                    startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            textView.setText(spanText, TextView.BufferType.SPANNABLE);
        }
    }

    /**
     * Create a scaled drawable to use it properly as dialog icon.
     * It will also remove the issue of too big dialog icon.
     *
     * @param context to retrieve resources.
     * @param drawable to scale.
     *
     * @return Scaled drawable to use as dialog icon.
     */
    public static Drawable createDialogIcon(Context context, @NonNull Drawable drawable) {
        int widthHeight = context.getResources().getDimensionPixelSize(R.dimen.sas_dialog_icon_size);
        return new ScaleDrawable(drawable, 0, widthHeight, widthHeight).getDrawable();
    }

    /**
     * Create a scaled drawable to use it properly as dialog icon.
     * It will also remove the issue of too big dialog icon.
     *
     * @param context to retrieve resources.
     * @param drawableId to scale.
     *
     * @return Scaled drawable to use as dialog icon.
     */
    public static Drawable createDialogIcon(Context context, @DrawableRes int drawableId) {
        return createDialogIcon(context, ContextCompat.getDrawable(context, drawableId));
    }
}
