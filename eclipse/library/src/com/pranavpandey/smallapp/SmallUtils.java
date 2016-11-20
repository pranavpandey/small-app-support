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

import java.text.DateFormat;

import com.pranavpandey.smallapp.dialog.ActionDialog.OnExtraInfoClickListener;
import com.pranavpandey.smallapp.dialog.ActionDialog.Type;
import com.pranavpandey.smallapp.dialog.OpenIntentDialog;
import com.pranavpandey.smallapp.dialog.OpenIntentDialog.OnActivityOpenListener;
import com.pranavpandey.smallapp.dialog.OpenIntentDialog.OnNoActivityListener;
import com.pranavpandey.smallapp.theme.SmallTheme;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * A helper class to save and load SharedPreferences. It also has some
 * other functions to open link and show hint if {@link SmallTheme} is
 * not initialized.
 */
public class SmallUtils {

	/**
	 * To detect if the current Android version is JellyBean or below.
	 *
	 * @return <code>true</code> If current version is greater than or equal to
	 * {@link Build.VERSION_CODES#JELLY_BEAN}.
	 */
	public static boolean isJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}
	
	/**
	 * To detect if the current Android version is Lollipop or below.
	 *
	 * @return <code>true</code> If current version is greater than or equal to
	 * {@link Build.VERSION_CODES#LOLLIPOP}.
	 */
	public static boolean isLollipop() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}
	
	/**
	 * To detect if the current Android version is M or below.
	 *
	 * @return <code>true</code> If current version is greater than or equal to
	 * {@link Build.VERSION_CODES#M}.
	 */
	public static boolean isMarshmallow() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	/**
	 * Set token manually for an AlertDialog so that we can display it
	 * from a non-UI ContextWrapper class (like service etc.).
	 *
	 * @param alertDialog to modify.
	 * @param windowToken Token of the ROOT_VIEW.
	 *
	 * @return Modified AlertDialog and is ready to call
	 * {@link android.app.AlertDialog#show()} from a {@link android.view.View}.
	 *
	 * @see {@link android.content.ContextWrapper}.
	 */
	public static AlertDialog createDialog(@NonNull AlertDialog alertDialog, @NonNull IBinder windowToken) {
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.token = windowToken;
		lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
		window.setAttributes(lp);
		return alertDialog;
	}
	
	/**
	 * Set token manually for an AlertDialog so that we can display it
	 * from a non-UI ContextWrapper class (like service etc.). It also sets
	 * a custom view for the dialog and handles top padding on Android L and 
	 * above devices.
	 *
	 * @param alertDialog to modify.
	 * @param windowToken Token of the ROOT_VIEW.
	 * @param view Custom view for alert dialog.
	 *
	 * @return Modified AlertDialog and is ready to call
	 * {@link android.app.AlertDialog#show()} from a {@link android.view.View}.
	 *
	 * @see {@link android.content.ContextWrapper}.
	 */
	public static AlertDialog createDialog(@NonNull AlertDialog alertDialog, 
			@NonNull IBinder windowToken, View view) {
		alertDialog.setView(view, 0, SmallUtils
				.getDialogTopPadding(alertDialog.getContext()), 0, 0);
		
		return createDialog(alertDialog, windowToken);
	}

	/**
	 * Set a boolean value in the Default SharedPreferences editor and call
	 * {@link android.content.SharedPreferences#commit()} to commit changes
	 * back from this editor.
	 *
	 * @param context to retrieve Default SharedPreferences.
	 * @param key The name of the preference to modify.
	 * @param value The new value for the preference.
	 *
	 * @see {@link android.preference.PreferenceManager#getDefaultSharedPreferences(Context)}.
	 */
	public static void savePrefs(Context context, String key, boolean value) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = sharedPrefs.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	/**
	 * Set an integer value in the Default SharedPreferences editor and call
	 * {@link android.content.SharedPreferences#commit()} to commit changes
	 * back from this editor.
	 *
	 * @param context to retrieve Default SharedPreferences.
	 * @param key The name of the preference to modify.
	 * @param value The new value for the preference.
	 *
	 * @see {@link android.preference.PreferenceManager#getDefaultSharedPreferences(Context)}.
	 */
	public static void savePrefs(Context context, String key, int value) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = sharedPrefs.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	/**
	 * Set a String value in the Default SharedPreferences editor and call
	 * {@link android.content.SharedPreferences#commit()} to commit changes
	 * back from this editor.
	 *
	 * @param context to retrieve Default SharedPreferences.
	 * @param key The name of the preference to modify.
	 * @param value The new value for the preference.
	 *
	 * @see {@link android.preference.PreferenceManager#getDefaultSharedPreferences(Context)}.
	 */
	public static void savePrefs(Context context, String key, String value) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = sharedPrefs.edit();
		edit.putString(key, value);
		edit.commit();
	}

	/**
	 * Retrieve a boolean value from the preferences.
	 *
	 * @param context to retrieve Default SharedPreferences.
	 * @param key The name of the preference to modify.
	 * @param value The new value for the preference.
	 *
	 * @return Returns the preference value if it exists, or defValue. Throws
	 * ClassCastException if there is a preference with this name that is not
	 * a boolean.
	 *
	 * @throws ClassCastException
	 *
	 * @see {@link android.preference.PreferenceManager#getDefaultSharedPreferences(Context)}.
	 */
	public static boolean loadPrefs(Context context, String key, boolean value) {
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return getPrefs.getBoolean(key, value);
	}

	/**
	 * Retrieve an integer value from the preferences.
	 *
	 * @param context to retrieve Default SharedPreferences.
	 * @param key The name of the preference to retrieve.
	 * @param defValue Value to return if this preference does not exist.
	 *
	 * @return Returns the preference value if it exists, or defValue. Throws
	 * ClassCastException if there is a preference with this name that is not
	 * a boolean.
	 *
	 * @throws ClassCastException
	 *
	 * @see {@link android.preference.PreferenceManager#getDefaultSharedPreferences(Context)}.
	 */
	public static int loadPrefs(Context context, String key, int value) {
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return getPrefs.getInt(key, value);
	}

	/**
	 * Retrieve a String value from the preferences.
	 *
	 * @param context to retrieve Default SharedPreferences.
	 * @param key Name of the preference to retrieve.
	 * @param defValue Value to return if this preference does not exist.
	 *
	 * @return Returns the preference value if it exists, or defValue. Throws
	 * ClassCastException if there is a preference with this name that is not
	 * a boolean.
	 *
	 * @throws ClassCastException
	 *
	 * @see {@link android.preference.PreferenceManager#getDefaultSharedPreferences(Context)}.
	 */
	public static String loadPrefs(Context context, String key, String value) {
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return getPrefs.getString(key, value);
	}

	/**
	 * Retrieve a Date and Time string from date milliSeconds based
	 * on system settings.
	 *
	 * @param context to retrieve system date and time format.
	 * @param milliSeconds to be converted into date and time.
	 *
	 * @return Formated date according to system settings.
	 *
	 * @see {@link java.util.Date}.
	 */
	public static String getDate(Context context, long milliSeconds) {
		DateFormat df = android.text.format.DateFormat.getDateFormat(context);
		DateFormat tf = android.text.format.DateFormat.getTimeFormat(context);
		
		return String.format(context.getResources().getString(R.string.sas_format_blank_space),
				df.format(milliSeconds), tf.format(milliSeconds));
	}

	/**
	 * Shows a toast message as hint, closer to the supplied view to
	 * mimic action bar hint method. To show hint above the view.
	 *
	 * @param context to retrieve resources.
	 * @param view for which hint to be shown.
	 * @param stringId Text id of the hint to be shown.
	 */
	public static void showHint(Context context, @NonNull View view, @StringRes int stringId) {
		showHint(context, view, context.getResources().getString(stringId));
	}

	/**
	 * Shows a toast message as hint, closer to the supplied view to
	 * mimic action bar hint method. To show hint above the view.
	 *
	 * @param context to retrieve resources.
	 * @param view for which hint to be shown.
	 * @param string Text to be shown as hint.
	 */
	public static void showHint(Context context, @NonNull View view, String string) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		Toast toast = Toast.makeText(view.getContext(), string, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.START, view.getLeft()
				- context.getResources().getDimensionPixelOffset(R.dimen.sas_hint_margin_left_right),
				location[1]
				- (int) (3.6 * context.getResources().getDimensionPixelOffset(R.dimen.sas_hint_margin_top_bottom)));
		toast.show();
	}

	/**
	 * Shows a toast message as hint, closer to the supplied view to
	 * mimic action bar hint method. To show hint below the view.
	 *
	 * @param context to retrieve resources.
	 * @param view for which hint to be shown.
	 * @param string Text to be shown as hint.
	 */
	public static void showHeaderHint(Context context, View view, String string) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		Toast toast = Toast.makeText(view.getContext(), string, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.START, view.getRight()
				+ context.getResources().getDimensionPixelOffset(R.dimen.sas_hint_margin_left_right),
				location[1] + context.getResources().getDimensionPixelOffset(R.dimen.sas_hint_margin_top_bottom));
		toast.show();
	}

	/**
	 * Shows a toast message as hint, closer to the supplied view to
	 * mimic action bar hint method. To show hint below the view.
	 *
	 * @param context to retrieve resources.
	 * @param view for which hint to be shown.
	 * @param stringId Text id of the hint to be shown.
	 */
	public static void showHeaderHint(Context context, @NonNull View view, @StringRes int stringId) {
		showHeaderHint(context, view, context.getResources().getString(stringId));
	}

	/**
	 * Tries to open the supplied link using {@link OpenIntentDialog}. If only
	 * one activity found then, it will directly open the link with that
	 * activity.
	 *
	 * @param context to retrieve resources.
	 * @param rootView to which the dialog should attach.
	 * If there is more than one activities available.
	 * @param link The link that should be open.
	 * @param exceptionLink The second link which should be tried if there is
	 * no activity found to open the first link.
	 * @param type The type of ActionDialog. Either <code>List</code> or
	 * <code>Grid</code> (if there is more than one app to open the link).
	 *
	 * @see {@link com.pranavpandey.smallapp.dialog.ActionDialog}.
	 */
	public static void openLink(final Context context, @NonNull final View rootView,
			@NonNull final String link, @Nullable final String exceptionLink, final Type type) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
		.setTitle(R.string.sas_open)
		.setNegativeButton(android.R.string.cancel, null);

		new OpenIntentDialog(context, intent, alertDialogBuilder, type)
		.setActivityOpenListener(new OnActivityOpenListener() {
			@Override
			public void onActivityOpen(ComponentName componentName) {
				((SmallApp) context).windowMinimize();
			}
		})
		.setNoActivityListener(new OnNoActivityListener() {
			@Override
			public void onNoActivityFound() {
				if (exceptionLink != null) {
					openLink(context, rootView, exceptionLink, null, type);
				}
			}
		})
		.setOpenSingleApp(true)
		.setExtraInfo(R.drawable.sas_ic_action_link, link, new OnExtraInfoClickListener() {
			@Override
			public void onExtraInfoClick(View view) {
				ClipboardManager clipboard = (ClipboardManager) 
						context.getSystemService(Context.CLIPBOARD_SERVICE);
		        ClipData clip = ClipData.newPlainText("Copied Text", link);
		        clipboard.setPrimaryClip(clip);
		        Toast.makeText(context, R.string.sas_copy_clipboard, Toast.LENGTH_SHORT).show();
			}
		})
		.show(rootView);
	}
	
	public static int getDialogTopPadding(Context context) {
		return isLollipop() ? 
				(int) (14 * context.getResources()
						.getDisplayMetrics().density)
				: 0;
	}
}
