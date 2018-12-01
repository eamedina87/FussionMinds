package ec.erickmedina.fussionminds.utils

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import ec.erickmedina.fussionminds.R
import ec.erickmedina.fussionminds.entities.ActivityResult
import ec.erickmedina.fussionminds.entities.MonitorResult
import ec.erickmedina.fussionminds.entities.SleepResult
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Utils {

    companion object {

        private val ONE_DAY = 24 * 3600 * 1000

        fun getEmojiForResult(context: Context?, result:MonitorResult) : Int {
            if (context==null) return -1
            return when (result.heartRateType(context)){
                MonitorResult.HEARTBEAT_NORMAL -> R.mipmap.ic_emoji_happy
                MonitorResult.HEARTBEAT_HIGH -> R.mipmap.ic_emoji_surprised
                else -> R.mipmap.ic_emoji_sad
            }
        }



        fun getActivityDummyData():ArrayList<ActivityResult>{
            var activityList = ArrayList<ActivityResult>()
            activityList.add(ActivityResult(ActivityResult.Type.STATIONARY, System.currentTimeMillis()))
            activityList.add(ActivityResult(ActivityResult.Type.STATIONARY, System.currentTimeMillis()-ONE_DAY + ONE_DAY/10))
            activityList.add(ActivityResult(ActivityResult.Type.STATIONARY, System.currentTimeMillis()-(2* ONE_DAY + ONE_DAY/8)))
            activityList.add(ActivityResult(ActivityResult.Type.STATIONARY, System.currentTimeMillis()-(3* ONE_DAY + ONE_DAY/5)))
            activityList.add(ActivityResult(ActivityResult.Type.BIKE, System.currentTimeMillis()-(4* ONE_DAY)))
            activityList.add(ActivityResult(ActivityResult.Type.VEHICLE, System.currentTimeMillis()-(4* ONE_DAY + ONE_DAY/3)))
            activityList.add(ActivityResult(ActivityResult.Type.BIKE, System.currentTimeMillis()-(5* ONE_DAY)))
            activityList.add(ActivityResult(ActivityResult.Type.WALK, System.currentTimeMillis()-(5* ONE_DAY + ONE_DAY/4)))
            return activityList
        }

        fun getActivityImageFor(type: ActivityResult.Type): Int {
            return when (type){
                ActivityResult.Type.WALK -> R.drawable.ic_activity_walk
                ActivityResult.Type.STATIONARY -> R.drawable.ic_activity_stationary
                ActivityResult.Type.VEHICLE -> R.drawable.ic_activity_vehicle
                ActivityResult.Type.BIKE -> R.drawable.ic_activity_bike
                else -> R.drawable.ic_activity_run
            }
        }

        fun getActivityDescriptionFor(type: ActivityResult.Type): String {
            return when (type){
                ActivityResult.Type.WALK -> "Walking"
                ActivityResult.Type.STATIONARY -> "Stationary"
                ActivityResult.Type.VEHICLE -> "Vehicle"
                ActivityResult.Type.BIKE -> "Biking"
                else -> "Running"
            }
        }

        fun getDateStringFor(date: Long): String {
            //val mDate = Date(date)
            //val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
            if (DateUtils.isToday(date)) return "Today" else
            return DateUtils.getRelativeTimeSpanString(date).toString()
            //return format.format(mDate)
        }

        fun getSleepDummyData(): ArrayList<SleepResult> {
            var activityList = ArrayList<SleepResult>()
            activityList.add(SleepResult(4, System.currentTimeMillis()))
            activityList.add(SleepResult(6, System.currentTimeMillis()-(1 *ONE_DAY)))
            activityList.add(SleepResult(5, System.currentTimeMillis()-(2* ONE_DAY)))
            activityList.add(SleepResult(4, System.currentTimeMillis()-(3* ONE_DAY)))
            activityList.add(SleepResult(5, System.currentTimeMillis()-(4* ONE_DAY)))
            activityList.add(SleepResult(3, System.currentTimeMillis()-(5* ONE_DAY)))
            activityList.add(SleepResult(4, System.currentTimeMillis()-(6* ONE_DAY)))
            activityList.add(SleepResult(5, System.currentTimeMillis()-(7* ONE_DAY)))
            return activityList
        }

        fun getAlertVisibilityFor(time: Int): Int {
            return when {
                time >= 5 -> View.INVISIBLE
                else -> View.VISIBLE
            }
        }

    }

}
