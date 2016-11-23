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

package com.pranavpandey.smallapp.sample;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.pranavpandey.smallapp.SmallApp;
import com.pranavpandey.smallapp.SmallUtils;
import com.pranavpandey.smallapp.adapter.ViewPagerAdapter;
import com.pranavpandey.smallapp.database.Associations;
import com.pranavpandey.smallapp.dialog.ActionDialog.Type;
import com.pranavpandey.smallapp.dialog.OpenIntentDialog;
import com.pranavpandey.smallapp.dialog.OpenIntentDialog.OnActivityOpenListener;
import com.pranavpandey.smallapp.theme.SmallTheme;
import com.pranavpandey.smallapp.view.PagerSlidingTabStrip;
import com.sony.smallapp.SmallAppWindow;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

public class SmallAppSample extends SmallApp {

    private static final String SOURCES_LINK = "https://github.com/pranavpandey/small-app-support";

    @Override
    protected int getLayoutId() {
        return R.layout.main;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        // Set title for the small app
        setTitle(R.string.title);

        // Set header with a option menu
        setupOptionMenu();

        // Set windows attributes
        SmallAppWindow.Attributes attr = getWindow().getAttributes();
        attr.minWidth = getResources().getDimensionPixelSize(R.dimen.min_width);
        attr.minHeight = getResources().getDimensionPixelSize(R.dimen.min_height);
        attr.width = getResources().getDimensionPixelSize(R.dimen.width);
        attr.height = getResources().getDimensionPixelSize(R.dimen.height);

        attr.flags |= SmallAppWindow.Attributes.FLAG_RESIZABLE;
        getWindow().setAttributes(attr);

        // Setup view pager and tab indicator
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setBackgroundColor(SmallTheme.getInstance().getTintAccentColor());
        tabStrip.setIndicatorColor(SmallTheme.getInstance().getAccentColor());

        // Add pages
        View page2 = LayoutInflater.from(this).inflate(R.layout.page_two, (ViewGroup) null);
        ArrayList<View> pages = new ArrayList<View>();
        pages.add(LayoutInflater.from(this).inflate(R.layout.page_one, (ViewGroup) null));
        pages.add(page2);

        // Set adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter((List<View>) pages);
        viewPager.setAdapter(viewPagerAdapter);
        tabStrip.setViewPager(viewPager);

        // Setup search
        final EditText editSearch = (EditText) page2.findViewById(R.id.edit_search);
        final Button btnSearchClear = (Button) page2.findViewById(R.id.btn_clear_text);
        editSearch.setVisibility(View.VISIBLE);
        editSearch.setHint("Dummy search");

        editSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editSearch.getText().length() != 0) {
                    btnSearchClear.setVisibility(View.VISIBLE);
                } else {
                    btnSearchClear.setVisibility(View.GONE);
                }
            }
        });

        btnSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSearch.setText("");
                btnSearchClear.setVisibility(View.GONE);
            }
        });

        // Set share button
        final Button btnShare = (Button) page2.findViewById(R.id.btn_share);
        btnShare.setTextColor(SmallTheme.getInstance().getTintPrimaryColor());

        btnShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void setupOptionMenu() {
        View header = LayoutInflater.from(this).inflate(R.layout.header,
                new LinearLayout(this), false);

        final View optionMenu = header.findViewById(R.id.option_menu);

        optionMenu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SmallTheme.getInstance().showHeaderHint(v, R.string.sas_options);
                return false;
            }
        });

        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), optionMenu);
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper
                                    .getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod(
                                    "setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popup.getMenuInflater().inflate(R.menu.main, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_settings:
                                showSettings();
                                break;

                            case R.id.menu_about:
                                new AboutDialog(SmallAppSample.this,
                                        R.mipmap.ic_launcher, R.string.app_name, Type.LIST)
                                        .show(getRootView());
                                break;

                            case R.id.menu_sources:
                                SmallUtils.openLink(SmallAppSample.this, getRootView(),
                                        SOURCES_LINK,
                                        null, Type.GRID);
                                break;
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });
        getWindow().setHeaderView(header);
    }

    private void showSettings() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.settings, new LinearLayout(this), false);

        ViewGroup clearDefaults;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(R.string.sas_settings)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
        ;
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Create dialog using SmallUtils as we have to show it from a service
        SmallUtils.createDialog(alertDialog, getRootView().getWindowToken()).show();

        clearDefaults = (ViewGroup) alertDialog.findViewById(R.id.view_clear_defaults);

        clearDefaults.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear all the associated apps
                (new Associations(getContext())).getHelper().clearAll(true);
            }
        });
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        intent.putExtra(Intent.EXTRA_BCC, "");
        intent.putExtra(Intent.EXTRA_TEXT, "Create advanced small apps for Sony devices with"
                + " Small App Support library.\n\n" + SOURCES_LINK);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(R.string.sas_share)
                .setNegativeButton(android.R.string.cancel, null);

        new OpenIntentDialog(getContext(), intent, alertDialogBuilder, Type.LIST)
                .setRememberSelection(true)
                .setActivityOpenListener(new OnActivityOpenListener() {
                    public void onActivityOpen(ComponentName componentName) {
                        windowMinimize();
                    }
                })
                .setExtraInfo(R.drawable.sas_ic_action_link, SOURCES_LINK, null)
                .show(getRootView());
    }
}
