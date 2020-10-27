package com.example.bottomnavigationexample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fragula.extensions.addFragment
import com.fragula.extensions.parentNavigator
import com.fragula.extensions.replaceFragment
import com.fragula.listener.OnFragmentNavigatorListener


const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

class BlankFragment : Fragment(), OnFragmentNavigatorListener {

    private var param1: String? = null
    private var param2: Int? = null

    private lateinit var tvArg1: TextView
    private lateinit var tvArg2: TextView
    private lateinit var tvNumber: TextView

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button

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

        tvArg1 = view.findViewById(R.id.tv_args_1)
        tvArg2 = view.findViewById(R.id.tv_args_2)
        tvNumber = view.findViewById(R.id.tv_number)
        button1 = view.findViewById(R.id.btn_add_fragment)
        button2 = view.findViewById(R.id.btn_add_fragment_args)
        button3 = view.findViewById(R.id.btn_replace_fragment)
        button4 = view.findViewById(R.id.btn_replace_fragment_args)

        retainInstance = true

        param1?.let {
            tvArg1.text = it
        }

        param2?.let {
            tvArg2.text = it.toString()
        }

        view.post {
            val count = parentNavigator.fragmentCount.toString()
            tvNumber.text = count
        }

        button1.setOnClickListener {
            addFragment<BlankFragment>()
        }

        button2.setOnClickListener {
            addFragment<BlankFragment> {
                ARG_PARAM1 to "Add fragment arg"
                ARG_PARAM2 to 12345
            }
        }

        button3.setOnClickListener {
            replaceFragment<BlankFragment>()
        }

        button4.setOnClickListener {
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
