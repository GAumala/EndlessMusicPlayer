package endlessmusicplayer.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ec.orangephi.endlessmusicplayer.R
import endlessmusicplayer.data.Song
import endlessmusicplayer.ui.interfaces.AdapterFragment
import endlessmusicplayer.ui.interfaces.RealmAdmin
import endlessmusicplayer.ui.interfaces.ScrollableActivity
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Fragment to display a list of songs. when clicked, song should be added to new queue and played
 * Created by gabriel on 3/16/16.
 */

class SongListFragment : Fragment(), AdapterFragment {
    lateinit var recyclerview : RecyclerView
    var listadapter : SongListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(inflater != null) {
            val view = inflater.inflate(R.layout.list, null)

            recyclerview = view.findViewById(R.id.recycler) as RecyclerView
            recyclerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            val scrollActivity = activity as ScrollableActivity
            recyclerview.addOnScrollListener(scrollActivity.getScrollListener())


            return view
        }

        return null
    }

    override fun onStart() {
        super.onStart()
        val realmAdmin = activity as RealmAdmin
        val results = realmAdmin.getAllSongs(ThreePageAdapter.FragmentPages.SONGS)
        if(results != null)
            setupAdapter(results)
    }

    override fun onStop() {
        listadapter?.realmResults = null
        super.onStop()
    }

    private fun setupAdapter(results : RealmResults<Song>){
        val newAdapter = SongListAdapter(activity, results)
        recyclerview.adapter = newAdapter
        listadapter = newAdapter
    }
    override fun setData(results: RealmResults<RealmObject>) {
        setupAdapter(results as RealmResults<Song>)
    }


}
