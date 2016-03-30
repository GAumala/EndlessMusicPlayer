package endlessmusicplayer.player

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import endlessmusicplayer.data.Song
import io.realm.RealmResults
import java.util.*

/**
 * Service that will play music even when the user is not viewing the app
 * Created by gabriel on 3/16/16.
 */

class MusicService : Service() , MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    //media player
    private var player : MediaPlayer? = null;
    private var currentSong : Int = 0
    val nowPlayingSong : Long
    get() = currentPlayList[currentSong]
    val nowPlayingPosition : Long
    get() {
        val mPlayer = player
        if(mPlayer != null  && status != PlaybackStatus.stopped)
            return mPlayer.currentPosition.toLong()
        else
            return 0L
    }
    private var songPosition : Long = 0
    private val songCollection : ArrayList<Long> = ArrayList()
    lateinit private var currentPlayList : ArrayList<Long>


    private val TICK_DURATION = 500L
    private val handler = Handler()
    var playbackListener : PlaybackListener? = null
    private var status = PlaybackStatus.stopped
    set(value) {
        when (value){
            PlaybackStatus.playing -> playbackListener?.onPlaybackStarted()
            PlaybackStatus.paused -> playbackListener?.onPlaybackPaused()
        }
        field = value
    }

    val musicPlaybackStatus : PlaybackStatus
    get() = status
    val updateRunnable = object : Runnable {
        override fun run() {
            if(status == PlaybackStatus.playing){
                playbackListener?.onPlaybackTick(currentPlayList[currentSong], songPosition)
                songPosition += TICK_DURATION
            }

            if(status != PlaybackStatus.stopped){
                handler.postDelayed(this, TICK_DURATION)
            }
        }

    }

    private val musicBind  = MusicBinder()

    override fun onCompletion(p0: MediaPlayer?) {
        playbackListener?.onPlaybackTick(currentPlayList[currentSong], p0!!.currentPosition.toLong())
        if(status == PlaybackStatus.playing) {
            player?.reset()
            currentSong++;
            if(currentSong < currentPlayList.size){
                playCurrentSong()
            } else
                setRandomPlaylist()

        } else {
           player?.release()
        }
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        return false
    }

    override fun onPrepared(p0: MediaPlayer?) {
        player?.start()
        songPosition = 0
        if(status == PlaybackStatus.stopped) {
            status = PlaybackStatus.playing
            updateRunnable.run()
        }

    }

    override fun onBind(p0: Intent?): IBinder? {
        //Log.d("MusicService", "bind service ")
        return musicBind
    }

    override fun onRebind(intent: Intent?) {
        //Log.d("MusicService", "rebind service ")
        val mPlayer = player
        if(mPlayer != null && status != PlaybackStatus.stopped) //currentPosition may be higher that the last one we calculated
            songPosition = mPlayer.currentPosition.toLong()

        if(status == PlaybackStatus.playing) //resume posting updates
            updateRunnable.run()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if(status == PlaybackStatus.stopped) {
            //player?.stop()
            player?.release()
            stopSelf()
        }
        //Log.d("MusicService", "unbind service ")
        handler.removeCallbacks(updateRunnable)
        return true

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Log.d("MusicService", "start sticky")
        return Service.START_STICKY
    }

    override fun onDestroy() {
        //Log.d("MusicService", "service destroy")
        super.onDestroy()
    }

    fun getRandomSongFromCollection() : Long{
        val ran = Random();
        if(songCollection.size == 0)
            return 0L
        val x = ran.nextInt(songCollection.size)
        return songCollection[x]
    }

    fun playCurrentSong(){
        val songId = currentPlayList[currentSong]
        val trackUri = ContentUris.withAppendedId(
        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
        try{
            if(status != PlaybackStatus.stopped) { //We are changing songs, do some cleanup and "stop" playback
                player!!.reset()
                handler.removeCallbacks(updateRunnable)
                status = PlaybackStatus.stopped
            }
            player!!.setDataSource(applicationContext, trackUri);
            player!!.prepareAsync();
        }
        catch(e : Exception){
            e.printStackTrace()
            Log.e("MUSIC SERVICE", "Error setting data source. song Id = $songId");
        }

    }

    public fun setRandomPlaylist(){
        val newPlaylist = ArrayList<Long>()
        val randomValues = ArrayList<Long>()
        randomValues.addAll(songCollection)
        while(randomValues.isNotEmpty()){
            val rand = Random()
            val index = rand.nextInt(randomValues.size)
            newPlaylist.add(randomValues.removeAt(index))
        }

        currentPlayList = newPlaylist
        currentSong = 0
        playCurrentSong()


    }

    private fun newSongIdList(songs : RealmResults<Song>) : ArrayList<Long>{
        val ids = ArrayList<Long>()
            for(song in songs)
                ids.add(song.id)
            return ids
    }
    fun startPlayback(playlist : RealmResults<Song>, startPosition : Int){
        currentPlayList = newSongIdList(playlist)
        currentSong = startPosition
        playCurrentSong()


    }
    override fun onCreate() {
        super.onCreate()


        if(player == null) {
            //Log.d("MusicService", "Service created")
            songPosition = 0
            currentSong = -1
            currentPlayList = songCollection
            status = PlaybackStatus.stopped
            initMusicPlayer()
        }
    }

    public fun toggleMusicPlayback(){
        val mediaPlayer = player
        if(mediaPlayer != null){
            when(status){
                PlaybackStatus.playing -> {
                    mediaPlayer.pause()
                    status = PlaybackStatus.paused
                }
                PlaybackStatus.paused -> {
                    mediaPlayer.start()
                    status = PlaybackStatus.playing
                }
            }
        }
    }

    fun stopMusicPlayback(){
        status = PlaybackStatus.stopped
    }

    private fun initMusicPlayer() {
        val newPlayer = MediaPlayer()
        newPlayer.setWakeMode(applicationContext,
                PowerManager.PARTIAL_WAKE_LOCK);
        newPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        newPlayer.setOnPreparedListener(this);
        newPlayer.setOnCompletionListener(this);
        newPlayer.setOnErrorListener(this);
        player = newPlayer
    }

    fun setSongs(realmCollection : RealmResults<Song> ){
        val songs = newSongIdList(realmCollection)
        songCollection.clear()
        songCollection.addAll(songs)

    }

    inner class MusicBinder : Binder() {
        fun getService() : MusicService{
            return this@MusicService;
        }
    }
}
