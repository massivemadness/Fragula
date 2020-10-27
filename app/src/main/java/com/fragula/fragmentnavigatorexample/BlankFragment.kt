package com.fragula.fragmentnavigatorexample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fragula.extensions.*
import com.fragula.listener.OnFragmentNavigatorListener
import kotlinx.android.synthetic.main.fragment_blank.*


const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

class BlankFragment : Fragment(), OnFragmentNavigatorListener {

    private var param1: String? = null
    private var param2: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getInt(ARG_PARAM2)
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
            tv_args_2.text = it.toString()
        }

        view.post {
            val count = parentNavigator.fragmentCount.toString()
            tv_number.text = count
        }

        btn_add_fragment.setOnClickListener {
            addFragment<BlankFragment>()
            getCallback<ExampleCallback>().onSuccess()
        }

        btn_add_fragment_args.setOnClickListener {
            addFragment<BlankFragment> {
                ARG_PARAM1 to "Add fragment arg"
                ARG_PARAM2 to 12345
            }
        }

        btn_replace_fragment.setOnClickListener {
            replaceFragment<BlankFragment>()
        }

        btn_replace_fragment_args.setOnClickListener {
            replaceFragment<BlankFragment> {
                ARG_PARAM1 to "Replace Fragment arg"
                ARG_PARAM2 to 6789
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        param1?.let {
            outState.putString(ARG_PARAM1, it)
        }

        param2?.let {
            outState.putInt(ARG_PARAM2, it)
        }
    }

    override fun onOpenedFragment() {
        Log.i("TEST","OPENED_FRAGMENT")
    }

    override fun onReturnedFragment() {
        Log.i("TEST","RETURNED_FRAGMENT")
    }
}
