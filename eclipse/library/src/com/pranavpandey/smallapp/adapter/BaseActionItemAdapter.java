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

package com.pranavpandey.smallapp.adapter;

import java.util.List;

import com.pranavpandey.smallapp.R;
import com.pranavpandey.smallapp.model.BaseActionItem;
import com.pranavpandey.smallapp.theme.SmallTheme;
import com.pranavpandey.smallapp.theme.SmallTheme.ColorType;
import com.pranavpandey.smallapp.view.ColoredImageView;
import com.pranavpandey.smallapp.view.ColoredTextView;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Base adapter to display action item in <code>List</code> or <code>Grid</code>. It extends the
 * ArrayAdapter and the list must contain {@link com.pranavpandey.smallapp.model.BaseActionItem}.
 * You can modify this class according to your item.
 *
 * @see com.pranavpandey.smallapp.dialog.ActionDialog.Type
 * @see android.widget.ArrayAdapter
 */
public class BaseActionItemAdapter extends ArrayAdapter<BaseActionItem> {

    /**
     * List with {@link com.pranavpandey.smallapp.model.BaseActionItem} to
     * show them in the adapter.
     */
    private List<BaseActionItem> actionsList;

    /**
     * Context to retrieve resources.
     */
    private Context mContext;

    /**
     * LayoutInflater to inflate row or item layout resource file.
     */
    private LayoutInflater mLayoutInflator;

    /**
     * Row or item layout resource id.
     */
    private @LayoutRes int mLayoutId;

    /**
     * Constructor using context, list of actions, and layout id to create
     * {@link BaseActionItemAdapter}. It will work with both <code>LIST</code>
     * and <code>GRID</code> types.
     *
     * @see com.pranavpandey.smallapp.dialog.ActionDialog.Type
     */
    public BaseActionItemAdapter(Context context, List<BaseActionItem> list,
                                 @LayoutRes int layoutId) {
        super(context, 0, list);
        this.mContext = context;
        this.actionsList = list;
        this.mLayoutId = layoutId;
        this.mLayoutInflator = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflator.inflate(mLayoutId, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BaseActionItem action = (BaseActionItem) actionsList.get(position);

        viewHolder.icon.setImageResource(action.getIconId());
        viewHolder.name.setText(mContext.getString(action.getNameId()));

        if (action.isColorizable()) {
            viewHolder.name.setColorType(ColorType.PRIMARY);
            viewHolder.icon.setColorType(ColorType.PRIMARY);
        } else {
            viewHolder.name.setTextColor(SmallTheme.getInstance().getColorFromType(ColorType.NONE));
            viewHolder.icon.setColorType(ColorType.NONE);
        }

        return convertView;
    }

    /**
     * Holder class to hold ImageView and TextView of the action item.
     */
    static class ViewHolder {

        /**
         * ImageView to show icon of the action item.
         */
        ColoredImageView icon;

        /**
         * TextView to show name of the action item.
         */
        ColoredTextView name;

        /**
         * Constructor using item view to create {@link ViewHolder}.
         * We will use this to access image and text view of the action item.
         *
         * @see #icon
         * @see #name
         */
        ViewHolder(View v) {
            icon = (ColoredImageView) v.findViewById(R.id.action_icon);
            name = (ColoredTextView) v.findViewById(R.id.action_title);
        }
    }
}
