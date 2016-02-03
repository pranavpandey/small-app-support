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

package com.pranavpandey.smallapp.theme;

import com.pranavpandey.smallapp.R;
import com.pranavpandey.smallapp.SmallUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * A class to detect Primary and Accent colors form the theme. Only for Xperia devices
 * with ICS support. In ICS, it will return the Theme accent color.
 */
public class SmallTheme {

	/** 
	 * If no {@link #primaryColor} color is found then, return this color.
	 */
	private @ColorInt int defaultPrimaryColor = 0;
	
	/** 
	 * If no {@link #accentColor} color is found then, return this color.
	 */
	private @ColorInt int defaultAccentColor = 0;
	
	/** 
	 * If no {@link #primaryColorDark} color is found then, return this color.
	 */
	private @ColorInt int defaultPrimaryColorDark = 0;
	
	/** 
	 * If no {@link #accentColorDark} color is found then, return this color.
	 */
	private @ColorInt int defaultAccentColorDark = 0;
	
	/** 
	 * Extracted {@link android.R.attr#colorPrimary} from the current theme. 
	 */
	private @ColorInt int primaryColor = 0;
	
	/** 
	 * Extracted {@link android.R.attr#colorPrimaryDark} from the current theme. 
	 */
	private @ColorInt int primaryColorDark = 0;
		
	/** 
	 * Extracted {@link android.R.attr#colorAccent} from the current theme. 
	 */
	private @ColorInt int accentColor = 0;
	
	/** 
	 * Extracted {@link android.R.attr#colorAccent} from the current theme. 
	 */
	private @ColorInt int accentColorDark = 0;
	
	/** 
	 * Calculated tint color based on {@link #primaryColor}.
	 */
	private @ColorInt int tintPrimaryColor = 0;
	
	/** 
	 * Calculated tint color based on {@link #accentColor}.
	 */
	private @ColorInt int tintAccentColor = 0;
	
	/** 
	 * Calculated tint color based on {@link #primaryColorDark}.
	 */
	private @ColorInt int tintPrimaryColorDark = 0;
	
	/** 
	 * Calculated tint color based on {@link #accentColorDark}.
	 */
	private @ColorInt int tintAccentColorDark = 0;
	
	/** 
	 * Singleton instance of {@link com.pranavpandey.smallapp.theme.SmallTheme}.
	 */
	private static SmallTheme sInstance;
	
	/** 
	 * Context of activity or application to retrieve resources.
	 */
	private Context mContext;
	
	/** 
	 * Class to hold the color type constant values according to the
	 * {@link com.pranavpandey.smallapp.R.attr#colorType}.
	 */
	public static class ColorType {
		
		/** 
	 * Constant for no color.
	 */
	public static final int NONE = 0;
	
	/** 
	 * Constant for {@link SmallTheme#primaryColor}
	 */
	public static final int PRIMARY = 1;
	
	/** 
	 * Constant for {@link SmallTheme#primaryColorDark}
	 */
	public static final int PRIMARY_DARK = 2;
	
	/** 
	 * Constant for {@link SmallTheme#accentColor}
	 */
	public static final int ACCENT = 3;
	
	/** 
	 * Constant for {@link SmallTheme#accentColorDark}
	 */
	public static final int ACCENT_DARK = 4;
	
	/** 
	 * Constant for {@link SmallTheme#tintPrimaryColor}
	 */
	public static final int TINT_PRIMARY = 5;
	
	/** 
	 * Constant for {@link SmallTheme#tintPrimaryColorDark}
	 */
	public static final int TINT_PRIMARY_DARK = 6;
	
	/** 
	 * Constant for {@link SmallTheme#tintAccentColor}
	 */
	public static final int TINT_ACCENT = 7;
	
	/** 
	 * Constant for {@link SmallTheme#tintAccentColorDark}
	 */
		public static final int TINT_ACCENT_DARK = 8;
	}
	
