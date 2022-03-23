# Fragula 2

**Fragula** is a swipe-to-dismiss extension for [navigation component](https://developer.android.com/guide/navigation/navigation-getting-started) library for Android.  
It is an adaptation of an earlier version created by **@shikleev** and now maintained in this repository.

![Android CI](https://github.com/massivemadness/Fragula/workflows/Android%20CI/badge.svg) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![MavenCentral](https://img.shields.io/maven-central/v/com.fragula2/fragula-core?label=Download)](https://repo1.maven.org/maven2/com/fragula2/fragula-core/)

![](.github/images/showcase_1.gif) ![](.github/images/showcase_2.gif)

---

# Table of Contents

1. [Gradle Dependency](#gradle-dependency)
2. [The Basics](#the-basics)
3. [More Options](#more-options)
    1. [Destination Arguments](#destination-arguments)
    2. [Page Transitions](#page-transitions)
    3. [Multiple BackStacks](#multiple-backstacks)
4. [Theming](#theming)

---

## Gradle Dependency

Add this to your module’s `build.gradle` file:

```gradle
dependencies {
  ...
  implementation 'com.fragula2:fragula-core:2.0-rc02'
}
```

The `fragula-core` module contains everything you need to get started with the library. It contains all core and normal-use functionality.

---

## The Basics

**First,** you need to replace `NavHostFragment` with `FragulaNavHostFragment` in your layout:

```xml
<!-- activity_main.xml -->
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:name="com.fragula2.FragulaNavHostFragment" 
    android:id="@+id/nav_host"
    app:navGraph="@navigation/nav_graph"
    app:defaultNavHost="true" />
```

**Second,** you need to replace your `<fragment>` destinations in graph with `<swipeable>` as shown below:

```xml
<!-- nav_graph.xml -->
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/detailFragment">

    <swipeable
        android:id="@+id/detailFragment"
        android:name="com.example.fragula.DetailFragment"
        android:label="DetailFragment"
        tools:layout="@layout/fragment_detail" />

    ...
    
</navigation>
```

**Finally**, you need to set opaque background to your fragment’s root layout to avoid any issues with swipe animation.

```xml
<!-- fragment_detail.xml -->
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?android:colorBackground">
    
    ...
    
</androidx.constraintlayout.widget.ConstraintLayout>
```

Now if you open the app you'll see that you can swipe fragments like in Telegram, Slack and many 
other messaging apps.

---

## More Options

### Destination Arguments

In general, you should work with Fragula as if you would work with normal fragments. You should 
strongly prefer passing only the minimal amount of data between destinations, as the total space
for all saved states is limited on Android.

*If you need to pass large amounts of data, consider using a [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel).*

**First**, add an argument to the destination:

```xml
<swipeable 
    android:id="@+id/detailFragment"
    android:name="com.example.fragula.DetailFragment">
     <argument
         android:name="itemId"
         app:argType="string" />
 </swipeable>
```

**Second**, create a Bundle object and pass it to the destination using `navigate()` as shown below: 

```kotlin
val bundle = bundleOf("itemId" to "123")
findNavController().navigate(R.id.detailFragment, bundle)
```

**Finally**, in your receiving destination’s code, use the `getArguments` method to retrieve the Bundle and use its contents:

```kotlin
val textView = view.findViewById<TextView>(R.id.textViewItemId)
textView.text = arguments?.getString("itemId")
```

It's strongly recommended to use [Safe Args](https://developer.android.com/jetpack/androidx/releases/navigation#safe_args) plugin for navigating and passing data, because it ensures type-safety.

### Page Transitions

You may want to know when the scrolling offset changes to make smooth transitions inside your fragment view.  
To start listening scroll events you need to retrieve `SwipeController` and set `OnSwipeListener` as shown below:

*Note: Currently shared element transitions between destinations is not supported in any form.*

```kotlin
class DetailFragment : Fragment(R.layout.fragment_detail) {
   
    private lateinit var swipeController: SwipeController
    private lateinit var listener: OnSwipeListener
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ...
        swipeController = findSwipeController() // retrieve SwipeController for a fragment
        listener = OnSwipeListener { position, positionOffset, positionOffsetPixels ->
            // do something with values
        }
        swipeController.addOnSwipeListener(listener)
    }
   
    override fun onDestroyView() {
        super.onDestroyView()
        swipeController.removeOnSwipeListener(listener)
    }
}
```

**Remember:** you must remove the listener when the fragment view is destroyed.

### Multiple BackStacks

Currently multiple backstacks is not supported, which means you can't safely use extensions such as
`NavigationView.setupWithNavController(...)` and `BottomNavigationView.setupWithNavController(...)`
without losing your current backstack.

---

## Theming

In most of the cases there is no need to change any of these values, but if you really need to you 
can override these attributes in your activity theme.

```xml
<style name="AppTheme" parent="Theme.MaterialComponents.Light.NoActionBar">
    <item name="colorPrimary">...</item>
    <item name="colorPrimaryDark">...</item>
    <item name="colorAccent">...</item>

    <!--
        This overrides the color used for the dimming when fragment is being dragged.
        The default value is #323232 for both light and dark themes.
    -->
    <item name="fgl_dim_color">#323232</item>

    <!--
        This overrides the amount of dimming when fragment is being dragged.
        Think of it as a `fgl_dim_color` alpha multiplier.
    -->
    <item name="fgl_dim_amount">0.2</item>
   
    <!--
        This overrides the duration of swipe animation using `navController.navigate(...)` 
        and `navController.popBackStack()` methods.
    -->
    <item name="fgl_anim_duration">300</item>
   
</style>
```