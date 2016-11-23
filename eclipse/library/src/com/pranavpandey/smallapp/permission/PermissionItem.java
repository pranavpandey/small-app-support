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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PermissionItem extends LinearLayout {

    private ImageView mIcon;

    private TextView mName;

    public PermissionItem(Context context, Drawable icon, String permission, String description) {
        super(context);
        init();

        this.mName.setText(String.format(getContext().getString(R.string.sas_format_next_line),
                permission, description));
        this.mIcon.setImageDrawable(icon);
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sas_dialog_perm_info, this, true);

        mIcon = (ImageView) findViewById(R.id.perm_info_icon);
        mName = (TextView) findViewById(R.id.perm_info_desc);
    }
}
