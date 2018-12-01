package ec.erickmedina.fussionminds.monitor

import android.support.v4.app.Fragment
import ec.erickmedina.fussionminds.entities.MonitorResult

class MonitorGraphFragment : Fragment(), MonitorContract.MonitorView{

    override fun onMonitorResultSuccess(result: MonitorResult) {

    }

    override fun onMonitorResultError() {

    }

}
