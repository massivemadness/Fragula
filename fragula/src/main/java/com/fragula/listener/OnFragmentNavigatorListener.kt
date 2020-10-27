package com.fragula.listener

interface OnFragmentNavigatorListener {
    // it called when the fragment opening transaction is complete
    fun onOpenedFragment()

    // it called when the return transaction to the previous fragment is complete
    fun onReturnedFragment()
}