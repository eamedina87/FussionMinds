package ec.erickmedina.fussionminds.monitor

import ec.erickmedina.fussionminds.entities.MonitorResult

interface MonitorContract {

    interface MonitorView{
        fun onMonitorResultSuccess(result: MonitorResult)
        fun onMonitorResultError()
    }

    interface MonitorPresenter{
        fun startMonitor()
        fun stopMonitor()
        fun getMonitorSettings()
        fun onCreate()
    }

}