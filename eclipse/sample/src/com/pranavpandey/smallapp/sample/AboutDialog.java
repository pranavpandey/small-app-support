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

package com.pranavpandey.smallapp.sample;

import com.pranavpandey.smallapp.sample.R;
import com.pranavpandey.smallapp.SmallUtils;
import com.pranavpandey.smallapp.dialog.ActionDialog.Type;
import com.pranavpandey.smallapp.theme.DynamicTheme;
import com.pranavpandey.smallapp.theme.SmallTheme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A class which creates a dialog to show app and developer info. Please modify
 * about.xml and {@link #createDialog(View)} function according to your need.
 *
 * @see {@link android.app.AlertDialog}.
 */
public class AboutDialog {

    /**
     * Context to create dialog.
     */
    private Context mContext;

    /**
     * App name to be displayed.
     */
    private String mAppName;

    /**
     * App big icon.
     */
    private int mAppIcon;

    /**
     * Dialog icon to be displayed before title.
     */
    private Drawable mDialogIcon;

    /**
     * About dialog to be displayed.
     */
    private AlertDialog mDialog;

    /**
     * Type of the ActionDialog. Either <code>List</code> or
     * <code>Grid</code> if there is more than one activity.
     */
    private Type mType;

    /**
     * Constructor using a context, app icon (drawableId) and app name to display an
     * About dialog which shows information about app and developer.
     */
    public AboutDialog(Context context, @DrawableRes int appIcon,
                       @NonNull String appName, Type type) {
        this.mContext = context;
        this.mAppIcon = appIcon;
        this.mAppName = appName;
        this.mType = type;

    }

    /**
     * Constructor using a context, app icon (drawableId) and app name (stringRes) to
     * display an About dialog which shows information about app and developer.
     */
    public AboutDialog(Context context, @DrawableRes int appIcon,
                       @StringRes int appNameId, Type type) {
        this.mContext = context;
        this.mAppIcon = appIcon;
        this.mAppName = context.getResources().getString(appNameId);
        this.mType = type;
    }

    /**
     * Creates a {@link AboutDialog} with the arguments supplied to the constructor
     * and {@link Dialog#show()} the dialog.
     *
     * @param rootView Root view to which the dialog should attach.
     */
    public void show(@NonNull View rootView) {
        createDialog(rootView);
        mDialog.show();
    }

    /**
     * Creates a {@link AboutDialog} with the arguments supplied to the constructor.
     * Modify this function to open your links. It is the only method to change
     * default links.
     *
     * @param rootView Root view to which the dialog should attach.
     *
     * @return An {@link android.app.AlertDialog} with app and developer info.
     */
    private void createDialog(@NonNull final View rootView) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.sas_dialog_about,
                new LinearLayout(mContext), false);

        ((ImageView) view.findViewById(R.id.img_app)).setImageResource(mAppIcon);
        ((TextView) view.findViewById(R.id.txt_name)).setText(mAppName);
        String version;

        try {
            version = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            version = new String();
        }
        ((TextView) view.findViewById(R.id.txt_version)).setText(String.format(
                mContext.getString(R.string.sas_version), version));

        view.findViewById(R.id.web_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SmallUtils.openLink(mContext, rootView,
                        "http://www.pranavpandey.com", null, mType);
                dismissDialog();
            }
        });

        view.findViewById(R.id.facebook_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SmallUtils.openLink(mContext, rootView,
                        "fb://facewebmodal/f?href=" + "https://www.facebook.com/PranavPandeDev",
                        "https://www.facebook.com/pranavpandedev", mType);
                dismissDialog();
            }
        });

        view.findViewById(R.id.twitter_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SmallUtils.openLink(mContext, rootView,
                        "twitter://user?user_id=630336695",
                        "https://twitter.com/pranavpandeydev", mType);
                mDialog.dismiss();
            }
        });

        view.findViewById(R.id.google_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SmallUtils.openLink(mContext, rootView,
                        "https://plus.google.com/+pranavpandeydev", null, mType);
                dismissDialog();
            }
        });

        view.findViewById(R.id.linkedin_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SmallUtils.openLink(mContext, rootView,
                        "https://in.linkedin.com/in/pranav-pandey-09032974", null, mType);
                dismissDialog();
            }
        });

        view.findViewById(R.id.github_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SmallUtils.openLink(mContext, rootView,
                        "https://github.com/pranavpandey", null, mType);
                dismissDialog();
            }
        });

        builder.setTitle(R.string.sas_about);
        builder.setView(view);
        if (mDialogIcon != null) {
            builder.setIcon(DynamicTheme.colorizeDrawable(mDialogIcon,
                    SmallTheme.getInstance().getPrimaryColor()));
        }

        builder.setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.sas_more_apps,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SmallUtils.openLink(mContext, rootView,
                                        "market://search?q=pub:Pranav+Pandey",
                                        "http://play.google.com/store/apps/dev?id=6608630615059087491",
                                        mType);
                                dismissDialog();
                            }
                        });

        mDialog = SmallUtils.createDialog(builder.create(),
                rootView.getWindowToken(), view);
    }

    /**
     * Get About dialog to do further operations.
     *
     * @return {@link #mDialog}.
     */
    public AlertDialog getDialog() {
        return mDialog;
    }

    /**
     * Dismiss the dialog.
     */
    public void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * Set the {@link Drawable} to be used in the title.
     *
     * @param icon Drawable to be used in the title.
     *
     * @return AboutDialog object to allow for chaining of calls to set
     * methods.
     */
    public AboutDialog setIcon(Drawable icon) {
        this.mDialogIcon = icon;
        return this;
    }

    /**
     * Set the {@link Drawable} to be used in the title.
     *
     * @param iconId DrawableId to be used in the title.
     *
     * @return AboutDialog object to allow for chaining of calls to set
     * methods.
     */
    public void setIcon(int iconId) {
        setIcon(ContextCompat.getDrawable(mContext, iconId));
    }
}
