package endlessmusicplayer.ui

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import ec.orangephi.endlessmusicplayer.R
import endlessmusicplayer.data.Artist
import endlessmusicplayer.data.RealmRecyclerAdapter
import endlessmusicplayer.ui.interfaces.RealmAdmin
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Created by gabriel on 4/3/16.
 */

class ArtistGridFragment : RecyclerFragment() {
    override val realmData: RealmResults<RealmObject>?
        get() {
            val realmAdmin = activity as RealmAdmin
            return realmAdmin.getAllArtists(ThreePageAdapter.FragmentPages.ARTISTS) as RealmResults<RealmObject>?
        }

    override fun newRecyclerAdapter(results: RealmResults<RealmObject>): RealmRecyclerAdapter {
        return ArtistGridAdapter(activity, results as RealmResults<Artist>)
    }

    override val fragmentLayout: Int
        get() = R.layout.list

    override val layoutManager: RecyclerView.LayoutManager
        get() = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)

}
