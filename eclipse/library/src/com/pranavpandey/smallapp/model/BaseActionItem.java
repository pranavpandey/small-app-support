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

package com.pranavpandey.smallapp.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Base class for ActionItem to be used in the
 * {@link com.pranavpandey.smallapp.dialog.ActionDialog} along with the
 * {@link com.pranavpandey.smallapp.adapter.BaseActionItemAdapter}.
 *
 * <p>It has basic functionality like icon, title but, you can extend it to modify
 * according to your need.</p>
 */
public class BaseActionItem {

    /**
     * Unique Action ID to identify which action is selected by
     * the user.
     */
    private int mActionId;

    /**
     * Action name to describe what will happen on click.
     */
    private @StringRes int mNameId;

    /**
     * Action icon for better visibility and presentation.
     */
    private @DrawableRes int mIconId;

    /**
     * Set <code>true</code>  to change action name and icon color to
     * {@link com.pranavpandey.smallapp.theme.SmallTheme#primaryColor}.
     */
    private boolean isColorizable;

    /**
     * Constructor using Action Id, Name Id, Icon ID to create a {@link BaseActionItem}.
     * You can also make it colored by passing colorizable boolean.
     *
     * @see #mActionId
     * @see #mNameId
     * @see #mIconId
     * @see #isColorizable
     */
    public BaseActionItem(int actionId, @StringRes int nameId,
                          @DrawableRes int iconId, boolean colorizable) {
        this.mActionId = actionId;
        this.mNameId = nameId;
        this.mIconId = iconId;
        this.isColorizable = colorizable;
    }

    /**
     * Set the unique id to perform actions in the {@link BaseActionItem}.
     *
     * @param actionId Unique id of the action.
     *
     * @see {@link #mActionId}.
     */
    public void setActionId(int actionId) {
        this.mActionId = actionId;
    }

    /**
     * Set the name displayed in the {@link BaseActionItem}.
     *
     * @see {@link #mNameId}.
     */
    public int getNameId() {
        return mNameId;
    }

    /**
     * Set the icon displayed in the {@link BaseActionItem}.
     *
     * @param iconId Drawable id of the action.
     *
     * @see {@link #mIconId}.
     */
    public void setIconId(int iconId) {
        this.mIconId = iconId;
    }

    /**
     * Colorize action item to make it distinguishable.
     *
     * @param colorizable Set <code>true</code> to colorize the action.
     *
     * @see {@link #isColorizable}.
     */
    public void setColorizable(boolean colorizable) {
        this.isColorizable = colorizable;
    }

    /**
     * Gets the action id.
     *
     * @return {@link #mActionId}.
     */
    public int getActionId() {
        return mActionId;
    }

    /**
     * Gets the action name id. It will be a String resource so,
     * you have to decode it manually.
     *
     * @param nameId Text id of the action.
     *
     * @return {@link #mNameId}.
     */
    public void setNameId(int nameId) {
        mNameId = nameId;
    }

    /**
     * Gets the action icon id. It will be a Drawable resource so,
     * you have to decode it manually.
     *
     * @return {@link #mNameId}.
     */
    public int getIconId() {
        return mIconId;
    }

    /**
     * Gets whether the action item should be colored or not.
     *
     * @return <code>true</code> if, the action item should be colored.
     */
    public boolean isColorizable() {
        return isColorizable;
    }
}
