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

import java.util.Iterator;
import java.util.List;

import com.pranavpandey.smallapp.R;
import com.sony.smallapp.SdkInfo;
import com.sony.smallapp.SmallApplicationManager;
import com.sony.smallapp.StartSmallAppConnection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

/**
 * A class with collection of helper functions and constants to make
 * launching of small app easier and from anywhere.
 */
public class SmallLauncher {

    /**
     * Constant for small app launcher category.
     */
    public static final String CATEGORY_LAUNCHER = "com.sony.smallapp.intent.category.LAUNCHER";

    /**
     * Constant for small app intent main.
     */
    public static final String ACTION_MAIN = "com.sony.smallapp.intent.action.MAIN";

    /**
     * Constant for start application intent action.
     */
    public static final String ACTION_START_APPLICATION = "com.sony.smallapp.intent.action.START_APPLICATION";

    /**
     * Constant small app permission.
     */
    public static final String PERMISSION_SMALLAPP = "com.sony.smallapp.permission.SMALLAPP";

    /**
     * Launch a small app from its package name.
     *
     * @param context to create launch intent.
     * @param packageManager to get package info.
     * @param packageName of the small app.
     *
     * @see {@link #getSmallAppIntent(Context, PackageManager, String)},
     * {@link #getSmallAppClassName(PackageManager, String)}.
     */
    public static boolean launchSmallApp(Context context, PackageManager packageManager,
                                         String packageName) {
        try {
            if (SdkInfo.VERSION.API_LEVEL >= 2) {
                SmallApplicationManager.startApplication(context,
                        getSmallAppIntent(context, packageManager, packageName));
            } else {
                Intent smallAppConnectionIntent = new Intent();
                smallAppConnectionIntent.setAction(ACTION_START_APPLICATION);
                smallAppConnectionIntent.setComponent(
                        new ComponentName("com.sony.smallapp.managerservice",
                        "com.sony.smallapp.managerservice.SmallAppManagerService"));
                StartSmallAppConnection localStartSmallAppConnection =
                        new StartSmallAppConnection(context,
                        new ComponentName(packageName, getSmallAppClassName(
                                packageManager, packageName)));
                context.startService(smallAppConnectionIntent);
                context.bindService(smallAppConnectionIntent, localStartSmallAppConnection, 0);
            }
            return true;
        } catch (Exception e) {
            Toast.makeText(context, R.string.sas_app_not_found, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Get intent to launch small app main from its package name.
     *
     * @param context to create launch intent.
     * @param packageManager to get package info.
     * @param packageName of the small app.
     *
     * @return Intent to launch small app.
     */
    public static Intent getSmallAppIntent(Context context, PackageManager packageManager,
                                           String packageName) {
        Intent smallIntent = new Intent(ACTION_MAIN);
        smallIntent.setPackage(packageName);
        smallIntent.addCategory(CATEGORY_LAUNCHER);

        List<?> list = packageManager.queryIntentServices(smallIntent, 0);
        if (list != null) {
            ResolveInfo resolveInfo = null;
            Iterator<?> iterator = list.iterator();
            while (iterator.hasNext()) {
                resolveInfo = (ResolveInfo) iterator.next();
            }

            if (resolveInfo != null) {
                Intent smallLaunchIntent = new Intent();
                smallLaunchIntent.setClass(context, resolveInfo.serviceInfo.getClass());
                smallLaunchIntent.setComponent(new ComponentName(packageName,
                        resolveInfo.serviceInfo.name));
                return smallLaunchIntent;
            }
        }
        return null;
    }

    /**
     * Extract small app main class name from its package name.
     *
     * @param packageManager to get package info.
     * @param packageName of the small app.
     *
     * @return Main class name of the small app.
     */
    public static String getSmallAppClassName(PackageManager packageManager, String packageName) {
        Intent smallIntent = new Intent(ACTION_MAIN);
        smallIntent.setPackage(packageName);
        smallIntent.addCategory(CATEGORY_LAUNCHER);

        List<?> list = packageManager.queryIntentServices(smallIntent, 0);
        if (list != null) {
            ResolveInfo resolveInfo = null;
            Iterator<?> iterator = list.iterator();
            while (iterator.hasNext()) {
                resolveInfo = (ResolveInfo) iterator.next();
            }

            if (resolveInfo != null) {
                return resolveInfo.serviceInfo.name;
            }
        }
        return null;
    }
}
