package ec.erickmedina.fussionminds.sleep

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.erickmedina.fussionminds.R
import ec.erickmedina.fussionminds.utils.Utils
import kotlinx.android.synthetic.main.layout_sleep.*

class SleepFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_sleep, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sleep_recyclerview.adapter = SleepAdapter(Utils.getSleepDummyData())

    }

}