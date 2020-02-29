package com.fragulo.fragmentnavigatorexample

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fragulo.navigator.common.Arg
import com.fragulo.navigator.listener.OnFragmentNavigatorListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_blank.*


const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

class BlankFragment : Fragment(), OnFragmentNavigatorListener {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retainInstance = true

        param1?.let {
            tv_args_1.text = it
        }

        param2?.let {
            tv_args_2.text = it
        }

        val count = (activity as MainActivity).navigator.fragmentsCount()?.toString()

        tv_number.text = count

        btn_add_fragment.setOnClickListener {
            (activity as MainActivity).addFragment(BlankFragment())
        }

        btn_add_fragment_args.setOnClickListener {
            (activity as MainActivity).addFragment(
                BlankFragment(),
                Arg(ARG_PARAM1, "Add fragment arg"),
                Arg(ARG_PARAM2, "New arg"))
        }

        btn_replace_fragment.setOnClickListener {
            (activity as MainActivity).replaceCurrentFragment(BlankFragment())
        }

        btn_replace_fragment_args.setOnClickListener {
            (activity as MainActivity).replaceCurrentFragment(
                BlankFragment(),
                Arg(ARG_PARAM1, "Replace Fragment arg"),
                Arg(ARG_PARAM2, "New arg"))
        }

    }

    override fun onOpenedFragment() {
        Log.i("TEST","OPENED_FRAGMENT")
    }

    override fun onReturnedFragment() {
        Log.i("TEST","RETURNED_FRAGMENT")
    }
}
