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

package com.pranavpandey.smallapp;

import com.pranavpandey.smallapp.theme.SmallTheme;
import com.sony.smallapp.SmallAppWindow;
import com.sony.smallapp.SmallApplication;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Base small app class to make things easier. It extends the Sony
 * SmallApplication to inherit all of its features. FOr now, it has
 * very basic functionality but we can add more features later.
 */
public abstract class SmallApp extends SmallApplication {

	/**
	 * Context to retrieve resources.
	 */
	private Context mContext;

	/**
	 * Configuration to maintain orientation changes.
	 */
	private Configuration mConfig;

	/**
	 * Root view to show dialogs.
	 */
	private ViewGroup mRootView;

	@Override
	protected void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		SmallTheme.initializeInstance(getContext());
		mConfig = new Configuration(getResources().getConfiguration());

		setContentView(R.layout.sas_main);
		mRootView = (ViewGroup) findViewById(R.id.frame_container);

		if (getLayoutId() != 0) {
		    View layoutView = LayoutInflater.from(SmallApp.this).inflate(getLayoutId(), null);
		    mRootView.addView(layoutView);
		}
	}

	/**
	 * Override this function in the extended class to provide a layout
	 * which will be added to the root view.
	 *
	 * @see {@link #mRootView}.
	 */
	protected abstract @LayoutRes int getLayoutId();

	@Override
	protected boolean onSmallAppConfigurationChanged(Configuration newConfig) {
		int diff = newConfig.diff(mConfig);
		mConfig = new Configuration(getResources().getConfiguration());

		if ((diff & ActivityInfo.CONFIG_ORIENTATION | ActivityInfo.CONFIG_FONT_SCALE |
		    ActivityInfo.CONFIG_SCREEN_SIZE | ActivityInfo.CONFIG_KEYBOARD) != 0) {
		    return true;
		}
		return super.onSmallAppConfigurationChanged(newConfig);
	}

	/**
	 * Minimize the small app window.
	 */
	public void windowMinimize() {
		getWindow().setWindowState(SmallAppWindow.WindowState.MINIMIZED);
	}

	/**
	 * Set small app window to its general state.
	 */
	public void windowNormal() {
		getWindow().setWindowState(SmallAppWindow.WindowState.NORMAL);
	}

	/**
	 * Set small app window fitted to the screen.
	 */
	public void windowFitted() {
		getWindow().setWindowState(SmallAppWindow.WindowState.FITTED);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		SmallTheme.getInstance().onDestroy();
	}

	/**
	 * @return {@link #mContext}.
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * @return {@link #mRootView}.
	 */
	public View getRootView() {
		return mRootView;
	}
}
