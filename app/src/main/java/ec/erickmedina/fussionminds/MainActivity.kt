package ec.erickmedina.fussionminds

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import ec.erickmedina.fussionminds.activity.ActivityFragment
import ec.erickmedina.fussionminds.base.BaseActivity
import ec.erickmedina.fussionminds.monitor.MonitorFragment

import ec.erickmedina.fussionminds.sleep.SleepFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {


    private val mMonitorFragment = MonitorFragment()
    private val mActivityFragment = ActivityFragment()
    private val mSleepFragment = SleepFragment()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_activity -> {
                loadFragment(mActivityFragment, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_monitor -> {
                loadFragment(mMonitorFragment, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_sleep -> {
                loadFragment(mSleepFragment, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return@OnNavigationItemSelectedListener false
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_activity
    }
}
