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
import endlessmusicplayer.data.RealmRecyclerAdapter
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

class SongListFragment : RecyclerFragment(){

    override val realmData: RealmResults<RealmObject>
        get() {
            val realmAdmin = activity as RealmAdmin
            return realmAdmin.getAllSongs(ThreePageAdapter.FragmentPages.SONGS) as RealmResults<RealmObject>
        }

    override val fragmentLayout: Int
        get() = R.layout.list

    override val layoutManager: RecyclerView.LayoutManager
        get() = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

    override fun newRecyclerAdapter(results: RealmResults<RealmObject>): RealmRecyclerAdapter {
        return SongListAdapter(activity, results as RealmResults<Song>)
    }





}
