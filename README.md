# Small App Support

<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_512x512.png" width="160" height="160" align="right" hspace="20">

*A simple library to build apps for Sony Small Apps Extension.*

Small App Support is an easy to use library to build attractive small apps with native apps like functionality. I have divided it into different parts for easy understanding. As small app is a service, there were issues while displaying dialog from the small app. So, I have wrote a function to easily display dialog from a small app. This library is a collection of such type of functions and classes to provide a better interface.

<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_1.png" width="280" height="486">
<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_2.png" width="280" height="486">
<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_3.png" width="280" height="486">

<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_4.png" width="280" height="486">
<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_5.png" width="280" height="486">
<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_6.png" width="280" height="486">

---

## Setup

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?)](https://www.apache.org/licenses/LICENSE-2.0.html)

### Eclipse with ADT

Its an Eclipse project, import it in eclipse and add the path of your Sony add-on SDK. Run the sample to see it working.

## Usage

Read the documentation below to know how you can make attractive small apps by using this library. It will also simplify the development process and please don't rely only on this, keep exploring the different classes to find some hidden functions. If there is any mistake or some important features are missing then, feel free to update it and send me the pull request so that I can update it.

I have divided it into different parts for easy understanding. First we have to initialize the `SmallTheme` to make things working properly.

### Theme

There is a base `SmallApp` class which you can extend to initialize the `SmallTheme` and it also has some useful functions and can handle configuration changes. After that you can use it to extract different colors from the theme by getting its instance.

#### SmallTheme

`SmallTheme` is a class to detect Primary and Accent colors from the theme. In ICS, it will return the Theme accent color. It also has some other functions like `showHint(view, string)`, `showHeaderHint(view, string)` to show hints for footer and header menu items respectively.

```java
public class SmallAppSample extends SmallApp {

    @Override
    protected int getLayoutId() {
        return R.layout.main;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        // Set title for the small app
        setTitle(R.string.title);

        // Set windows attributes
        SmallAppWindow.Attributes attr = getWindow().getAttributes();
        attr.minWidth = getResources().getDimensionPixelSize(R.dimen.min_width);
        attr.minHeight = getResources().getDimensionPixelSize(R.dimen.min_height);
        attr.width = getResources().getDimensionPixelSize(R.dimen.width);
        attr.height = getResources().getDimensionPixelSize(R.dimen.height);

        attr.flags |= SmallAppWindow.Attributes.FLAG_RESIZABLE;
        getWindow().setAttributes(attr);
        
        // Get base colors
        @ColorInt int primaryColor = SmallTheme.getInstance().getPrimaryColor();
        @ColorInt int accentColor = SmallTheme.getInstance().getAccentColor();
        
        // Get tint colors so that it will always be visible on the base color
        @ColorInt int tintPrimaryColor = SmallTheme.getInstance().getTintPrimaryColor();
        @ColorInt int tintAccentColor = SmallTheme.getInstance().getTintAccentColor();
        
        // Use other SmallApp funtions
        
        // Minimize the small app window
        windowMinimize();
        
        // Set small app window fitted to the screen
        windowFitted();
        
        // Set small app window to its general state
        windowNormal();
        
        // Setup header with option menu
        View header = LayoutInflater.from(this).inflate(R.layout.header, new LinearLayout(this), false);
		
		final View optionMenu = header.findViewById(R.id.option_menu);
		
        // Show header hint
		optionMenu.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				SmallTheme.getInstance().showHeaderHint(v, R.string.sas_options);
				return false;
			}
		});
    }
}
```

If you don't want to extend `SmallApp` class then, you have to do the following modifications in your `SmallApplication` class.

```java
public class SmallAppSample extends SmallApplication {

    @Override
    protected void onCreate() {
        super.onCreate();
        // Initialize SmallTheme instance
        SmallTheme.initializeInstance(getApplicationContext());
        
        ...
    }
}
```

#### DynamicTheme

In theme, there is another useful class `DynamicTheme`. It is mostly used internally but you can also use its `static` functions to generate colors dynamically. 

```java
// Calculate tint based on a given color for better readability
DynamicTheme.getTintColor(color);

// Calculate accent based on a given color for dynamic theme generation.
// Still in beta so, sometimes may be inaccurate color
DynamicTheme.getAccentColor(color);

// Calculate contrast of a color based on the give base color so
// that it will be visible always on top of the base color.
DynamicTheme.getContrastColor(color, contrastWith);

// Colorize and return the mutated drawable so that, all other references 
// do not change.
DynamicTheme.colorizeDrawable(drawable, color);
// OR
DynamicTheme.colorizeDrawableRes(context, drawable, color);

// Highlight the query text within a TextView. Suitable for notifying user about the 
// searched query found in the adapter. TextView should not be empty. Please set your 
// default text first then, highlight the query text by using this function.
DynamicTheme.highlightQueryTextColor(query, textView, color);
// OR
DynamicTheme.highlightQueryTextColorRes(query, textView, colorId);
```

---

### View

It consists of different views by which you can easily change `TextView` color or can apply filter on a `ImageView` according to the colors extracted from the theme. Make sure to initialize `SmallTheme` first if you are not extending `SmallApp` class. You can make these views background aware so that their color will always be visible on the supplied background. If no background is supplied then, it will use the default background color i.e; `#FF1A1A1A`

Following color attributes are available which you can use in the layout to colorize these views.

#### ColorAttributes

1. `colorType` - applies filter on image view or changes text color according to the following values.

    0. `none` - no color will be appled to the view.
    1. `primary` - extracted `colorPrimary` from the current theme.
    2. `primary_dark` - extracted `colorPrimaryDark` from the current theme.
    3. `accent` - extracted `colorAccent` from the current theme.
    4. `accent_dark` - extracted `colorAccentDark` from the current theme.
    5. `tint_primary` - calculated tint color based on the `colorPrimary`.
    6. `tint_primary_dark` - calculated tint color based on the `colorPrimaryDark`.
    7. `tint_accent` - calculated tint color based on the `colorAccent`.
    8. `tint_accent_dark` - calculated tint color based on the `colorAccentDark`.
    
2. `backgroundAware` - `true` if this view will change its color according to the background. It was introduced to provide better legibility for colored images and to avoid dark image on dark background like situations. If this boolean is set then, it will check for the contrast color and do color calculations according to that color so that this image view will always be visible on that background. If no contrast color is found then, it will take default background color.

3. `contrastWith` - background color for this view so that it will remain in contrast with this color.

4. `colorAlpha` - background alpha for this view ranging from 0 - 255.

#### ColoredImageView

An `ImageView` to apply color filter according to the supplied color `colorType`.

```xml
<com.pranavpandey.smallapp.view.ColoredImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:colorType="primary" 
    app:backgroundAware="true"
    app:contrastWith="#FF2A2A2A" />
```

In the above example, it will automatically apply `colorPrimary` filter on the `ImageView`. As `backgroundAware` set to `true` and a `contrastWith` color is also supplied so, it will check that the applied filter will be visible on `#FF2A2A2A` background or not. If not or both the colors are dark then, it will calculate the tint or light version of the `colorPrimary` which will always be visible on that background and changes the color filter.

#### PressedStateImageView

An ImageView which changes alpha on touch to show pressed state. It is extended from `ColoredImageView` to provide colorizing abilities whenever is required.

```xml
<com.pranavpandey.smallapp.view.PressedStateImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/icon" />
```
    
#### ColoredTextView

A TextView to change its color according to the supplied `colorType`.

```xml
<com.pranavpandey.smallapp.view.ColoredTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:colorType="primary" 
    app:backgroundAware="true"
    app:contrastWith="#FF2A2A2A" />
```

In the above example, it will automatically set the text color to `colorPrimary`. As `backgroundAware` set to `true` and a `contrastWith` color is also supplied so, it will check that the applied color will be visible on `#FF2A2A2A` background or not. If not or both the colors are dark then, it will calculate the tint or light version of the `colorPrimary` which will always be visible on that background and changes the text color.

#### ColoredLinearLayout

A LinearLayout to change background according to the supplied `colorType`.

```xml
<com.pranavpandey.smallapp.view.ColoredLinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    app:colorType="primary" 
    app:colorAlpha="200" />
```

In the above example, it will automatically set the background color to `colorPrimary`. As `colorAlpha` is set to 200 so, it will also change the `argb` color alpha component to 200.

All the views has getters and setters for these attributes to change them at runtime.

```java
// Setters

// ColoredImageView
ColoredImageView.setColorType(colorType);
ColoredImageView.setBackgroundAware(boolean);
ColoredImageView.setContrastWith(color);

// ColoredTextView
ColoredTextView.setColorType(colorType);
ColoredTextView.setBackgroundAware(boolean);
ColoredTextView.setContrastWith(color);

// ColoredLinearLayout
ColoredLinearLayout.setColorType(colorType);
ColoredLinearLayout.setColorAlpha(int);


// Getters

// ColoredImageView
ColoredImageView.getColorType();
ColoredImageView.isBackgroundAware();
ColoredImageView.getContrastWith();

// ColoredTextView
ColoredTextView.getColorType();
ColoredTextView.isBackgroundAware();
ColoredTextView.getContrastWith();

// ColoredLinearLayout
ColoredLinearLayout.getColorType();
ColoredLinearLayout.getColorAlpha();
```

---

### Launcher

A set of classs with collection of helper functions and constants to make launching of small app easier and from anywhere.

#### ShortcutLauncher

An abstract activity to launch small app shortcuts form anywhere. Extend it in your project and override `getShortcutPackage()` method to pass a package name. You can also start the activity with an intent extra contating the package name and can extract that package name in this method. Rest of the things will be handle by the `SmallLauncher`.

```java
// Start activity with an intent extra.
Intent intent = new Intent(context, ShortcutActivity.class);
intent.putExtra(ShortcutLauncher.PACKAGE_NAME, "com.pranavpandey.smallapp.sample");
startActivity(intent);

...

// Extend ShortcutLauncher to launch small app.
public class ShortcutActivity extends ShortcutLauncher {

    // Override this function and pass a package name or 
    // start this activity with the intent.
	@Override
	protected String getShortcutPackage() {
		return "com.pranavpandey.smallapp.sample";
        
		// OR
        return getIntent().getStringExtra(ShortcutLauncher.PACKAGE_NAME);
	}
}
```

---

### SmallUtils

There were always be a problem while displaying dialog from a small app as it is derived from the `Service`. But by doing some midifications we can do it easily. `SmallUtils` is a collection of such useful functions. It also has other functions to save settings in `SharedPreferences`.

```java
// Create simple alert dialog .
AlertDialog.Builder builder = new AlertDialog.Builder(context);
builder.setTitle(R.string.sas_about);
builder.setPositiveButton(android.R.string.ok, null);

// Use SmallUtils to display it from a small app.
// Pass the window token of your root view. A parent view of which you want to 
// attach the dialog.
SmallUtils.createDialog(builder.create(), getRootView().getWindowToken());

// Save values in SharedPreferences.

// Save integer value
SmallUtils.savePrefs(context, "Key", int);
// Save booelan value
SmallUtils.savePrefs(context, "Key", boolean);
// Save String value
SmallUtils.savePrefs(context, "Key", string);


// Load values from SharedPreferences.

// Load integer preference. If not found then, return defaultInt.
SmallUtils.loadPrefs(context, "Key", defaultInt);
// Load boolean preference. If not found then, return defaultBoolean.
SmallUtils.loadPrefs(context, "Key", defaultBooelan);
// Load String preference. If not found then, return defaultString.
SmallUtils.loadPrefs(context, "Key", defaultString);
```

---

### Dialog

There are some in-built `Dialogs` so you don't have to write your own. You can use them to show options on long press or if you want to open some links, files, etc. It has an intent app picker which can also remeber the user's choices so they don't need to choose the app next time. Read below to know about their usage.

#### ActionDialog

A class which creates a dialog to show different actions to perform various operations by using an adapter containing all the actions which will be displayed either in a `List`or `Grid`. You can use `BaseActionItemAdapter` or any other custom adapter according to your need. Set a click listener to dispatch click events so that you can perform actions.

You can also set extra info to display as header of `List` or `Grid` which is also clickable.

```java
// Initalize list
ArrayList<BaseActionItem> actionList  = new ArrayList<BaseActionItem>();

// Add actions into the list.
actionList.add(new BaseActionItem(actionId, nameId, drawableId, isColorizable));
actionList.add(new BaseActionItem(actionId, nameId, drawableId, isColorizable));

// Configure dialog builder.
AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
alertDialogBuilder.setTitle(string);
alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);
        
// Initialize ActionDialog and pass the builder.
ActionDialog actionDialog = new ActionDialog(context, alertDialogBuilder, Type.GRID);

// Set actions adapter.
actionDialog.setAdapter(new BaseActionItemAdapter(context, actionList, R.layout.sas_item_grid_action), 
    new OnActionItemClickListener() {
        public void onActionItemClick(DialogInterface dialog, Adapter adapter, AdapterView<?> parent,
                View view, int position, long id) {
            int actionId =  ((BaseActionItem) adapter.getItem(position)).getActionId();
            switch (actionId) {
                // handle action item click events.
            }
        }
    }
)
// Set extra info to be displayed in the header of list or grid.
.setExtraInfo(drawableId, textString, new OnExtraInfoClickListener() {
    public void onExtraInfoClick(View v) {
        // handle click event.
    }	
})
// Show the action dialog.
.show(getRootView());
```

#### OpenIntentDialog

A class which creates a dialog to show all the activities available to handle the supplied intent. It is a solution to handle no activity found exception and you can also do some other work if this exception occurs. It extends the `ActionDialog` class so that you can use its functions also.

```java
// Create intent.
Intent intent = new Intent(Intent.ACTION_SEND);
intent.setType("application/*");
intent.putExtra(Intent.EXTRA_SUBJECT, appName);
intent.putExtra(Intent.EXTRA_BCC, "");
intent.putExtra(Intent.EXTRA_TEXT, appName + "\n" 
        + "http://play.google.com/store/apps/details?id=" + packageName);

// Configure dialog builder.
AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
.setTitle(R.string.share)
.setNegativeButton(android.R.string.cancel, null);

// Show opent intent dialog.
new OpenIntentDialog(context, intent, alertDialogBuilder, Type.GRID)
.setActivityOpenListener(new OnActivityOpenListener() {
    public void onActivityOpen(ComponentName componentName) {
        // handle activity opent event.
    }	
})
// true if remember user selection to open same intent 
// with the same app next time.
.setRememberSelection(boolean)
// Set extra info to be displayed in the header of list or grid.
// Pass null to make header not clickable.
.setExtraInfo(drawableId, shareString, null)
.show(getRootView());
```

It will show a list of all the apps that can handle this intent. Select one of them, to open the intent. If `setRememberSelection(true)` then, it will show a checkbox to the users so that they can remember their selection for this intent.

You can also clear the user selection and all the associated apps by using the `Associations` class.

```java
// Clear all the associated apps.
// Pass true to show a toast message and notify user.
new Associations(context).getHelper().clearAll(true);
```

---

## Apps using Small App Support

All of my small apps are built with this library. You can download them from Google Play. Please email me if you are using this library and  want to feature your small app here.

**[Stopwatch](https://play.google.com/store/apps/details?id=com.pranavpandey.smallapp.stopwatch)**

**[Torch](https://play.google.com/store/apps/details?id=com.pranavpandey.smallapp.torch.lite)**

**[Launcher](https://play.google.com/store/apps/details?id=com.pranavpandey.smallapp.launcher)**

**[Phone](https://play.google.com/store/apps/details?id=com.pranavpandey.smallapp.phone)**

**[Files](https://play.google.com/store/apps/details?id=com.pranavpandey.smallapp.files.lite)**

---

## License

    Copyright (C) 2016 Pranav Pandey

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
