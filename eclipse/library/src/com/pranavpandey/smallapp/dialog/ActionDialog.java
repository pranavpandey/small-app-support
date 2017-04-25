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

package com.pranavpandey.smallapp.dialog;

import com.pranavpandey.smallapp.R;
import com.pranavpandey.smallapp.SmallUtils;
import com.pranavpandey.smallapp.view.ColoredImageView;
import com.pranavpandey.smallapp.view.ColoredTextView;
import com.pranavpandey.smallapp.view.HeaderGridView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

/**
 * A class which creates a dialog to show different actions to perform various
 * operations by using an adapter containing all the actions which will be displayed
 * either in a <code>List</code> or <code>Grid</code>. You can use Base action
 * item adapter or any other custom adapter according to your need. Set a click
 * listener to dispatch click events so that you can perform actions.
 *
 * <p>You can also set extra info to display as header of <code>List</code> or <code>Grid</code>
 * which is also clickable.</p>
 *
 * @see android.app.AlertDialog
 * @see com.pranavpandey.smallapp.adapter.BaseActionItemAdapter
 * @see OnActionItemClickListener
 * @see #setExtraInfo(int, int, OnExtraInfoClickListener)
 * @see OnExtraInfoClickListener
 */
public class ActionDialog {

    /**
     * Interface definition for a callback to be invoked when an action
     * item is clicked.
     */
    public interface OnActionItemClickListener {

        /**
         * Called when an action item is clicked.
         *
         * @param dialog The dialog where the selection was made.
         * @param adapter The adapter in this AdapterView.
         * @param parent The AdapterView where the click happened.
         * @param view The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter).
         * @param position The position of the view in the adapter.
         * @param id The row id of the item that was clicked.
         */
        public void onActionItemClick(DialogInterface dialog, Adapter adapter,
                                      AdapterView<?> parent, View view, int position, long id);
    }

    /**
     * Interface definition for a callback to be invoked when extra
     * info is clicked.
     */
    public interface OnExtraInfoClickListener {

        /**
         * Called when extra info is clicked.
         *
         * @param view The view that was clicked.
         */
        public void onExtraInfoClick(View view);
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button in check button layout changed.
     */
    public interface OnCheckButtonChangedListener {

        /**
         * Called when the checked state of a compound button in the check button
         * layout has changed.
         *
         * @param view The view that was clicked.
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         * @param textView The text view to update description according to the state
         * of the compound button.
         */
        public void onCheckButtonChanged(View view, CompoundButton buttonView,
                                         boolean isChecked, TextView textView);
    }

    /**
     * Enum to define the type of this dialog. Either a
     * <code>List</code> or <code>Grid</code>.
     *
     * @see Type#LIST
     * @see Type#GRID
     */
    public static enum Type {

        /**
         * Actions will be displayed in the vertical list.
         *
         * @see android.widget.ListView
         */
        LIST,

        /**
         * Actions will be displayed in the vertical Grid.
         *
         * @see android.widget.GridView
         */
        GRID
    }

    /**
     * Context to retrieve resources and to build the
     * final {@link ActionDialog}.
     */
    private Context mContext;

    /**
     * Dialog to be displayed.
     */
    private AlertDialog mDialog;

    /**
     * AlertDialog builder to attach with the dialog. Provide
     * info about title, buttons, etc. in this builder.
     */
    private AlertDialog.Builder mDialogBuilder;

    /**
     * Type of the ActionDialog. Either <code>List</code> or
     * <code>Grid</code> based on {@link #Type}.
     */
    private Type mType;

    /**
     * View of this dialog.
     */
    private View mView;

    /**
     * <code>Grid</code> to display all the action items.
     */
    private HeaderGridView mActionGridView;

    /**
     * <code>List</code> to display all the action items.
     */
    private ListView mActionListView;

    /**
     * Adapter in the {@link Type#LIST} or {@link Type#GRID}.
     */
    private ListAdapter mAdapter;

    /**
     * Listener used to dispatch action item click events.
     */
    private @Nullable OnActionItemClickListener mActionItemListener;

    /**
     * Listener used to dispatch extra info click events.
     */
    private @Nullable OnExtraInfoClickListener mExtraInfoListener;

    /**
     * Listener used to dispatch check button layout click events.
     */
    private @Nullable OnCheckButtonChangedListener mCheckButtonListener;

    /**
     * Extra info icon.
     */
    private Drawable mExtraInfoIcon;

    /**
     * Extra info text.
     */
    private CharSequence mExtraInfoText;

    /**
     * Check button text.
     */
    private CharSequence mCheckButtonText;

    /**
     * <code>true</code> if extra info is set to display as header of
     * {@link Type#LIST} or {@link Type#GRID}.
     */
    private boolean mExtraInfo = false;

