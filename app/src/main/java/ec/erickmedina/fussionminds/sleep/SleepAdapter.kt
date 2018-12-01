package ec.erickmedina.fussionminds.sleep

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ec.erickmedina.fussionminds.R
import ec.erickmedina.fussionminds.entities.SleepResult
import ec.erickmedina.fussionminds.utils.Utils

class SleepAdapter(val activityDummyData: ArrayList<SleepResult>) : RecyclerView.Adapter<SleepAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sleep, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() =
        activityDummyData.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tempData = activityDummyData[position]
        if (tempData.time <= 4) holder.image?.visibility = View.VISIBLE else holder.image?.visibility = View.INVISIBLE
        holder.time?.text = "${tempData.time} hours"
        holder.date?.text = Utils.getDateStringFor(tempData.date)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        val image = itemView?.findViewById<ImageView>(R.id.sleep_image)
        val time = itemView?.findViewById<TextView>(R.id.sleep_time)
        val date = itemView?.findViewById<TextView>(R.id.sleep_date)

    }

}