	/**
	 * Cannot be called directly, use initialize instance. Constructor 
	 * using context to initialize theme when application starts.
	 */
	public SmallTheme(Context context) {
		this.mContext = context;
		this.defaultPrimaryColor = resolveColor(R.attr.sasDefaultColorPrimary, 
				ContextCompat.getColor(mContext, R.color.sas_default_color_primary));
		this.defaultAccentColor = resolveColor(R.attr.sasDefaultColorAccent, 
				ContextCompat.getColor(mContext, R.color.sas_default_color_accent));
		this.defaultPrimaryColorDark = resolveColor(R.attr.sasDefaultColorPrimaryDark, 
				ContextCompat.getColor(mContext, R.color.sas_default_color_primary_dark));
		this.defaultAccentColorDark = resolveColor(R.attr.sasDefaultColorAccentDark, 
				ContextCompat.getColor(mContext, R.color.sas_default_color_accent_dark));
		
		initTheme();
	}
	
	/**
	 * Cannot be called directly, use initialize instance. Constructor using a context to 
	 * initialize theme when application starts with the ability to set default Primary and 
	 * Accent colors. Use it if you want to set different colors if no color is found. You 
	 * can pass following default colors to change them manually:-
	 *   
	 * {@link #defaultPrimaryColor}, {@link #defaultAccentColor}, {@link #defaultPrimaryColorDark}, 
	 * {@link #defaultAccentColorDark}.
	 */
	public SmallTheme(Context context, @ColorInt int defaultPrimaryColor, @ColorInt int defaultAccentColor, 
			@ColorInt int defaultPrimaryColorDark, @ColorInt int defaultAccentColorDark) {
		this.mContext = context;
		this.defaultPrimaryColor = defaultPrimaryColor;
		this.defaultAccentColor = defaultAccentColor;
		this.defaultPrimaryColorDark = defaultPrimaryColorDark;
		this.defaultAccentColorDark = defaultAccentColorDark;
		
		initTheme();
	}
	
	/**
	 * Initialize theme when application starts. Must be initialize once.
	 *  
	 * @param context Context to retrieve resources.
	 */
	public static synchronized void initializeInstance(Context context) {
		if (context == null) {
			throw new NullPointerException("Context should not be null");
		}
		
		if (sInstance == null) {
			sInstance = new SmallTheme(context);
		}
	}
	
	/**
	 * Initialize theme when application starts with the ability to set 
	 * default Primary and Accent colors. Use it if you want to set different 
	 * colors if no color is found. Must be initialize once.
	 *  
	 * @param context to retrieve resources. 
	 * @param defaultPrimaryColor Default Primary color. 
	 * @param defaultAccentColor Default Accent color. 
	 * @param defaultPrimaryColorDark Dark Default Primary color. 
	 * @param defaultAccentColorDark Dark Default Accent color.
	 * 
	 * @see {@link #defaultPrimaryColor}, {@link #defaultAccentColor}, 
	 * {@link #defaultPrimaryColorDark}, {@link #defaultAccentColorDark}.
	 */
	public static synchronized void initializeInstance(Context context, @ColorInt int defaultPrimaryColor, 
			@ColorInt int defaultAccentColor, @ColorInt int defaultPrimaryColorDark, 
			@ColorInt int defaultAccentColorDark) {
		if (context == null) {
			throw new NullPointerException("Context should not be null");
		}
		
		if (sInstance == null) {
			sInstance = new SmallTheme(context, defaultPrimaryColor, defaultAccentColor, 
					defaultPrimaryColorDark, defaultAccentColorDark);
		}
	}
	
