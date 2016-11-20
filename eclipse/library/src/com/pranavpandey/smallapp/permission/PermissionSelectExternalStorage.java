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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pranavpandey.smallapp.R;
import com.pranavpandey.smallapp.SmallUtils;
import com.pranavpandey.smallapp.theme.DynamicTheme;

@TargetApi(23)
public class PermissionSelectExternalStorage extends PermissionBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		buildPermissionsDialog();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public final void onActivityResult(final int requestCode,
			final int resultCode, final Intent resultData) {
	    if (requestCode == REQUEST_CODE_STORAGE_ACCESS) {
	        Uri treeUri = null;
	        if (resultCode == Activity.RESULT_OK) {
	            // Get Uri from Storage Access Framework.
	            treeUri = resultData.getData();

	            // Persist URI in shared preference so that you can use it later.
	            // Use your own framework here instead of PreferenceUtil.
	            SmallUtils.savePrefs(this, PREF_EXTERNAL_STORAGE_URI, treeUri.toString());
	            Toast.makeText(this, treeUri.toString(), Toast.LENGTH_LONG).show();

	            // Persist access permissions.
	            final int takeFlags = resultData.getFlags()
	                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
	                		| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
	            getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
	        }
	    }
	    finish();
	}

	private void buildPermissionsDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		View view = getLayoutInflater().inflate(R.layout.sas_dialog_permission,
				new LinearLayout(this), false);
		ViewGroup frame = (ViewGroup) view.findViewById(R.id.permission_frame);

		String label = getApplicationInfo().loadLabel(getPackageManager()).toString();
		((TextView) view.findViewById(R.id.permission_message))
    	.setText(String.format(getString(R.string.sas_format_next_line),
    				getString(R.string.sas_select_external_storage_request_desc),
    				String.format(getString(
    						R.string.sas_select_external_storage_request_info), label)));

		frame.addView(new PermissionItem(this,
				ContextCompat.getDrawable(this, R.drawable.sas_ic_select_storage),
				getString(R.string.sas_select_external_storage),
				getString(R.string.sas_select_external_storage_desc)));

	    try {
			alertDialogBuilder.setIcon(DynamicTheme.createDialogIcon(this,
					getPackageManager().getApplicationIcon(getPackageName())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		alertDialogBuilder.setTitle(label)
		.setPositiveButton(R.string.sas_perm_continue, new DialogInterface.OnClickListener() {
			@Override
    		public void onClick(DialogInterface dialog, int id) {
				triggerStorageAccessFramework();
            }
        })
    	.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int id) {
    			finish();
            }
        })
    	.setCancelable(false);

		final AlertDialog dialog = alertDialogBuilder.create();
		dialog.setView(view, 0, SmallUtils.getDialogTopPadding(this), 0, 0);

		showPermissionDialog(dialog);
	}
}
