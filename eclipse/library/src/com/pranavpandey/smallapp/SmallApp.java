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

package com.pranavpandey.smallapp;

import java.util.ArrayList;

import com.pranavpandey.smallapp.permission.PermissionDangerous;
import com.pranavpandey.smallapp.permission.PermissionSelectExternalStorage;
import com.pranavpandey.smallapp.permission.PermissionWriteSystemSettings;
import com.pranavpandey.smallapp.theme.SmallTheme;
import com.sony.smallapp.SmallAppWindow;
import com.sony.smallapp.SmallApplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Base small app class to make things easier. It extends the Sony
 * SmallApplication to inherit all of its features. For now, it has
 * very basic functionality but we can add more features later.
 */
@TargetApi(23)
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

    private static final int PERMISSIONS_CHECK_DELAY = 150;
    private static final int PERMISSIONS_CHECK_DELAY_NO_UI = 200;

    @Override
    protected void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        SmallTheme.initializeInstance(getContext());
        mConfig = new Configuration(getResources().getConfiguration());

        // Request runtime permissions if available.
        if (SmallUtils.isMarshmallow()) {
            final ArrayList<String> permissionsToGrant = new ArrayList<String>();
            if (getPermissions() != null) {
                for (int i = 0; i < getPermissions().length; i++) {
                    if (ContextCompat.checkSelfPermission(mContext,
                            getPermissions()[i]) != PackageManager.PERMISSION_GRANTED) {
                        permissionsToGrant.add(getPermissions()[i]);
                    }
                }
            }

            if (!permissionsToGrant.isEmpty()) {
                Intent intent = new Intent(mContext, PermissionDangerous.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(PermissionDangerous.PERMISSIONS,
                        permissionsToGrant.toArray(new String[permissionsToGrant.size()]));
                openDelayedActivity(intent);
            } else if (writeSystemSettings() &&
                    !Settings.System.canWrite(getContext())) {
                Intent intent = new Intent(mContext, PermissionWriteSystemSettings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                openDelayedActivity(intent);
            }
        }

        setContentView(R.layout.sas_main);
        mRootView = (ViewGroup) findViewById(R.id.frame_container);

        if (getLayoutId() != 0) {
            View layoutView = LayoutInflater.from(SmallApp.this).inflate(getLayoutId(), null);
            mRootView.addView(layoutView);
        }
    }

    private void openDelayedActivity(final Intent intent) {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(intent);
            }
        }, getLayoutId() != 0 ? PERMISSIONS_CHECK_DELAY
                : PERMISSIONS_CHECK_DELAY_NO_UI);

        return;
    }

    /**
     * Override this function in the extended class to provide a layout
     * which will be added to the root view.
     *
     * @see #mRootView
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

    protected String[] getPermissions() {
        return null;
    }

    protected boolean writeSystemSettings() {
        return false;
    }

    protected void requestSelectExternalStorage() {
        Intent intent = new Intent(getContext(), PermissionSelectExternalStorage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        windowMinimize();
    }
}
