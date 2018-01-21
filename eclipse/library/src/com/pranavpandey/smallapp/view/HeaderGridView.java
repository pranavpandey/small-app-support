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

package com.pranavpandey.smallapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * A GridView that can be displayed inside a ScrollView in its
 * expanded form so that it occupies the maximum height which is
 * equivalent to WRAP_CONTENT.
 *
 * Normal GridView collapses when displayed inside a ScrollView. So,
 * this class will be helpful to provide ListView header like
 * functionality.
 */
public class HeaderGridView extends GridView {

    /**
     * <code>true</code> if, GridView is in the expanded form.
     */
    private boolean mIsExpanded = false;

    public HeaderGridView(Context context) {
        super(context);
    }

    public HeaderGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @return <code>true</code> if, GridView is in the expanded form.
     */
    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isExpanded()) {
            // Calculate entire height by providing a very large height hint.
            // But do not use the highest 2 bits of this integer; those are
            // reserved for the MeasureSpec mode.
            int expandSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Set whether the GridView should expand or not.
     *
     * @param expanded <code>true</code> to expand GridView.
     */
    public void setExpanded(boolean expanded) {
        this.mIsExpanded = expanded;
    }
}
