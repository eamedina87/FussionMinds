package ec.erickmedina.fussionminds.base

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import ec.erickmedina.fussionminds.R

open class BaseActivity  : AppCompatActivity(), BaseView{

    override fun loadFragment(fragment: Fragment, putInBackStack: Boolean) {
        when (putInBackStack) {
            true ->
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            false ->
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commitAllowingStateLoss()
        }
    }
}