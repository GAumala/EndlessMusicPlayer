package endlessmusicplayer.player

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.*
import endlessmusicplayer.data.RealmActivity
import endlessmusicplayer.data.Song
import endlessmusicplayer.ui.materialplaypause.PlayPauseView
import io.realm.RealmChangeListener
import io.realm.RealmResults
import java.util.*

/**
 * Created by gabriel on 3/25/16.
 */

abstract open class MusicActivity : RealmActivity() {

    lateinit var playIntent : Intent
    protected var musicService : MusicService? = null
    private var musicBound : Boolean = false
    protected val hasBoundService : Boolean
    get() = musicBound

    //Music UI
    protected var progressbar: ProgressBar? = null
    protected var songTitle : TextView? = null
    protected var songArtist : TextView? = null
    protected var btnPlayPause : PlayPauseView? = null

    protected var currentSong : Song? = null

    private val musicConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as MusicService.MusicBinder
            val msService = binder.getService()
            musicService = msService
            //Log.d("MusicActivity", "Service connected")
            val songs = getSongIds()
            if (songs != null)
                msService.setSongs(songs)

            musicBound = true

            msService.playbackListener = getPlaybackListener()
            onMusicServiceBinded(msService.musicPlaybackStatus)

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            musicBound = false
            //Log.d("MusicActivity", "Service disconnected")
        }
    }

    lateinit var mPlaybackListener : PlaybackListener
    open fun getPlaybackListener() : PlaybackListener {
        mPlaybackListener = object : PlaybackListener() {
            override fun onPlaybackStarted() {
                btnPlayPause?.playing = true
            }

            override fun onPlaybackPaused() {
                btnPlayPause?.playing = false
            }

            override fun onPlaybackTick(id: Long, progress: Long) {
                if(currentSong == null && getRealm() == null)
                    return //DEAD ACTIVITY

                if(currentSong == null || currentSong?.id != id){
                    displayCurrentSong(id)
                }
                val duration = currentSong!!.duration
                updateProgressBar(progress, duration)
            }
        }
        return mPlaybackListener
    }

    open protected fun updateProgressBar(prog : Long, max : Long) {
        val percentage = 100 * prog/max
        progressbar?.progress = percentage.toInt()
    }

    open protected fun displayCurrentSong(id : Long){
        val song = getRealm()!!.where(Song::class.java).equalTo("id", id).findFirst()
        songTitle?.text = song.title
        songArtist?.text = song.artist
        currentSong = song
    }

    open fun startPlaylist(playlist : RealmResults<Song>, position : Int){
        musicService?.startPlayback(playlist, position) ?: Log.e("MainTabActivity", "CANT PLAY SONGS")
    }

    /**
     * Service should only call this at start to get all available songs.
     */
    private fun getSongIds() : RealmResults<Song>? {
        val songs = songList
        if(songs.isLoaded) {
            return songs
        } else {
            songList.addChangeListener (object : RealmChangeListener {
                override fun onChange() {
                    songList.removeChangeListener(this)
                    musicService!!.setSongs(songList)
                }

            })
            return null
        }
    }

    /**
     * Removes the current playbackListener that the service holds. This must be called before
     * going to other activivities because this activity's onStop() method may remove another
     * activity's listener.
     */
    private fun removePlaybackListener(){
        val mService = musicService
        if(mService != null){
            if(mService.playbackListener == mPlaybackListener)
                mService.playbackListener = null
        }
    }


     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

     }

    override fun onStart() {
        super.onStart()
        playIntent = Intent(applicationContext, MusicService::class.java)
        val isRunning = isMyServiceRunning()
        if(!isRunning)
            startService(playIntent)
        Log.d("MusicActivity", "start activity")
        applicationContext.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        currentSong = null
        if(musicBound) {
            removePlaybackListener()
            //gotta use application context for unbind http://stackoverflow.com/questions/10986408/unable-to-destroy-activity-service-is-not-registered
            applicationContext.unbindService(musicConnection)
        }
        super.onStop()

    }

    override fun onBackPressed() {
        if(musicBound) {
            if(musicService?.musicPlaybackStatus == PlaybackStatus.paused)
                musicService?.stopMusicPlayback()
            musicBound = false
            applicationContext.unbindService(musicConnection)
            musicService = null;
        }
        super.onBackPressed()

    }

    override fun onDestroy() {
        currentSong = null
        super.onDestroy()
    }

    protected fun isMyServiceRunning() : Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicService::class.java.name.equals(service.service.className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Callback called after activity binds with music service. In this callback, the front end
     * activity should update its UI to reflect the current playback status
     * @param status the playback status of the MusicService
     */
    abstract open fun onMusicServiceBinded(status : PlaybackStatus)

}