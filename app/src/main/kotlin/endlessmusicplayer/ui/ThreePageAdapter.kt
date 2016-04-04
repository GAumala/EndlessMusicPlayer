package endlessmusicplayer.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import utils.CustomFragmentPagerAdapter

/**
 * ViewPagerAdapter for the tabs holding artists, albums and songs
 * Created by gabriel on 3/16/16.
 */

abstract class ThreePageAdapter : CustomFragmentPagerAdapter {

    enum class FragmentPages {
       ARTISTS {
           override fun toString() = "Artists"
       }, ALBUMS {
            override fun toString() = "Albums"
        }, SONGS{
            override fun toString() = "Songs"
        };

    }

    constructor(manager : FragmentManager) : super (manager){
    }

    override fun getCount(): Int {
        return FragmentPages.values().count()
    }

    override fun getItem(p0: Int): Fragment {
        when (FragmentPages.values()[p0]){
            FragmentPages.ARTISTS-> return ArtistGridFragment()
            FragmentPages.ALBUMS-> return  Fragment()
            FragmentPages.SONGS-> return SongListFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val page = FragmentPages.values()[position]
        return page.toString()
    }

}