    /**
     * <code>true</code> if auto dismiss dialog when an action item
     * is clicked.
     */
    private boolean mAutoDismiss = true;

    /**
     * <code>true</code> ff a custom dialog will be displayed from
     * the extended class. It extends the functionality of this
     * dialog. It is well implemented in {@link OpenIntentDialog}.
     *
     * @see OpenIntentDialog#createDialog(View)
     */
    private boolean mCustomDialog = false;

    /**
     * <code>true</code> if check button layout is enabled. Enable it to
     * remember user selection for later use. It is well implemented in
     * {@link OpenIntentDialog}.
     *
     * @see OpenIntentDialog#createDialog(View)
     */
    private boolean mCheckButtonLayout = false;

    /**
     * Checked state of the button in check button layout.
     * <code>true</code> if it is checked.
     */
    private boolean mCheckButtonState = false;

    /**
     * LayoutInflater to inflate views.
     */
    private LayoutInflater mInflater;

    /**
     * Constructor using a context, alert dialog builder and the dialog type to
     * display an Action dialog which shows a set of actions to perform different
     * operations.
     *
     * @see android.app.AlertDialog.Builder
     * @see Type
     */
    public ActionDialog(Context context, AlertDialog.Builder dialogBuilder, Type type) {
        this.mContext = context;
        this.mDialogBuilder = dialogBuilder;
        this.mType = type;
        this.mInflater = LayoutInflater.from(mContext);

    }

    /**
     * Creates a {@link ActionDialog} with the arguments supplied to the
     * constructor by using the supplied adapter.
     *
     * @param rootView Root view to which the dialog should attach.
     */
    public void createDialog(@NonNull View rootView) {
        if (mType == Type.GRID) {
            mView = mInflater.inflate(R.layout.sas_dialog_action_grid,
                    new LinearLayout(mContext), false);
            mActionGridView = (HeaderGridView) mView.findViewById(R.id.action_grid);

            if (mExtraInfo) {
                ((ColoredTextView) mView.findViewById(R.id.extra_info_desc))
                        .setText(mExtraInfoText);
                ((ColoredImageView) mView.findViewById(R.id.extra_info_icon))
                        .setImageDrawable(mExtraInfoIcon);
                mActionGridView.setExpanded(true);

                if (mExtraInfoListener != null) {
                    mView.findViewById(R.id.extra_info_layout).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mExtraInfoListener.onExtraInfoClick(v);

                            autoDismiss();
                        }
                    });
                }

                mView.findViewById(R.id.extra_info_layout).setVisibility(View.VISIBLE);

