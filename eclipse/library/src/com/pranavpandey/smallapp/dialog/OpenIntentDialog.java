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

package com.pranavpandey.smallapp.dialog;

import java.util.Collections;
import java.util.List;

import com.pranavpandey.smallapp.R;
import com.pranavpandey.smallapp.SmallUtils;
import com.pranavpandey.smallapp.database.Associations;
import com.pranavpandey.smallapp.view.ColoredImageView;
import com.pranavpandey.smallapp.view.ColoredTextView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A class which creates a dialog to show all the activities available to
 * handle the supplied intent. It is a solution to handle no activity
 * found exception and you can also do some other work if this exception
 * occurs. It extends the {@link ActionDialog} class so that you can use its
 * functions also.
 *
 * @see {@link android.app.AlertDialog}, {@link android.content.Intent}.
 */
public class OpenIntentDialog extends ActionDialog {

	/**
	 * Interface definition for a callback to be invoked when there will be
	 * no activity to handle this intent. Use this listener to notify user
	 * or to do some other operation.
	 */
	public interface OnNoActivityListener {

		/**
		 * Called when there is no activity to handle this intent.
		 */
		public void onNoActivityFound();
	}

	/**
	 * Interface definition for a callback to be invoked when an activity
	 * has been launched.
	 */
	public interface OnActivityOpenListener {

		/**
		 * Called when an activity has been launched.
		 *
		 * @param componentName The activity component that has been
		 * launched.
		 */
		public void onActivityOpen(ComponentName componentName);
	}

	/**
	 * Intent to query list of activities.
	 */
	private Intent mIntent;

	/**
	 * <code>true</code> if auto open when there is only
	 * <code>one</code> activity in the list.
	 */
	private boolean mOpenSingleApp = false;

	/**
	 * <code>true</code> if remember user selection to open same
	 * intent with the same app next time.
	 */
	private boolean mRememberSelection = false;

	/**
	 * <code>true</code> Force user to select the app even if it is
	 * already associated. Useful to work with open as file.
	 */
	private boolean mForceSelection = false;

	/**
	 * Listener used to notify that there is no activity to handle
	 * this intent.
	 */
	private @Nullable OnNoActivityListener mNoActivityListener;

	/**
	 * Listener used to notify which activity has been opened.
	 */
	private @Nullable OnActivityOpenListener mActivityOpenListener;

	/**
	 * PackageManager to perform activity operations.
	 */
	private PackageManager mPackageManager;

	/**
	 * Default intent type to save associations in case of open as
	 * operations. Save this type if it is not null;
	 */
	private String mDefaultIntentType;

	/**
	 * Constructor using a context, intent, alert dialog builder and dialog type to
	 * display an Action dialog which shows a list of activities that are available
	 * to handle the supplied intent.
	 */
	public OpenIntentDialog(Context context, Intent intent, AlertDialog.Builder dialogBuilder, Type type) {
		super(context, dialogBuilder, type);
		this.mIntent = intent;
		this.mPackageManager = getContext().getPackageManager();	
	}

