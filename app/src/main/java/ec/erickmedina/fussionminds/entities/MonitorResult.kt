package ec.erickmedina.fussionminds.entities

import android.content.Context
import ec.erickmedina.fussionminds.utils.PreferenceUtils

data class MonitorResult (val id:Int, val time: Long, val heartRate: Int, val result:String) {
    fun heartRateType(context:Context): Int {
        return when{
            heartRate >= PreferenceUtils.getHeartRateMax(context) -> HEARTBEAT_HIGH
            heartRate <= PreferenceUtils.getHeartRateMin(context) -> HEARTBEAT_LOW
            else -> HEARTBEAT_NORMAL
        }

    }

    companion object {
        public const val HEARTBEAT_NORMAL = 0
        public const val HEARTBEAT_HIGH = 1
        public const val HEARTBEAT_LOW = -1
    }
}
