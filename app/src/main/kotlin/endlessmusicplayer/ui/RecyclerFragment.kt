package endlessmusicplayer.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.orangephi.endlessmusicplayer.R
import endlessmusicplayer.data.RealmRecyclerAdapter
import endlessmusicplayer.data.Song
import endlessmusicplayer.ui.interfaces.RealmAdmin
import endlessmusicplayer.ui.interfaces.ScrollableActivity
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Created by gabriel on 4/3/16.
 */

abstract class RecyclerFragment : Fragment() {

    lateinit var recyclerview : RecyclerView
    var listadapter : RealmRecyclerAdapter? = null

    abstract val realmData : RealmResults<RealmObject>

    abstract fun newRecyclerAdapter(results : RealmResults<RealmObject>) : RealmRecyclerAdapter

    abstract val fragmentLayout : Int

    abstract val layoutManager : RecyclerView.LayoutManager

    protected fun setupAdapter(results : RealmResults<RealmObject>){
        //val newAdapter = SongListAdapter(activity, results as RealmResults<Song>)
        val newAdapter = newRecyclerAdapter(results)
        recyclerview.adapter = newAdapter
        listadapter = newAdapter
    }
    public fun setData(results: RealmResults<RealmObject>) {
        setupAdapter(results)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(inflater != null) {
            val view = inflater.inflate(fragmentLayout, null)

            recyclerview = view.findViewById(R.id.recycler) as RecyclerView
            //recyclerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recyclerview.layoutManager = layoutManager
            val scrollActivity = activity as ScrollableActivity
            recyclerview.addOnScrollListener(scrollActivity.getScrollListener())


            return view
        }

        return null
    }



    override fun onStart() {
        super.onStart()
        val realmAdmin = activity as RealmAdmin
        //val results = realmAdmin.getAllSongs(ThreePageAdapter.FragmentPages.SONGS)
        val results = realmData
        if(results != null)
            setupAdapter(results)
    }


    override fun onStop() {
        listadapter?.realmResults = null
        super.onStop()
    }


}