	/**
	 * Creates a {@link ActionDialog} with the arguments supplied to the
	 * constructor by using the Activity adapter.
	 *
	 * @param rootView Root view to which the dialog should attach.
	 *
	 * @see {@link ActivityAdapter}.
	 */
	@Override
	public void createDialog(@NonNull View rootView) {
		List<ResolveInfo> launchables = mPackageManager.queryIntentActivities(mIntent,
				PackageManager.MATCH_DEFAULT_ONLY);

		if (launchables.isEmpty()) {
			setCustomDialog(true);

			if (mNoActivityListener != null) {
				mNoActivityListener.onNoActivityFound();
			} else {
				View mView = LayoutInflater.from(getContext()).inflate(R.layout.sas_dialog_warning_info,
						new LinearLayout(getContext()), false);
				((TextView) mView.findViewById(R.id.warning_message)).setText(R.string.sas_error_no_apps);

				if (isExtraInfo()) {
					((ColoredTextView) mView.findViewById(R.id.extra_info_desc)).setText(getExtraInfoText());
					((ColoredImageView) mView.findViewById(R.id.extra_info_icon))
										.setImageDrawable(getExtraInfoIcon());
				} else {
					((View) mView.findViewById(R.id.extra_info_layout)).setVisibility(View.GONE);
				}

				getDialogBuilder()
				.setView(mView)
				.setNegativeButton(null, null)
				.setNeutralButton(null, null)
				.setPositiveButton(android.R.string.ok, null);

				SmallUtils.createDialog(getDialogBuilder().create(), rootView.getWindowToken()).show();
			}
		} else {
			Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(mPackageManager));

			String associatedPackage = (new Associations(getContext()))
					.getHelper().get(mIntent.resolveType(getContext()));

			if (associatedPackage != null && !mForceSelection) {
				for (ResolveInfo resolveInfo: launchables) {
					if (resolveInfo.activityInfo.packageName.equals(associatedPackage)) {
						setCustomDialog(true);
						launchActivity(resolveInfo);
						return;
					}
				}
			}

			if (launchables.size() == 1 && mOpenSingleApp) {
				setCustomDialog(true);
				launchActivity(launchables.get(0));
			} else {
				if (mRememberSelection) {
					setCheckButtonLayout(R.string.sas_always_use, false, null);
				}

				ActivityAdapter adapter = new ActivityAdapter(getContext(), mPackageManager,
						getType() == Type.LIST ? R.layout.sas_row_list_action : R.layout.sas_item_grid_action);
				adapter.addAll(launchables);
				setAdapter(adapter, new OnActionItemClickListener() {
					@Override
					public void onActionItemClick(DialogInterface dialog, Adapter adapter,
							AdapterView<?> parent, View view, int position, long id) {
						launchActivity((ResolveInfo) adapter.getItem(position));

						if (isCheckButtonChecked()) {
							if (mForceSelection) {
								if (mDefaultIntentType != null) {
									(new Associations(getContext()))
									.getHelper().put(mDefaultIntentType,
											((ResolveInfo) adapter.getItem(position))
											.activityInfo.packageName);
								}
							} else {
								(new Associations(getContext()))
								.getHelper().put(mIntent.resolveType(getContext()),
										((ResolveInfo) adapter.getItem(position)).activityInfo.packageName);
							}
						}
					}
				});
				super.createDialog(rootView);
			}
		}
	}

	/**
	 * Launch a particular {@link android.app.Activity} from the supplied
	 * {@link android.content.pm.ResolveInfo}.
	 *
	 * @param resolveInfo ResolveInfo to set the component name.
	 *
	 * @see {@link android.content.ComponentName}.
	 */
	private void launchActivity(ResolveInfo resolveInfo) {
		ActivityInfo activity = resolveInfo.activityInfo;
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mIntent.setClassName(activity.applicationInfo.packageName, activity.name);
		getContext().startActivity(mIntent);

	    if (mActivityOpenListener != null) {
	    	mActivityOpenListener.onActivityOpen(new ComponentName(
	    			activity.applicationInfo.packageName, activity.name));
		}
	}

	/**
	 * Set the value of {@link #mOpenSingleApp}.
	 *
	 * @param isOpenSingleApp Value to be set. Either <code>true</code>
	 * or <code>false</code>.
	 *
	 * @return OpenIntentDialog object to allow for chaining of calls
	 * to set methods.
	 */
	public OpenIntentDialog setOpenSingleApp(boolean isOpenSingleApp) {
		mOpenSingleApp = isOpenSingleApp;
		return this;
	}

	/**
	 * Set the value of {@link #mRememberSelection}.
	 *
	 * @param isRememberSelection Value to be set. Either <code>true</code> or
	 * <code>false</code>.
	 *
	 * @return ActionDialog object to allow for chaining of calls to set
	 * methods.
	 */
	public OpenIntentDialog setRememberSelection(boolean isRememberSelection) {
		this.mRememberSelection = isRememberSelection;
		return this;
	}

	/**
	 * Set the value of {@link #mForceSelection}.
	 *
	 * @param isForceSelection Value to be set. Either <code>true</code> or
	 * <code>false</code>.
	 *
	 * @return ActionDialog object to allow for chaining of calls to set
	 * methods.
	 */
	public OpenIntentDialog setForceSelection(boolean isForceSelection) {
		this.mForceSelection = isForceSelection;
		return this;
	}

	/**
	 * Set the value of {@link #mDefaultIntentType}.
	 *
	 * @param defaultIntentType to save associations.
	 *
	 * @return ActionDialog object to allow for chaining of calls to set
	 * methods.
	 */
	public OpenIntentDialog setDefaultIntentType(String defaultIntentType) {
		this.mDefaultIntentType = defaultIntentType;
		return this;
	}

	/**
	 * Creates a {@link ActionDialog} with the arguments supplied to the constructor
	 * and {@link Dialog#show()} the dialog based on the user settings. It is not
	 * necessary that it will show a dialog each time. Please, read about
	 * {@link #mOpenSingleApp} and {@link OnNoActivityListener}.
	 *
	 * @param rootView Root view to which the dialog should attach.
	 */
	@Override
	public void show(@NonNull View rootView) {
		super.show(rootView);
	}

	/**
	 * Set a callback to be invoked when there will be no activity
	 * to handle the supplied intent.
	 *
	 * @param noActivityListener to dispatch event when there will
	 * be no activity to handle this intent.
	 *
	 * @return OpenIntentDialog object to allow for chaining of calls
	 * to set methods.
	 */
	public OpenIntentDialog setNoActivityListener(@Nullable OnNoActivityListener noActivityListener) {
		this.mNoActivityListener = noActivityListener;
		return this;
	}

	/**
	 * Set a callback to be invoked when when an activity has been
	 * opened to with the supplied intent.
	 *
	 * @param activityOpenListener to dispatch event when an
	 * activity has been opened to with the supplied intent.
	 *
	 * @return OpenIntentDialog object to allow for chaining of calls
	 * to set methods.
	 */
	public OpenIntentDialog setActivityOpenListener(@Nullable OnActivityOpenListener activityOpenListener) {
		this.mActivityOpenListener = activityOpenListener;
		return this;
	}

	/**
	 * @return The callback to be invoked when there will be no activity
	 * to handle this intent, or <code>null</code> if no callback has
	 * been set.
	 */
	@Nullable
	public OnNoActivityListener getNoActivityListener() {
		return mNoActivityListener;
	}

	/**
	 * @return The callback to be invoked when an activity has been
	 * opened to with the supplied intent, or <code>null</code> if
	 * no callback has been set.
	 */
	@Nullable
	public OnActivityOpenListener getActivityOpenListener() {
		return mActivityOpenListener;
	}

	/**
	 * Adapter to display activity item in <code>List</code>. It extends the
	 * ArrayAdapter and the list must contain {@link android.content.pm.ResolveInfo}.
	 *
	 * @see {@link android.widget.ArrayAdapter}.
	 */
	static class ActivityAdapter extends ArrayAdapter<ResolveInfo> {

		/**
		 * PackageManager to retrieve app name and icon.
		 */
		PackageManager packageManager = null;

		/**
		 * LayoutInflater to inflate row or item layout resource file.
		 */
		LayoutInflater mLayoutInflator;

		/**
		 * Row or item layout resource id.
		 */
		@LayoutRes int mLayoutId;

		ActivityAdapter(Context context, PackageManager pm, int layoutId) {
			super(context, 0);
			this.packageManager = pm;
			this.mLayoutId = layoutId;
			this.mLayoutInflator = LayoutInflater.from(getContext());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mLayoutInflator.inflate(mLayoutId, parent, false);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.name.setText(getItem(position).loadLabel(packageManager));
			viewHolder.icon.setImageDrawable(getItem(position).loadIcon(packageManager));

			return convertView;
		}

		/**
		 * Holder class to hold ImageView and TextView of the activity item.
		 */
		static class ViewHolder {

			/**
			 * ImageView to show icon of the activity.
			 */
			ImageView icon;

			/**
			 * TextView to show name of the activity.
			 */
			TextView name;

			/**
			 * Constructor using activity view to create {@link ViewHolder}.
			 * We will use this to access image and text view of the activity item.
			 *
			 * @see {@link #icon}, {@link #name}.
			 */
			public ViewHolder(View view) {
				icon = (ImageView) view.findViewById(R.id.action_icon);
				name = (TextView) view.findViewById(R.id.action_title);
			}
		}
	}
}
