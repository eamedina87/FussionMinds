package ec.erickmedina.fussionminds.activity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ec.erickmedina.fussionminds.R
import ec.erickmedina.fussionminds.entities.ActivityResult
import ec.erickmedina.fussionminds.utils.Utils

class ActivityAdapter(val activityDummyData: ArrayList<ActivityResult>) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() =
        activityDummyData.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tempData = activityDummyData[position]
        holder.image?.setImageResource(Utils.getActivityImageFor(tempData.type))
        holder.description?.text = Utils.getActivityDescriptionFor(tempData.type)
        holder.date?.text = Utils.getDateStringFor(tempData.date)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        val image = itemView?.findViewById<ImageView>(R.id.activity_image)
        val description = itemView?.findViewById<TextView>(R.id.activity_description)
        val date = itemView?.findViewById<TextView>(R.id.activity_date)

    }

}
