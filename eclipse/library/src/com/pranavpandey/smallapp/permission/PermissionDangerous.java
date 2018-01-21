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

package com.pranavpandey.smallapp.permission;

import java.util.ArrayList;

import com.pranavpandey.smallapp.R;
import com.pranavpandey.smallapp.SmallUtils;
import com.pranavpandey.smallapp.theme.DynamicTheme;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.M)
public class PermissionDangerous extends PermissionBase {

    public static final String PERMISSIONS = "permissions";

    public static final int PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions(getPermissions());
    }

    private void requestPermissions(String[] permissions) {
        if (getPermissions() != null) {
            ActivityCompat.requestPermissions(this, permissions,
                    PERMISSIONS_REQUEST_CODE);
        } else {
            finishPermissionsChecker();
        }
    }

    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(PERMISSIONS);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPermissionScreenOpened()) {
            launchSmallApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                final ArrayList<String> permissionsNotGranted = new ArrayList<String>();
                boolean isGrantedAllPermissions = true;
                boolean isAskForPermissionsAgain = false;

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isGrantedAllPermissions = false;
                        permissionsNotGranted.add(permissions[i]);
                    }

                    isAskForPermissionsAgain = isAskForPermissionsAgain ||
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                    PermissionDangerous.this, permissions[i]);
                }

                if (isGrantedAllPermissions) {
                    launchSmallApp();
                } else if (isAskForPermissionsAgain) {
                    buildPermissionsDialog(permissionsNotGranted, true);
                }

                if (!isAskForPermissionsAgain) {
                    buildPermissionsDialog(permissionsNotGranted, false);
                }
                break;
        }
    }

    private void buildPermissionsDialog(final ArrayList<String> permissions,
                                        final boolean isRequest) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.sas_dialog_permission,
                new LinearLayout(this), false);
        TextView message = (TextView) view.findViewById(R.id.permission_message);
        ViewGroup frame = (ViewGroup) view.findViewById(R.id.permission_frame);

        final ArrayList<String> permissionGroups = new ArrayList<String>();
        for (String permission: permissions) {
            try {
                PermissionInfo permInfo = getPackageManager()
                        .getPermissionInfo(permission, PackageManager.GET_META_DATA);
                if (!permissionGroups.contains(permInfo.group)) {
                    permissionGroups.add(permInfo.group);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (String permissionGroup: permissionGroups) {
            try {
                PermissionGroupInfo permGroupInfo = getPackageManager()
                        .getPermissionGroupInfo(permissionGroup, PackageManager.GET_META_DATA);
                frame.addView(new PermissionItem(this, permGroupInfo.loadIcon(getPackageManager()),
                        permGroupInfo.loadLabel(getPackageManager()).toString(),
                        permGroupInfo.loadDescription(getPackageManager()).toString()));
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (isRequest) {
            message.setText(R.string.sas_perm_request_desc);
        } else {
            message.setText(String.format(getString(R.string.sas_format_next_line),
                    getString(R.string.sas_perm_request_desc),
                    getString(R.string.sas_perm_request_info)));
        }

        try {
            alertDialogBuilder.setIcon(DynamicTheme.createDialogIcon(this,
                    getPackageManager().getApplicationIcon(getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        alertDialogBuilder.setTitle(getApplicationInfo().loadLabel(getPackageManager()).toString())
                .setPositiveButton(isRequest ? R.string.sas_perm_request
                        : R.string.sas_perm_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (isRequest) {
                            requestPermissions(permissions.toArray(new String[permissions.size()]));
                        } else {
                            openPermissionSettings(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finishPermissionsChecker();
                    }
                })
                .setCancelable(false);

        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.setView(view, 0, SmallUtils.getDialogTopPadding(this), 0, 0);

        showPermissionDialog(dialog);
    }
}