	/**
	 * Initialize theme when application starts with the ability to set 
	 * default Primary and Accent colors. Use it if you want to set different 
	 * colors if no color is found. Must be initialize once.
	 *  
	 * @param context to retrieve resources. 
	 * @param defaultPrimaryColorId Default Primary color resource. 
	 * @param defaultAccentColorId Default Accent color resource.
	 * @param defaultPrimaryColorDarkId Dark Default Primary color resource. 
	 * @param defaultAccentColorDarkId Dark Default Accent color resource.
	 * 
	 * @see {@link #defaultPrimaryColor}, {@link #defaultAccentColor}, 
	 * {@link #defaultPrimaryColorDark}, {@link #defaultAccentColorDark}.
	 */
	public static synchronized void initializeInstanceRes(Context context, @ColorRes int defaultPrimaryColorId, 
			@ColorRes int defaultAccentColorId, @ColorRes int defaultPrimaryColorDarkId, 
			@ColorRes int defaultAccentColorDarkId) {
		if (context == null) {
			throw new NullPointerException("Context should not be null");
		}
		
		if (sInstance == null) {
			sInstance = new SmallTheme(context, 
					ContextCompat.getColor(context, defaultPrimaryColorId), 
					ContextCompat.getColor(context, defaultAccentColorId), 
					ContextCompat.getColor(context, defaultPrimaryColorDarkId), 
					ContextCompat.getColor(context, defaultAccentColorDarkId));
		}
	}
	
	/**
	 * Get instance to access public methods. Must be called before accessing methods.
	 *  
	 * @return {@link #sInstance} Singleton {@link SmallTheme} instance.
	 */
	public static synchronized SmallTheme getInstance() {
		if (sInstance == null) {
		    throw new IllegalStateException(SmallTheme.class.getSimpleName() +
		            " is not initialized, call initializeInstance(..) method first.");
		}
		
		return sInstance;
	}
	
	/**
	 * Initialize all colors. Can be used to re-generate all colors also.
	 */
	public void initTheme() {
		// Set Light colors
		setPrimaryColor();
		setAccentColor();
		setTintPrimaryColor();
		setTintAccentColor();
	
		// Set Dark colors
		setPrimaryColorDark();
		setAccentColorDark();
		setTintPrimaryColorDark();
		setTintAccentColorDark();
	}
	
	/**
	 * Retrieve current primary color form the theme.
	 * 
	 * @return {@link #primaryColor} if exists, otherwise {@link #defaultPrimaryColor}.
	 */
	public int getPrimaryColor() {
		return primaryColor;
	}
	
	/**
	 * Retrieve current dark primary color form the theme.
	 * 
	 * @return {@link #primaryColorDark} if exists, otherwise {@link #defaultPrimaryColorDark}.
	 */
	public @ColorInt int getPrimaryColorDark() {
		return primaryColorDark;
	}
	
	/**
	 * Retrieve current accent color form the theme.
	 * 
	 * @return {@link #accentColor} if exists, otherwise {@link #defaultAccentColor}.
	 */
	public @ColorInt int getAccentColor() {
		return accentColor;
	}
	
	/**
	 * Retrieve current dark accent color form the theme.
	 * 
	 * @return {@link #accentColorDark} if exists, otherwise {@link #defaultAccentColorDark}.
	 */
	public @ColorInt int getAccentColorDark() {
		return accentColorDark;
	}
	
	/**
	 * Retrieve calculated tint color based on {@link #primaryColor}.
	 * 
	 * @return {@link #tintPrimaryColor}.
	 */
	public @ColorInt int getTintPrimaryColor() {
		return tintPrimaryColor;
	}
	
	/**
	 * Retrieve calculated tint color based on {@link #primaryColorDark}.
	 * 
	 * @return {@link #tintPrimaryColorDark}.
	 */
	public @ColorInt int getTintPrimaryColorDark() {
		return tintPrimaryColorDark;
	}
	
	/**
	 * Retrieve calculated tint color based on {@link #accentColor}.
	 * 
	 * @return {@link #tintAccentColor}.
	 */
	public @ColorInt int getTintAccentColor() {
		return tintAccentColor;
	}
	
	/**
	 * Retrieve calculated tint color based on {@link #accentColorDark}.
	 * 
	 * @return {@link #tintAccentColorDark}.
	 */
	public @ColorInt int getTintAccentColorDark() {
		return tintAccentColorDark;
	}
	
