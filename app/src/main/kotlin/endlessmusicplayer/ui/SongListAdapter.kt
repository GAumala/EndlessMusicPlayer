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
import endlessmusicplayer.data.Song
import endlessmusicplayer.player.MusicActivity
import io.realm.RealmResults
import utils.TimeConverter

/**
 * Adapter for ListView containing songs
 * Created by gabriel on 3/16/16.
 */

class SongListAdapter : RecyclerView.Adapter<SongListAdapter.SongViewHolder>{

    private val mContext : Context
    var realmResults : RealmResults<Song>?
    constructor (ctx : Context, results : RealmResults<Song>) : super (){
        mContext = ctx
        realmResults = results
    }

    override fun onBindViewHolder(mHolder : SongViewHolder?, position : Int) {
        val currentSong = realmResults?.get(position) ?: Song()
        val holder = mHolder
        if(holder != null) {
            holder.titleText.text = currentSong.title
            holder.subText.text = currentSong.artist
            holder.rightSubText.text = TimeConverter.timeToMinutes(currentSong.duration)

            holder.leftImage.setOnClickListener({ view ->
                Log.d("SongListFragment", "clicked")
                val results = realmResults
                if(results != null) {
                    val myActivity = mContext as MusicActivity
                    myActivity.startPlaylist(results, position)
                }
            })
        }

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
