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

package com.pranavpandey.smallapp.permission;

import com.pranavpandey.smallapp.launcher.SmallLauncher;
import com.pranavpandey.smallapp.theme.SmallTheme;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

public abstract class PermissionBase extends Activity {

    protected final int REQUEST_CODE_STORAGE_ACCESS = 1;
    protected static String PREF_EXTERNAL_STORAGE_URI = "pref_external_storage_uri";

    private boolean mPermissionScreenOpened = false;

    private AlertDialog mPermissionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SmallTheme.initializeInstance(getApplicationContext());
    }

    public boolean isPermissionScreenOpened() {
        return mPermissionScreenOpened;
    }

    public void launchSmallApp() {
        SmallLauncher.launchSmallApp(this, getPackageManager(), getPackageName());
        finish();
    }

    public void finishPermissionsChecker() {
        SmallTheme.getInstance().onDestroy();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            mPermissionDialog.dismiss();
        }
        mPermissionDialog = null;
    }

    protected void openPermissionSettings(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        mPermissionScreenOpened = true;
    }

    public void showPermissionDialog(AlertDialog dialog) {
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            mPermissionDialog.dismiss();
        }
        mPermissionDialog = dialog;
        mPermissionDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void triggerStorageAccessFramework() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_STORAGE_ACCESS);
        mPermissionScreenOpened = true;
    }
}
