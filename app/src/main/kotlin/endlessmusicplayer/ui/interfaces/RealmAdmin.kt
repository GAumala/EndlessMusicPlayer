package endlessmusicplayer.ui.interfaces

import endlessmusicplayer.data.Artist
import endlessmusicplayer.data.Song
import endlessmusicplayer.ui.ThreePageAdapter
import io.realm.RealmResults

/**
 * Interface for activities that have a realm instance running
 * Created by gabriel on 3/16/16.
 */

interface RealmAdmin {

    /**
     * get all songs from database. If the result has already been requested before, return immediately
     * else return null, but perform the query asynchronously and then update the fragment.
     */
    abstract  fun getAllSongs(page : ThreePageAdapter.FragmentPages) : RealmResults<Song>?
    abstract  fun getAllArtists(page : ThreePageAdapter.FragmentPages) : RealmResults<Artist>?
}
