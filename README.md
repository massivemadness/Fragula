# Fragula 2

**Fragula** is a swipe-to-dismiss extension for [navigation component](https://developer.android.com/guide/navigation/navigation-getting-started) library for Android.  
It is an adaptation of an earlier version created by **@shikleev** and now maintained in this repository.

![Android CI](https://github.com/massivemadness/Fragula/workflows/Android%20CI/badge.svg) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Fragula-orange.svg?style=flat)](https://android-arsenal.com/details/1/8405)

![Showcase](.github/images/showcase.gif)

---

# Table of Contents

## Fragment Navigation

1. [Gradle Dependency](#gradle-dependency)
2. [The Basics](#the-basics)
3. [More Options](#more-options)
    1. [Destination Arguments](#destination-arguments)
    2. [Multiple BackStacks](#multiple-backstacks)
4. [Swipe Direction](#swipe-direction)
5. [Swipe Transitions](#swipe-transitions)
6. [Theming](#theming)

## Jetpack Compose
1. [Gradle Dependency](#gradle-dependency-1)
2. [The Basics](#the-basics-1)
3. [Customization](#customization)

---

# Fragment Navigation

The `fragula-core` module provides everything you need to get started with the library. 
It contains all core and normal-use functionality.  

[![MavenCentral](https://img.shields.io/maven-central/v/com.fragula2/fragula-core?label=Download&color=blue)](https://repo1.maven.org/maven2/com/fragula2/fragula-core/)

<img src="https://raw.githubusercontent.com/massivemadness/Fragula/develop/.github/images/carbon.png" width="700" />

## Gradle Dependency

Add this to your module’s `build.gradle` file:

```gradle
dependencies {
  ...
  implementation 'com.fragula2:fragula-core:2.3'
}
```

The `fragula-core` module **does not** provide support for jetpack compose, you need to add the
`fragula-compose` dependency in your project.

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

**Finally**, you need to set opaque background and layout direction flag to your fragment’s root 
layout to avoid any issues with swipe animation.

```xml
<!-- fragment_detail.xml -->
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?android:colorBackground"
    android:layoutDirection="local">
    
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

**Finally**, in your receiving destination’s code, use the `getArguments()` method to retrieve the
Bundle and use its contents:

```kotlin
val textView = view.findViewById<TextView>(R.id.textViewItemId)
textView.text = arguments?.getString("itemId")
```

It's strongly recommended to use [Safe Args](https://developer.android.com/jetpack/androidx/releases/navigation#safe_args)
plugin for navigating and passing data, because it ensures type-safety.

### Multiple BackStacks

Currently multiple backstacks is **not supported**, which means you can’t safely use extensions such 
as `setupWithNavController(...)` without losing your current backstack. 

This issue affects both `BottomNavigationView` and `NavigationView` widgets.

---

## Swipe Direction

If you want to change the direction of swipe gesture, you can do that by setting
`app:swipeDirection="..."` manually in your navigation container. This example below sets up
vertical swipe direction.

```xml
<!-- activity_main.xml -->
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:name="com.fragula2.FragulaNavHostFragment" 
    android:id="@+id/nav_host" 
    app:swipeDirection="top_to_bottom"
    app:navGraph="@navigation/nav_graph"
    app:defaultNavHost="true" />
```

You can use either `left_to_right` (default) or `right_to_left` for horizontal direction.
For vertical direction you can use only `top_to_bottom`, the `bottom_to_top` is not supported due 
to internal ViewPager2 restrictions.

> **Note**
> If you having an issues with nested scrollable views, this appears to be a 
[scroll issue](https://developer.android.com/training/animation/vp2-migration#nested-scrollables) 
in ViewPager2. Please follow Google’s example to solve this.

---

## Swipe Transitions

You may want to know when the scrolling offset changes to make smooth transitions inside your 
fragment view. To start listening scroll events you need to retrieve `SwipeController` and set 
`OnSwipeListener` as shown below:

```kotlin
class DetailFragment : Fragment(R.layout.fragment_detail) {
   
    private lateinit var swipeController: SwipeController
    private lateinit var swipeListener: OnSwipeListener
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ...
        swipeController = findSwipeController()
        swipeListener = OnSwipeListener { position, positionOffset, positionOffsetPixels ->
            // TODO animate views using `positionOffset` or `positionOffsetPixels`.
            //  the `position` points to the position of the fragment in backstack
        }
        swipeController.addOnSwipeListener(swipeListener)
    }
   
    override fun onDestroyView() {
        super.onDestroyView()
        swipeController.removeOnSwipeListener(swipeListener)
    }
}
```

> **Note**
> Currently shared element transitions between destinations are not supported in any form.
> Remember that you must remove the listener when the fragment view is destroyed.

---

## Theming

In most of the cases there is no need to change any values, but if you wish to override these, 
there are attributes provided:

```xml
<style name="AppTheme" parent="Theme.MaterialComponents.Light.NoActionBar">
    <item name="colorPrimary">...</item>
    <item name="colorPrimaryDark">...</item>
    <item name="colorAccent">...</item>

    <!--
        This overrides the color used for the dimming when fragment is being dragged.
        The default value is #000000 for both light and dark themes.
    -->
    <item name="fgl_dim_color">#000000</item>

    <!--
        This overrides the amount of dimming when fragment is being dragged.
        Think of it as a `fgl_dim_color` alpha multiplier.
    -->
    <item name="fgl_dim_amount">0.10</item>

    <!--
        This overrides the parallax multiplier when fragment is being dragged.
        It determines how much the underneath fragment will be shifted
        relative to the visible fragment (that is being dragged).
    -->
    <item name="fgl_parallax_factor">1.3</item>
   
    <!--
        This overrides the duration of swipe animation using `navController.navigate(...)` 
        and `navController.popBackStack()` methods.
    -->
    <item name="fgl_anim_duration">500</item>
   
    <!--
        This overrides the elevation applied to the fragment view.
        Note that it doesn't support `android:outlineAmbientShadowColor`
        and `android:outlineSpotShadowColor` attributes in your theme.
    -->
    <item name="fgl_elevation">3dp</item>
   
</style>
```

---

# Jetpack Compose

The `fragula-compose` module provides support for jetpack compose.
It may not contain all the features described earlier. If you want to make a feature request, 
consider creating an issue on GitHub.  

[![MavenCentral](https://img.shields.io/maven-central/v/com.fragula2/fragula-compose?label=Download&color=red)](https://repo1.maven.org/maven2/com/fragula2/fragula-compose/)

<img src="https://raw.githubusercontent.com/massivemadness/Fragula/develop/.github/images/carbon_compose.png" width="700" />

## Gradle Dependency

Add this to your module’s `build.gradle` file:

```gradle
dependencies {
  ...
  implementation 'com.fragula2:fragula-compose:2.3'
}
```

The `fragula-compose` module **does not** provide support for fragments, you need to add the
`fragula-core` dependency in your project.

---

## The Basics

**First,** you need to replace `NavHost(...)` with `FragulaNavHost(...)` in your main composable:

```kotlin
// MainActivity.kt
setContent {
    AppTheme {
        Surface(
           modifier = Modifier.fillMaxSize(), 
           color = MaterialTheme.colors.background,
        ) {
            val navController = rememberNavController()
            FragulaNavHost(navController, startDestination = "list") {
                // ...
            }
        }
    }
}
```

**Second,** you need to replace your `composable(...)` destinations with `swipeable(...)` as shown below:

```kotlin
val navController = rememberNavController()
FragulaNavHost(navController, startDestination = "list") {
    swipeable("list") {
        ListScreen(navController)
    }
    swipeable("details") {
        DetailsScreen(navController)
    }
}
```

**Finally**, you need to set opaque background to your composables to avoid any issues with swipe animation.

```kotlin
@Composable
fun DetailsScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()
       .background(Color.White)
    ) {
        // TODO content
    }
}
```

Now if you open the app you'll see that you can swipe composables like in Telegram, Slack and many
other messaging apps.

---

## Customization

If you'd like to discover more customization features, here is parameters list.

```kotlin
@Composable
fun FragulaNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    dimColor: Color = DimColor, // Color used for the dimming
    dimAmount: Float = 0.1f, // Percentage of dimming (depends on drag offset)
    parallaxFactor: Float = 1.3f, // Parallax multiplier (depends on drag offset)
    animDurationMs: Int = 500, // Duration of swipe animation
    elevation: Dp = 3.dp, // Elevation applied on the composable
    builder: NavGraphBuilder.() -> Unit
) {
    // ...
}
```