package com.example.bottomnavigationexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.fragula.Navigator
import com.google.android.material.bottomnavigation.BottomNavigationView


class BottomActivity : AppCompatActivity() {

    lateinit var navigator1: Navigator
    lateinit var navigator2: Navigator
    lateinit var navigator3: Navigator

    lateinit var bottomView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom)

        navigator1 = findViewById(R.id.navigatorTab1)
        navigator2 = findViewById(R.id.navigatorTab2)
        navigator3 = findViewById(R.id.navigatorTab3)
        bottomView = findViewById(R.id.bottomView)

        if (savedInstanceState == null) {
            navigator1.addFragment(BlankFragment())
            navigator2.addFragment(BlankFragment())
            navigator3.addFragment(BlankFragment())
        } else {
            savedInstanceState.getInt(SELECTED_ITEM_KEY)?.let {
                selectTab(it)
            }
        }

        setBottomBarListener()
    }

    private fun setBottomBarListener() {
        bottomView.setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener selectTab(it.itemId)
        }
    }

    private fun selectTab(tabId: Int) = when (tabId) {
        R.id.navigation_tab_1 -> {
            navigator1.visibility = View.VISIBLE
            navigator2.visibility = View.INVISIBLE
            navigator3.visibility = View.INVISIBLE
            true
        }
        R.id.navigation_tab_2 -> {
            navigator1.visibility = View.INVISIBLE
            navigator2.visibility = View.VISIBLE
            navigator3.visibility = View.INVISIBLE
            true
        }
        R.id.navigation_tab_3 -> {
            navigator1.visibility = View.INVISIBLE
            navigator2.visibility = View.INVISIBLE
            navigator3.visibility = View.VISIBLE
            true
        }
        else -> false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_ITEM_KEY, bottomView.selectedItemId)
    }

    companion object {
        const val SELECTED_ITEM_KEY = "SELECTED_ITEM_KEY"
    }
}