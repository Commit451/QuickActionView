# QuickActionView
View that shows quick actions when long pressed, inspired by Pinterest

[![Build Status](https://travis-ci.org/ovenbits/QuickActionView.svg?branch=master)](https://travis-ci.org/ovenbits/QuickActionView) [![](https://jitpack.io/v/ovenbits/QuickActionView.svg)](https://jitpack.io/#ovenbits/QuickActionView) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-QuickActionView-green.svg?style=true)](https://android-arsenal.com/details/1/3536)

![Sample Gif](https://raw.githubusercontent.com/ovenbits/QuickActionView/master/screenshots/qav.gif)

# Gradle Dependency

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Then, add the library to your project `build.gradle`
```gradle
dependencies {
    compile 'com.github.ovenbits:QuickActionView:1.0.2'
}
```

# Basic Usage

See the sample app for usage within a normal layout, as well as within a list using RecyclerView. In the most basic usage:

```java
View view = findViewById(R.id.your_view);
QuickActionView.make(this)
      .addActions(R.menu.actions)
      .register(view);
```
You can also create Actions at runtime:
```java
Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_24dp);
String title = getString(R.string.action_favorite);
Action action = new Action(1337, icon, title);
QuickActionView.make(this)
        .addAction(action)
        //more configuring
        .register(view);
```

# Configuring the QuickActionView

QuickActionView can be customized globally, or on a per Action basis.
```java
QuickActionView.make(this)
                .addActions(R.menu.actions)
                .setOnActionSelectedListener(mQuickActionListener)
                .setBackgroundColor(Color.RED)
                .setTextColor(Color.BLUE)
                .setTextSize(30)
                .setScrimColor(Color.parseColor("#99FFFFFF"))
                //etc
                .register(view);
```

# Configuring Action Items

Use the `QuickActionConfig` builder to create custom configurations for each action item you create.
```java
//Give one of the quick actions custom colors
Action.Config actionConfig = new Action.Config()
        .setBackgroundColor(Color.BLUE)
        .setTextColor(Color.MAGENTA);

QuickActionView.make(this)
        .addActions(R.menu.actions)
        //the custom Action.Cofig will only apply to the addToCart action
        .setActionConfig(actionConfig, R.id.action_add_to_cart)
        .register(findViewById(R.id.custom_parent));
```

# Customizing Animations

You can even customize the animation performed when the actions come into view and go out of view. `FadeAnimator` `PopAnimator` and `SlideFromCenterAnimator` are three pre-built animators, and all you need to do to create your own is implement `ActionsInAnimator`, `ActionsOutAnimator`
and call `setActionsInAnimator` and `setActionsOutAnimator` respectively.

# Listening for Events

You can hook into the interesting events a QuickActionView has:
```java
QuickActionView.make(this)
      .addActions(R.menu.actions)
      .setOnActionSelectedListener(mQuickActionListener)
      .setOnShowListener(mQuickActionShowListener)
      .setOnDismissListener(mQuickActionDismissListener)
      .setOnActionHoverChangedListener(mOnActionHoverChangedListener)
      .register(view);
```

See the sample for more detail.

License
--------

    Copyright 2016 Oven Bits, LLC

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
