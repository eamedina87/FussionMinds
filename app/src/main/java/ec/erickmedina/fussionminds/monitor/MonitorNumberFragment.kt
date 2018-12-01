package ec.erickmedina.fussionminds.monitor

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.erickmedina.fussionminds.R
import ec.erickmedina.fussionminds.entities.MonitorResult
import kotlinx.android.synthetic.main.layout_monitor_number.*

class MonitorNumberFragment : Fragment(), MonitorContract.MonitorView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_monitor_number, container, false)
    }

    override fun onMonitorResultSuccess(result: MonitorResult) {
        if (isVisible)
            heartbeat_real.text = getString(R.string.monitor_result_number, result.heartRate)
    }

    override fun onMonitorResultError() {

    }

}
