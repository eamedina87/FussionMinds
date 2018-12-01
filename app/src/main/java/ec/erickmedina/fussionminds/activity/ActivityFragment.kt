package ec.erickmedina.fussionminds.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.erickmedina.fussionminds.R
import ec.erickmedina.fussionminds.utils.Utils
import kotlinx.android.synthetic.main.layout_activity.*

class ActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity_recyclerview.adapter = ActivityAdapter(Utils.getActivityDummyData())

    }




}