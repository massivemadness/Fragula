package com.fragulo.fragmentnavigatorexample

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fragulo.common.Arg
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigator.init(supportFragmentManager)
        navigator.onPageScrolled = {position, positionOffset, positionOffsetPixels ->
            Log.i(
                TAG,
                "position: $position  positionOffset: $positionOffset  positionOffsetPixels: $positionOffsetPixels"
            )
        }
        navigator.onNotifyDataChanged = { fragmentCount -> }
        navigator.onPageScrollStateChanged = { state -> }
        if (savedInstanceState == null) {
            navigator.addFragment(BlankFragment())
        }
    }

    override fun onDestroy() {
        navigator.release()
        super.onDestroy()
    }

    fun addFragment(
        fragment: Fragment,
        vararg args: Arg<*, *>) {
        navigator.addFragment(fragment, *args)
    }

    fun addFragment(fragment: Fragment) {
        navigator.addFragment(fragment)
    }

    fun replaceCurrentFragment(newFragment: Fragment, vararg args: Arg<*, *>) {
        navigator.replaceCurrentFragment(newFragment, *args)
    }

    fun replaceCurrentFragment(newFragment: Fragment) {
        navigator.replaceCurrentFragment(newFragment)
    }

    fun replaceFragmentByPosition(newFragment: Fragment, position: Int, vararg args: Arg<*, *>) {
        navigator.replaceFragmentByPosition(newFragment, position, *args)
    }

    fun replaceFragmentByPosition(newFragment: Fragment, position: Int) {
        navigator.replaceFragmentByPosition(newFragment, position)
    }

    // Intercept and block touch event when the new fragment is opening
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (navigator.isBlockTouchEvent)
            true
        else
            super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if (navigator.fragmentsCount() > 1) {
            navigator.goToPreviousFragmentAndRemoveLast()
        } else {
            super.onBackPressed()
        }
    }
}
