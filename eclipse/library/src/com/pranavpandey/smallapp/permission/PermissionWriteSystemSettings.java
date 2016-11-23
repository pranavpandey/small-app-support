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

import com.pranavpandey.smallapp.R;
import com.pranavpandey.smallapp.SmallUtils;
import com.pranavpandey.smallapp.theme.DynamicTheme;
import com.pranavpandey.smallapp.theme.SmallTheme;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

@TargetApi(23)
public class PermissionWriteSystemSettings extends PermissionBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openPermissionSettings(Settings.ACTION_MANAGE_WRITE_SETTINGS);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPermissionScreenOpened()
                && Settings.System.canWrite(this)) {
            launchSmallApp();
        } else {
            SmallTheme.initializeInstance(getApplicationContext());
            buildPermissionsDialog();
        }
    }

    private void buildPermissionsDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.sas_dialog_permission,
                new LinearLayout(this), false);
        ViewGroup frame = (ViewGroup) view.findViewById(R.id.permission_frame);

        String label = getApplicationInfo().loadLabel(getPackageManager()).toString();
        ((TextView) view.findViewById(R.id.permission_message))
                .setText(String.format(getString(R.string.sas_format_next_line),
                        getString(R.string.sas_perm_request_desc),
                        String.format(getString(
                                R.string.sas_perm_write_system_settings_info), label)));

        frame.addView(new PermissionItem(this,
                ContextCompat.getDrawable(this, R.drawable.sas_ic_menu_settings),
                getString(R.string.sas_perm_write_system_settings),
                getString(R.string.sas_perm_write_system_settings_desc)));

        try {
            alertDialogBuilder.setIcon(DynamicTheme.createDialogIcon(this,
                    getPackageManager().getApplicationIcon(getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        alertDialogBuilder.setTitle(label)
                .setPositiveButton(R.string.sas_perm_continue,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        openPermissionSettings(Settings.ACTION_MANAGE_WRITE_SETTINGS);
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
