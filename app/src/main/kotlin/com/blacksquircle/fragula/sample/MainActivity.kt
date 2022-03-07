package com.blacksquircle.fragula.sample

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.blacksquircle.fragula.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            binding.navigator.addFragment(BlankFragment())
        }

        binding.navigator.onPageScrolled = { position, positionOffset, positionOffsetPixels ->
            Log.i(TAG, "position: $position  positionOffset: $positionOffset  positionOffsetPixels: $positionOffsetPixels")
        }
        binding.navigator.onNotifyDataChanged = { fragmentCount -> }
        binding.navigator.onPageScrollStateChanged = { state -> }
    }

    // Intercept and block touch event when the new fragment is opening
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (binding.navigator.isBlockTouchEvent)
            true
        else
            super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if (binding.navigator.fragmentCount > 1) {
            binding.navigator.goToPreviousFragmentAndRemoveLast()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}