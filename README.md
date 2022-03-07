[![](https://jitpack.io/v/shikleyev/fragula.svg)](https://jitpack.io/#shikleyev/fragula)

# Fragula
A simple and customizable Android fragments navigator with support "swipe to dismiss" gestures and saving a stack of fragments when changing the screen orientation

![](.github/20200301_131107.gif)

### Requirements
* A project configured with the AndroidX
* SDK 21 and and higher

### Demo Application
[![Get it on Google Play](https://play.google.com/intl/en_us/badges/images/badge_new.png)](https://play.google.com/store/apps/details?id=info.yamm.project2&hl=ru)

(The app requires vk.com registration)

### Install
Download via **Gradle**:

Add this to the **project `build.gradle`** file:
```gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

And then add the dependency to the **module `build.gradle`** file:
```gradle
implementation 'com.github.shikleev:fragula:latest_version'
```

### Usage
#### Simple usage
All you need to do is create a Navigator in the xml of your activity:
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.fragula.Navigator
    android:id="@+id/navigator"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/black"
    tools:context=".MainActivity"/>
```

And add a first fragment:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    if (savedInstanceState == null) {
        navigator.addFragment(BlankFragment())
    }
}
```

#### Passing arguments to a fragment
You can pass arguments in the function parameters:
```kotlin
navigator.addFragment(BlankFragment()) {
    "ARG_KEY_1" to "Add fragment arg"
    "ARG_KEY_2" to 12345
}
```
Or using kotlin-extensions:
```kotlin
addFragment<BlankFragment> {
    "ARG_KEY_1" to "Add fragment arg"
    "ARG_KEY_2" to 12345
}
```

And get them in an opened fragment:
```kotlin
class BlankFragment : Fragment() {

    private var param1: String? = null
    private var param2: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("ARG_KEY_1")
            param2 = it.getInt("ARG_KEY_2")
        }
    }
}
```

#### Replace fragment
```kotlin
navigator.replaceFragment(BlankFragment())
```
With kotlin-extensions
```kotlin
replaceFragment<BlankFragment>()
```
Or replace by position with arguments
```kotlin
replaceFragment<BlankFragment>(
   position = position,
   bundleBuilder = {
        "ARG_KEY_1" to "Replace fragment arg"
   }
)
```

#### Intercept events
Intercept the touch event while the fragment transaction is in progress.
In your Activity:
```kotlin
override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    return if (navigator.isBlockTouchEvent)
        true
    else
        super.dispatchTouchEvent(ev)
}
```
Intercept onBackPressed:
```kotlin
override fun onBackPressed() {
    if (navigator.fragmentCount > 1) {
        navigator.goToPreviousFragmentAndRemoveLast()
    } else {
        super.onBackPressed()
    }
}
```

#### Fragment transition callback
The fragment opening transaction is executed synchronously and starts after onViewCreated finishes in the fragment being torn off. If you have asynchronous code that displays the results in a fragment, this may affect the arbitrariness of the fragment's opening animation. For such cases, you need to use an interface in your fragment that will report that the fragment transaction is complete.
```kotlin
class BlankFragment : Fragment(), OnFragmentNavigatorListener {
    override fun onOpenedFragment() {
        //This is called when the animation for opening a new fragment is complete
    }
    override fun onReturnedFragment() {
        //This will be called when you return to this fragment from the previous one
    }
}
```
You can also use other callbacks:
```kotlin
navigator.onPageScrolled = {position, positionOffset, positionOffsetPixels ->  }

navigator.onNotifyDataChanged = {fragmentCount ->  
// Called after a new fragment is added to the stack or when the fragment is removed from the stack
}

navigator.onPageScrollStateChanged = {state -> 
// SCROLL_STATE_IDLE, SCROLL_STATE_SETTLING, SCROLL_STATE_DRAGGING
}
```

#### Fragment stack
You can get a stack of fragments by accessing the Navigator:
```kotlin
val fragments: List<Fragment> = navigator.fragments
```
Or using the Fragment Manager to search for a fragment by tag
(The Navigator assigns a tag to each fragment depending on the position in the Navigator):
```kotlin
val fragment = supportFragmentManager.findFragmentByTag("0")
if (fragment != null && fragment is MainFragment) {
    mainFragment = fragment
}
```

#### Information transfer between fragments
You can implement your own interface in the target fragment and call its callbacks in the current fragment:
```kotlin
interface ExampleCallback {
    fun onReceive()
}

class TargetFragment : Fragment(), ExampleCallback {

    override fun onReceive() {
        // do something
    }
}
```
Then, on the current fragment, call the getCallback<T> function and call the desired function:
```kotlin
class CurrentFragment : Fragment() {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        getCallback<ExampleCallback>.onReceive()
    }
}
```

#### Page transformer
Navigator based on ViewPager, so you can use your own PageTransformer:
```kotlin
class CustomPageTransformer: FragmentNavigator.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            cameraDistance = width * 100f
            pivotY = height / 2f
            when {
                position > 0 && position < 0.99 -> {
                    alpha = 1f
                    rotationY = position * 150
                    pivotX = width / 2f
                }
                position > -1 && position <= 0 -> {
                    alpha = 1.0f - abs(position * 0.7f)
                    translationX = -width * position
                    rotationY = position * 30
                    pivotX = width.toFloat()
                }
            }
        }
    }
}
```
And in your activity:
```kotlin
navigator.setPageTransformer(false, CustomPageTransformer())
```
![](.github/20200312_143526.gif)


#### Change the animation duration
```kotlin
navigator.setDurationFactor(1.8f)
```



### Issues
#### 1.
The Navigator cannot delete a fragment in the middle or beginning of the fragment stack. This leads to the violation of the order of the fragments and unexpected errors. Use onBackPressed to delete the last fragment or 
```kotlin
navigator.goToPreviousFragmentAndRemoveLast()
```
If you want to remove the last few fragments, use:
```kotlin
navigator.goToPosition(position)
```
This will also remove all closed fragments from the stack
#### 2.
Gestures conflict when using Motion Layout

![](.github/20200301_133838.gif)

If there is a conflict of gestures you can disable the swipe gestures in the Navigator and then turn them back on
```kotlin
MotionLayout.setOnTouchListener { view, motionEvent ->
    when (motionEvent.action) {
        MotionEvent.ACTION_DOWN -> {
            navigator.setAllowedSwipeDirection(SwipeDirection.NONE)
        }
        MotionEvent.ACTION_UP -> {
            navigator.setAllowedSwipeDirection(SwipeDirection.RIGHT)
        }
        MotionEvent.ACTION_CANCEL -> {
            navigator.setAllowedSwipeDirection(SwipeDirection.RIGHT)
        }
    }
    return@setOnTouchListener false
}
```
![](.github/20200301_133937.gif)


Also, you can take a look at the [sample project](https://github.com/shikleyev/fragula/tree/master/app) for more information.

![](.github/20200301_131439.gif)


[Sample project for bottom navigation](https://github.com/shikleev/fragula/tree/master/bottomnavigationexample)

![](.github/ezgif-6-50786136c405.gif)


