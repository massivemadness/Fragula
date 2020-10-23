package com.fragulo.fragmentnavigatorexample

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fragulo.common.Arg
import com.fragulo.common.BundleBuilder
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

    fun addFragment(
        fragment: Fragment,
        builder: (BundleBuilder.() -> Unit)? = null
    ) {
        navigator.addFragment(fragment, builder)
    }

    fun replaceFragment(
        fragment: Fragment,
        position: Int? = null,
        builder: (BundleBuilder.() -> Unit)? = null
    ) {
        navigator.replaceFragment(fragment, position, builder)
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
