package endlessmusicplayer.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import ec.orangephi.endlessmusicplayer.R
import endlessmusicplayer.data.Artist
import endlessmusicplayer.data.RealmRecyclerAdapter
import endlessmusicplayer.player.MusicActivity
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Created by gabriel on 4/3/16.
 */

class ArtistGridAdapter : RealmRecyclerAdapter{

    constructor(mContext : Context, results: RealmResults<Artist>) : super(mContext,results as RealmResults<RealmObject>){

    }
    val artistResults : RealmResults<Artist>?
            get() = realmResults as RealmResults<Artist>?

    override fun onCreateViewHolder(parent: ViewGroup?, p1: Int): RecyclerView.ViewHolder? {
        val view = LayoutInflater.from(mContext).inflate(R.layout.grid_view_item, parent, false);
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(mHolder: RecyclerView.ViewHolder?, position: Int) {
        val currentArtist = artistResults?.get(position)  ?: Artist()
        val holder = mHolder as ArtistViewHolder
        holder.titleText.text = currentArtist.title

        /*
        (holder.leftImage.parent as View).setOnClickListener({ view ->
            Log.d("SongListFragment", "clicked")
            val results = songResults
            if(results != null) {
                val myActivity = mContext as MusicActivity
                myActivity.startPlaylist(results , position)
            }
        })
        */

    }

    /**
     * Holder subclass for Artists.
     *
     */
	class ArtistViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val image : ImageView
	    val titleText : TextView
	    val overflowIcon : ImageButton

        init {
            image = view.findViewById(R.id.gridViewImage) as ImageView
            titleText = view.findViewById(R.id.gridViewTitleText) as TextView
            overflowIcon = view.findViewById(R.id.overflow_icon) as ImageButton
        }

	}

}