	/**
	 * Retrieve all colors in an integer array.
	 * 
	 * @return An array of colors containing {@link #primaryColor}, {@link #accentColor}, 
	 * {@link #tintPrimaryColor}, {@link #tintAccentColor}, {@link #primaryColorDark}, 
	 * {@link #accentColorDark}, {@link #tintPrimaryColorDark}, {@link #tintAccentColorDark}.
	 */
	public int[] getAllColors() {
		return new int[] { getPrimaryColor(), getAccentColor(), getTintPrimaryColor(), getTintAccentColor(), 
				getPrimaryColorDark(), getAccentColorDark(), getTintPrimaryColorDark(), getTintAccentColorDark() }; 
	}
	
	/**
	 * Retrieve all light colors in an integer array.
	 * 
	 * @return An array of colors containing {@link #primaryColor}, {@link #accentColor}, 
	 * {@link #tintPrimaryColor}, {@link #tintAccentColor}.
	 */
	public int[] getAllLightColors() {
		return new int[] { getPrimaryColor(), getAccentColor(), getTintPrimaryColor(),  getTintAccentColor() }; 
	}
	
	/**
	 * Retrieve all dark colors in an integer array.
	 * 
	 * @return An array of colors containing {@link #primaryColorDark}, {@link #accentColorDark}, 
	 * {@link #tintPrimaryColorDark}, {@link #tintAccentColorDark}.
	 */
	public int[] getAllDarkColors() {
		return new int[] { getPrimaryColorDark(), getAccentColorDark(), 
				getTintPrimaryColorDark(),  getTintAccentColorDark() }; 
	}
	
	/**
	 * Extract {@link android.R.attr#colorPrimary} from the theme if exists, otherwise 
	 * set it to {@link #defaultPrimaryColor}.  
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void setPrimaryColor() {		
		int colorID = 0;
		
		if (SmallUtils.isLollipop()) {
		    primaryColor = resolveColor(android.R.attr.colorPrimary, 0);
		} else {
			colorID = mContext.getResources().getIdentifier(
					"semc_theme_accent_color", "color", "com.sonyericsson.uxp");
		}
				    
		if (colorID != 0) {
			primaryColor = ContextCompat.getColor(mContext, colorID);
		}
		
		if (primaryColor == 0 || primaryColor == android.graphics.Color.WHITE) {
			primaryColor = defaultPrimaryColor;	
		}
	}
	
	/**
	 * Extract {@link android.R.attr#colorPrimaryDark} from the theme if exists, otherwise 
	 * set it to {@link #defaultPrimaryColorDark}.  
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void setPrimaryColorDark() {		
		int colorID = 0;
		
		if (SmallUtils.isLollipop()) {
		    primaryColorDark = resolveColor(android.R.attr.colorPrimaryDark, 0);
		} else {
			colorID = mContext.getResources().getIdentifier(
					"semc_theme_accent_color", "color", "com.sonyericsson.uxp");
		}
				    
		if (colorID != 0) {
			primaryColorDark = ContextCompat.getColor(mContext, colorID);
		}
		
		if (primaryColorDark == 0 || primaryColorDark == android.graphics.Color.WHITE) {
			primaryColorDark = defaultPrimaryColorDark;	
		}
	}
	
	/**
	 * Extract {@link android.R.attr#colorAccent} from the theme if exists, otherwise 
	 * set it to {@link #defaultAccentColor}.  
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void setAccentColor() {
		if (SmallUtils.isLollipop()) {
		    accentColor = resolveColor(android.R.attr.colorAccent, 0);
		}
		
		if (accentColor == 0 || accentColor == android.graphics.Color.WHITE) {
			accentColor = SmallUtils.isLollipop() ? defaultAccentColor : primaryColor;	
		}
	}
	
	/**
	 * Extract {@link android.R.attr#colorAccent}  from the theme if exists, otherwise 
	 * set it to {@link #defaultAccentColorDark}.  
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void setAccentColorDark() {
		if (SmallUtils.isLollipop()) {
		    accentColorDark = resolveColor(android.R.attr.colorAccent, 0);
		}
		
		if (accentColor == 0 || accentColor == android.graphics.Color.WHITE) {
			accentColorDark = SmallUtils.isLollipop() ? defaultAccentColorDark : primaryColorDark;	
		}
	}
	
	/**
	 * Calculate tint color based on {@link #primaryColor} 
	 * for better readability.  
	 */
	private void setTintPrimaryColor() {
		tintPrimaryColor = DynamicTheme.getTintColor(primaryColor); 
	}
	
