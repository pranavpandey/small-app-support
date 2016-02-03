# Small App Support

<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_512x512.png" width="160" height="160" align="right" hspace="20">

*A simple library to build apps for Sony Small Apps Extension.*

Small App Support is an easy to use library to build attractive small apps with native apps like functionality. I have divided it into different parts for easy understanding. As small app is a service there was a problem displaying a dialog from the small app. So, I have wrote a function to easily display a dialog from a small app. This library is a collection of such type of functions and classes to provide a good interface.

<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_1.png" width="280" height="486">
<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_2.png" width="280" height="486">
<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_3.png" width="280" height="486">

<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_4.png" width="280" height="486">
<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_5.png" width="280" height="486">
<img src="https://raw.githubusercontent.com/pranavpandey/small-app-support/master/graphics/sas_screen_6.png" width="280" height="486">

---

# Usage

Its an Eclipse project, import it in the eclipse and add the path of your Sony add-on SDK. Run the sample to see it working. Full documentation coming soon...

I have divided it into different parts for easy understanding. First we have to initialize the `SmallTheme` to make things working properly.

## Theme

There is a base `SmallApp` class which you can extend to initialize the `SmallTheme` and it also has some useful functions and can handle configuration changes. Now, you can use it to extract different colors from the theme by getting its instance.

`SmallTheme` is a class to detect Primary and Accent colors form the theme. In ICS, it will return the Theme accent color. It also has some other functions like `showHint(View, String)`, `showHeaderHint(View, String)` to show hints for footer and header menu items respectively.

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

---

# Apps using Small App Support

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
