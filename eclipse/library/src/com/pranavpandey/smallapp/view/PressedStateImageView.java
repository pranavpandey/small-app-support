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
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * An ImageView which changes alpha on touch to show pressed state.
 * It is extended from {@link com.pranavpandey.smallapp.view.ColoredImageView} to provide
 * colorizing abilities whenever is required.
 */
public class PressedStateImageView extends ColoredImageView {

	public PressedStateImageView(Context context) {
		super(context);
	}

	public PressedStateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PressedStateImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public PressedStateImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
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

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		setAlpha(enabled ? 1.0f : 0.7f);
	}
}
