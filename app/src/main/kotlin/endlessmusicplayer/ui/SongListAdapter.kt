package endlessmusicplayer.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import ec.orangephi.endlessmusicplayer.R
import endlessmusicplayer.data.RealmRecyclerAdapter
import endlessmusicplayer.data.Song
import endlessmusicplayer.player.MusicActivity
import io.realm.RealmObject
import io.realm.RealmResults
import utils.TimeConverter

/**
 * Adapter for ListView containing songs
 * Created by gabriel on 3/16/16.
 */

class SongListAdapter : RealmRecyclerAdapter{

    constructor(mContext : Context, results: RealmResults<Song>) : super(mContext,results as RealmResults<RealmObject>){

    }

    val songResults : RealmResults<Song>?
            get() = realmResults as RealmResults<Song>?


    override fun onBindViewHolder(mHolder : RecyclerView.ViewHolder?, position : Int) {
        val currentSong = songResults?.get(position)  ?: Song()
        val holder = mHolder as SongViewHolder
        holder.titleText.text = currentSong.title
        holder.subText.text = currentSong.artist
        holder.rightSubText.text = TimeConverter.timeToMinutes(currentSong.duration)

        (holder.leftImage.parent as View).setOnClickListener({ view ->
            Log.d("SongListFragment", "clicked")
            val results = songResults
            if(results != null) {
                val myActivity = mContext as MusicActivity
                myActivity.startPlaylist(results , position)
            }
        })

    }

    override fun getItemCount(): Int {
        return realmResults?.size ?: 0
    }

    override fun onCreateViewHolder(parent : ViewGroup?, p1: Int): SongViewHolder? {
        val view = LayoutInflater.from(mContext).inflate(R.layout.song_list_item, parent, false);
        return SongViewHolder(view)
    }

    /**
     * Holder subclass for Songs.
     *
     */
	class SongViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val leftImage : CircleImageView
	    val titleText : TextView
	    val subText : TextView
	    val rightSubText : TextView
	    val overflowIcon : ImageButton

        init {
            leftImage = view.findViewById(R.id.songsListAlbumThumbnail) as CircleImageView
            titleText = view.findViewById(R.id.songNameListView) as TextView
            subText = view.findViewById(R.id.artistNameSongListView) as TextView
            rightSubText = view.findViewById(R.id.songDurationListView) as TextView
            overflowIcon = view.findViewById(R.id.overflow_icon) as ImageButton
        }

	}
}
