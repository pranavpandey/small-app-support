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

package com.pranavpandey.smallapp.launcher;

import android.app.Activity;
import android.os.Bundle;

/**
 * An abstract activity to launch small app shortcuts form anywhere. 
 * Extend it in your project and override {@link #getShortcutPackage()}
 * method to pass a package name. Rest of the things will be handle by
 * the {@link com.pranavpandey.smallapp.launcher.SmallLauncher}.
 */
public abstract class ShortcutLauncher extends Activity {
	
	/**
	 * Key to get the package name from the intent.
	 */
	public static final String PACKAGE_NAME = "package_name";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		launchShortcut(getShortcutPackage());
	}
	
	/**
	 * Override this function to pass a package name of small app.
	 */
	protected abstract String getShortcutPackage();
	
	/**
	 * Launch the small app according to the passed package by using
	 * {@link com.pranavpandey.smallapp.launcher.SmallLauncher}.
	 */
	private void launchShortcut(String packageName) {
		if (packageName != null) {
			SmallLauncher.launchSmallApp(this, getPackageManager(), packageName);
		}
		
		finish();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		onDestroy();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