	/**
	 * Calculate tint color based on {@link #primaryColorDark}
	 * for better readability.  
	 */
	private void setTintPrimaryColorDark() {
		tintPrimaryColorDark = DynamicTheme.getTintColor(primaryColorDark); 
	}
	
	/**
	 * Calculate tint color based on {@link #accentColor} 
	 * for better readability.  
	 */
	private void setTintAccentColor() {
		tintAccentColor = DynamicTheme.getTintColor(accentColor); 
	}
	
	/**
	 * Calculate tint color based on {@link #accentColorDark} 
	 * for better readability.  
	 */
	private void setTintAccentColorDark() {
		tintAccentColorDark = DynamicTheme.getTintColor(accentColorDark); 
	}
	
	/**
	 * Get color according to the {@link ColorType}.
	 * 
	 * @param colorType Color attribute from {@link com.pranavpandey.smallapp.R.attr#colorType}.
	 * 
	 * @return Color based on the attribute.
	 */
	public int getColorFromType(int colorType) {
		switch (colorType) {
			case ColorType.PRIMARY: return getPrimaryColor();
			case ColorType.PRIMARY_DARK: return getPrimaryColorDark();
			case ColorType.ACCENT: return getAccentColor();
			case ColorType.ACCENT_DARK: return getAccentColorDark();
			case ColorType.TINT_PRIMARY: return getTintPrimaryColor();
			case ColorType.TINT_PRIMARY_DARK: return getTintPrimaryColorDark();
			case ColorType.TINT_ACCENT: return getTintAccentColor();
			case ColorType.TINT_ACCENT_DARK: return getTintAccentColorDark();
			default: return resolveColor(android.R.attr.textColorPrimary, 0);
		}
	}
	
	/**
	 * Shows a toast message as hint, closer to the supplied view to
	 * mimic action bar hint method.
	 * 
	 * @param view for which hint to be shown.
	 * @param stringId of the hint to be shown.
	 */
	public void showHint(@NonNull View view, @StringRes int stringId) {
		SmallUtils.showHint(mContext, view, mContext.getResources().getString(stringId));
	}
	
	/**
	 * Shows a toast message as hint, closer to the supplied view to
	 * mimic action bar hint method. To show hint above the view.
	 * 
	 * @param view for which hint to be shown.
	 * @param string to be shown as hint.
	 */
	public void showHint(@NonNull View view, String string) {
		SmallUtils.showHint(mContext, view, string);
	}
	
	/**
	 * Shows a toast message as hint, closer to the supplied view to
	 * mimic action bar hint method. To show hint below the view.
	 * 
	 * @param view for which hint to be shown.
	 * @param string to be shown as hint.
	 */
	public void showHeaderHint(@NonNull View view, String string) {
		SmallUtils.showHeaderHint(mContext, view, string);
	}
	
	/**
	 * Shows a toast message as hint, closer to the supplied view to
	 * mimic action bar hint method. To show hint below the view.
	 * 
	 * @param view for which hint to be shown.
	 * @param stringId of the hint to be shown.
	 */
	public void showHeaderHint(@NonNull View view, @StringRes int stringId) {
		SmallUtils.showHeaderHint(mContext, view, mContext.getResources().getString(stringId));
	}
	
	/**
	 * Extract the supplied color attribute value from the theme.
	 * 
	 * @param attr Color attribute whose value should be extracted.
	 * @param defValue Value to return if the attribute is not defined or not a resource.
	 * 
	 * @color Value of the supplied attribute.
	 */
	public int resolveColor(int attr, int defValue) {
		TypedArray a = mContext.getTheme().obtainStyledAttributes(new int[] { attr }); 
		try {
			return a.getColor(0, defValue);
		} finally {
			a.recycle();
		}
	}
	
	/**
	 * Set {@link #sInstance} to null when small app exits for better theme
	 * results when theme is changed. 
	 */
	public void onDestroy() {
		sInstance = null;
	}
}
