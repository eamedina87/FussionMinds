package ec.erickmedina.fussionminds.base

import android.support.v4.app.Fragment

interface BaseView {
    fun loadFragment(fragment: Fragment, putInBackStack: Boolean)
}