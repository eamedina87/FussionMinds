package ec.erickmedina.fussionminds.monitor

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.erickmedina.fussionminds.R
import ec.erickmedina.fussionminds.entities.MonitorResult
import kotlinx.android.synthetic.main.layout_monitor.*

class MonitorFragment : Fragment(), MonitorContract.MonitorView{

    private val mGraphFragment = MonitorGraphFragment()
    private val mNumberFragment = MonitorNumberFragment()
    private val mEmojiFragment = MonitorEmojiFragment()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_monitor, container, false)
    }

    private var mPresenter: MonitorPresenterImpl? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        monitor_viewpager.adapter = fragmentManager?.let { ScreenSlidePagerAdapter(it) }
        monitor_viewpager.currentItem = 0
        mPresenter = MonitorPresenterImpl()
        mPresenter?.setView(this)
        mPresenter?.onCreate()
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.startMonitor()
    }

    override fun onPause() {
        super.onPause()
        mPresenter?.stopMonitor()
    }

    override fun onMonitorResultSuccess(result: MonitorResult) {
        activity?.runOnUiThread {
            mEmojiFragment.onMonitorResultSuccess(result)
            mNumberFragment.onMonitorResultSuccess(result)
        }
    }

    override fun onMonitorResultError() {

    }


    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            return when (position){
                0 -> showNumberMonitor()
                else -> showEmojiMonitor()
            }
        }
    }

    private fun showGraphMonitor(): Fragment {
        return mGraphFragment
    }

    private fun showNumberMonitor(): Fragment {
        return mNumberFragment
    }

    private fun showEmojiMonitor(): Fragment {
        return mEmojiFragment
    }

}