                final ScrollView scrollView = (ScrollView)
                        mView.findViewById(R.id.action_grid_scroll);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, 0);
                    }
                });
            }

            mActionGridView.setAdapter(mAdapter);
            mActionGridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mActionItemListener != null) {
                        mActionItemListener.onActionItemClick(mDialog, mAdapter,
                                parent, view, position, id);
                    }

                    autoDismiss();
                }
            });
        } else {
            mView = mInflater.inflate(R.layout.sas_dialog_action_list,
                    new LinearLayout(mContext), false);
            mActionListView = (ListView) mView.findViewById(R.id.action_list);

            if (mExtraInfo) {
                View mHeader = mInflater.inflate(R.layout.sas_dialog_extra_info_list,
                        mActionListView, false);
                ((ColoredTextView) mHeader.findViewById(R.id.extra_info_desc))
                        .setText(mExtraInfoText);
                ((ColoredImageView) mHeader.findViewById(R.id.extra_info_icon))
                        .setImageDrawable(mExtraInfoIcon);

                mActionListView.addHeaderView(mHeader, null,
                        mExtraInfoListener != null ? true: false);
            }

            mActionListView.setAdapter(mAdapter);
            mActionListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mExtraInfo) {
                        position--;
                        if (position < 0) {
                            if (mExtraInfoListener != null) {
                                mExtraInfoListener.onExtraInfoClick(view);

                                autoDismiss();
                            }
                            return;
                        }
                    }

                    if (mActionItemListener != null) {
                        mActionItemListener.onActionItemClick(mDialog, mAdapter,
                                parent, view, position, id);
                    }

                    autoDismiss();
                }
            });
        }

        if (mCheckButtonLayout) {
            final TextView chkText = (TextView) mView.findViewById(R.id.check_text);
            final View chkLayout = (View) mView.findViewById(R.id.check_layout);
            final CompoundButton chkButton = (CompoundButton) mView.findViewById(R.id.check_button);

            chkText.setText(mCheckButtonText);
            chkLayout.setVisibility(View.VISIBLE);

            chkLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckButtonState = !chkButton.isChecked();
                    chkButton.setChecked(mCheckButtonState);

                    if (mCheckButtonListener != null) {
                        mCheckButtonListener.onCheckButtonChanged(chkLayout, chkButton,
                                chkButton.isChecked(), chkText);
                    }
                }
            });

            chkButton.setChecked(mCheckButtonState);
        }

        mDialog = SmallUtils.createDialog(mDialogBuilder.create(),
                rootView.getWindowToken(), mView);
    }

    /**
     * Set actions adapter for this dialog with on click listener to dispatch
     * click events. You can use Base action item adapter or any other custom
     * adapter according to your need.
     *
     * @param adapter Action adapter for this dialog.
     * @param actionItemListener Listener to dispatch click events.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     *
     * @see com.pranavpandey.smallapp.adapter.BaseActionItemAdapter
     * @see #getAdapter()
     */
    public ActionDialog setAdapter(@NonNull ListAdapter adapter,
                                   OnActionItemClickListener actionItemListener) {
        this.mAdapter = adapter;
        this.mActionItemListener = actionItemListener;
        return this;
    }

    /**
     * Set actions adapter for this dialog. You can use Base action item adapter
     * or any other custom adapter according to your need.
     *
     * @param adapter Action adapter for this dialog.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     *
     * @see com.pranavpandey.smallapp.adapter.BaseActionItemAdapter
     * @see #getAdapter()
     */
    public ActionDialog setAdapter(@NonNull ListAdapter adapter) {
        this.mAdapter = adapter;

        if (mType == Type.GRID) {
            if (mActionGridView != null) mActionGridView.setAdapter(adapter);
        } else {
            if (mActionListView != null) mActionListView.setAdapter(adapter);
        }
        return this;
    }

    /**
     * Set extra info to display as header of {@link Type#LIST} or {@link Type#GRID}.
     *
     * @param icon Extra info image drawable.
     * @param text Extra info text.
     * @param extraInfoListener Extra info listener to dispatch click events. Pass
     * <code>null</code> for no action.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     */
    public ActionDialog setExtraInfo(@Nullable Drawable icon, @Nullable CharSequence text,
                                     @Nullable OnExtraInfoClickListener extraInfoListener) {
        this.mExtraInfoIcon = icon;
        this.mExtraInfoText = text;
        this.mExtraInfoListener = extraInfoListener;
        this.mExtraInfo = true;
        return this;
    }

    /**
     * Set extra info to display as header of {@link Type#LIST} or {@link Type#GRID}.
     *
     * @param iconId Extra info image drawable id.
     * @param textId Extra info text id.
     * @param extraInfoListener Extra info listener to dispatch click events. Pass
     * <code>null</code> for no action.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     */
    public ActionDialog setExtraInfo(@DrawableRes int iconId, @StringRes int textId,
                                     @Nullable OnExtraInfoClickListener extraInfoListener) {
        this.mExtraInfoIcon = ContextCompat.getDrawable(mContext, iconId);
        this.mExtraInfoText = mContext.getText(textId);
        this.mExtraInfoListener = extraInfoListener;
        this.mExtraInfo = true;
        return this;
    }

    /**
     * Set extra info to display as header of {@link Type#LIST} or {@link Type#GRID}.
     *
     * @param iconId Extra info image drawable id.
     * @param text Extra info text.
     * @param extraInfoListener Extra info listener to dispatch click events. Pass
     * <code>null</code> for no action.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     */
    public ActionDialog setExtraInfo(@DrawableRes int iconId, @Nullable CharSequence text,
                                     OnExtraInfoClickListener extraInfoListener) {
        this.mExtraInfoIcon = ContextCompat.getDrawable(mContext, iconId);
        this.mExtraInfoText = text;
        this.mExtraInfoListener = extraInfoListener;
        this.mExtraInfo = true;
        return this;
    }

    /**
     * Set extra info to display as header of {@link Type#LIST} or {@link Type#GRID}.
     *
     * @param icon Extra info image drawable.
     * @param textId Extra info text id.
     * @param extraInfoListener Extra info listener to dispatch click events. Pass
     * <code>null</code> for no action.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     */
    public ActionDialog setExtraInfo(@Nullable Drawable icon, @StringRes int textId,
                                     @Nullable OnExtraInfoClickListener extraInfoListener) {
        this.mExtraInfoIcon = icon;
        this.mExtraInfoText = mContext.getText(textId);
        this.mExtraInfoListener = extraInfoListener;
        this.mExtraInfo = true;
        return this;
    }

    /**
     * Set check button layout to remember user selection.
     *
     * @param text Check button text.
     * @param defaultState The checked default state of the check button.
     * @param checkButtonListener Check button listener to dispatch state change events. Pass
     * <code>null</code> for no action.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     */
    public ActionDialog setCheckButtonLayout(@Nullable String text, boolean defaultState,
                                             @Nullable OnCheckButtonChangedListener checkButtonListener) {
        this.mCheckButtonText = text;
        this.mCheckButtonListener = checkButtonListener;
        this.mCheckButtonState = defaultState;
        this.mCheckButtonLayout = true;
        return this;
    }

    /**
     * Set check button layout to remember user selection.
     *
     * @param textId Check button text id.
     * @param defaultState The checked default state of the check button.
     * @param checkButtonListener Check button listener to dispatch state change events. Pass
     * <code>null</code> for no action.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     */
    public ActionDialog setCheckButtonLayout(@StringRes int textId, boolean defaultState,
                                             @Nullable OnCheckButtonChangedListener checkButtonListener) {
        return setCheckButtonLayout(mContext.getText(textId).toString(), defaultState,
                checkButtonListener);
    }

    /**
     * Set the value of {@link #mAutoDismiss}.
     *
     * @param isAutoDismiss Value to be set. Either <code>true</code> or <code>false</code>.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     */
    public ActionDialog setAutoDismiss(boolean isAutoDismiss) {
        this.mAutoDismiss = isAutoDismiss;
        return this;
    }

    /**
     * Set the value of {@link #mAutoDismiss}.
     *
     * @param isAutoDismiss Value to be set. Either <code>true</code> or <code>false</code>.
     *
     * @return ActionDialog object to allow for chaining of calls to set
     * methods.
     */
    public ActionDialog setCustomDialog(boolean isCustomDialog) {
        this.mCustomDialog = isCustomDialog;
        return this;
    }

    /**
     * Dismiss dialog if {@link #mAutoDismiss} is set to
     * <code>true</code>.
     */
    protected void autoDismiss() {
        if (mAutoDismiss) {
            dismissDialog();
        }
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
     * Creates a {@link ActionDialog} with the arguments supplied to the constructor
     * and {@link Dialog#show()} the dialog.
     *
     * @param rootView Root view to which the dialog should attach.
     */
    public void show(@NonNull View rootView) {
        createDialog(rootView);

        if (!mCustomDialog) {
            mDialog.show();
        }
    }

    /**
     * Get this dialog to do further operations.
     *
     * @return {@link #mDialog}.
     */
    public AlertDialog getDialog() {
        return mDialog;
    }

    /**
     * Get context to used to create this dialog.
     *
     * @return Context used to create this dialog.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Returns the adapter currently in use in the <code>List</code> or <code>Grid</code>.
     * The returned adapter might not be the same adapter passed to
     * {@link #setAdapter(ListAdapter)} but might be a {@link WrapperListAdapter}.
     *
     * @return The adapter currently used to display data in this ActionDialog.
     *
     * @see #setAdapter(ListAdapter)
     */
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * @return The callback to be invoked when an action item in this dialog
     * has been clicked, or <code>null</code> if no callback has been set.
     */
    @Nullable
    public OnActionItemClickListener getActionItemListener() {
        return mActionItemListener;
    }

    /**
     * @return The callback to be invoked when extra info in this dialog
     * has been clicked, or <code>null</code> if no callback has been set.
     */
    @Nullable
    public OnExtraInfoClickListener getExtraInfoListener() {
        return mExtraInfoListener;
    }

    /**
     * @return The type of the dialog. Either <code>LIST</code> or
     * <code>GRID</code>.
     */
    public Type getType() {
        return mType;
    }

    /**
     * @return The AlertDialog builder to set custom views.
     */
    public AlertDialog.Builder getDialogBuilder() {
        return mDialogBuilder;
    }

    /**
     * @return <code>true</code> if a custom dialog will be displayed by using
     * the values of this dialog.
     */
    public boolean isCustomDialog() {
        return mCustomDialog;
    }

    /**
     * @return <code>true</code> if extra info is set to display as header of
     * {@link Type#LIST} or {@link Type#GRID}.
     */
    public boolean isExtraInfo() {
        return mExtraInfo;
    }

    /**
     * @return If check button layout is enabled to remember user selection.
     */
    public boolean isCheckButtonLayout() {
        return mCheckButtonLayout;
    }

    /**
     * @return If check is checked to remember user selection.
     */
    public boolean isCheckButtonChecked() {
        return mCheckButtonState;
    }

    /**
     * @return The icon set to display as Extra info.
     */
    public Drawable getExtraInfoIcon() {
        return mExtraInfoIcon;
    }

    /**
     * @return The text set to display as Extra info.
     */
    public CharSequence getExtraInfoText() {
        return mExtraInfoText;
    }
}
