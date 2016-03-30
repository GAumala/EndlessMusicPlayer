package endlessmusicplayer.ui

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import com.truizlop.fabreveallayout.FABRevealLayout
import ec.orangephi.endlessmusicplayer.R
import endlessmusicplayer.data.RealmActivity
import endlessmusicplayer.data.Song
import endlessmusicplayer.player.MusicActivity
import endlessmusicplayer.player.MusicService
import endlessmusicplayer.player.PlaybackListener
import endlessmusicplayer.player.PlaybackStatus
import endlessmusicplayer.ui.interfaces.AdapterFragment
import endlessmusicplayer.ui.interfaces.RealmAdmin
import endlessmusicplayer.ui.interfaces.ScrollableActivity
import endlessmusicplayer.ui.materialplaypause.PlayPauseView
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults
import java.util.*

/**
 * Main Activity with 3 tabs: artists, albums and songs.
 * Created by gabriel on 3/16/16.
 */

class MainTabActivity : MusicActivity(), RealmAdmin, ScrollableActivity {


    private var viewPager : ViewPager? = null
    lateinit private var fab_layout : FABRevealLayout

    var fabLayoutIsVisible = true
    var fabIsVisible = true

    private val adapter : ThreePageAdapter = object : ThreePageAdapter(supportFragmentManager){
        override fun getFragmentForPosition(position: Int): Fragment? {
            val tag = makeFragmentName(viewPager?.id ?: 1, getItemId(position));
            val fragment = supportFragmentManager.findFragmentByTag(tag);
            return fragment;
        }
    }

    private val playbackListener = object : PlaybackListener() {
        override fun onPlaybackStarted() {
            btnPlayPause?.toggle()
        }

        override fun onPlaybackPaused() {
            btnPlayPause?.toggle()
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

    private val myScrollListener = object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    if(fabLayoutIsVisible && dy > 0){
                        //Toast.makeText(this@MainTabActivity, "Hide", Toast.LENGTH_SHORT).show()
                        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down);
                        fab_layout.startAnimation(anim)
                        fabLayoutIsVisible = false
                    } else if(!fabLayoutIsVisible && dy < 0){
                        //Toast.makeText(this@MainTabActivity, "Show", Toast.LENGTH_SHORT).show()
                        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up);
                        fab_layout.startAnimation(anim)
                        fabLayoutIsVisible = true
                    }
                }
            }

    private fun revealMusicPlaybackBar(){
        fab_layout.revealSecondaryView()
        fabIsVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_main_tabs)

        progressbar = findViewById(R.id.progressbar) as ProgressBar

        songArtist = findViewById(R.id.song_artist) as TextView
        songTitle = findViewById(R.id.song_title) as TextView
        btnPlayPause = findViewById(R.id.play_pause_btn) as PlayPauseView
        btnPlayPause!!.setOnClickListener({
            musicService?.toggleMusicPlayback()
        })
        btnPlayPause!!.toggle()

       val toolbar = findViewById(R.id.toolbar) as Toolbar
       setSupportActionBar(toolbar)

       val pager = findViewById(R.id.pager) as ViewPager
       viewPager = pager
       pager.adapter = adapter

       val tabLayout = findViewById(R.id.tablayout) as TabLayout
       tabLayout.setupWithViewPager(pager)

       fab_layout =  findViewById(R.id.fab_reveal_layout) as FABRevealLayout
       val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener({
            revealMusicPlaybackBar()
        })

        //musicService?.playbackListener = playbackListener



    }

    override fun getPlaybackListener(): PlaybackListener {
        return playbackListener
    }

    override fun startPlaylist(playlist: RealmResults<Song>, position: Int) {
        super.startPlaylist(playlist, position)
        //animate fab layout
        if(fabLayoutIsVisible && fabIsVisible)
            revealMusicPlaybackBar()
        else {
            val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up);
            anim.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationStart(p0: Animation?) {
                }
                override fun onAnimationRepeat(p0: Animation?) {
                }
                override fun onAnimationEnd(p0: Animation?) {
                    if(fabIsVisible) revealMusicPlaybackBar()
                }
            })
            fab_layout.startAnimation(anim)
            fabLayoutIsVisible = true

        }
    }

    private fun updateProgressBar(prog : Long, max : Long) {
        val percentage = 100 * prog/max
        progressbar?.progress = percentage.toInt()
    }
    private fun displayCurrentSong(id : Long){
        val song = getRealm()!!.where(Song::class.java).equalTo("id", id).findFirst()
        songTitle?.text = song.title
        songArtist?.text = song.artist
        currentSong = song
    }

    override fun onMusicServiceBinded(status: PlaybackStatus) {
        if(status != PlaybackStatus.stopped && fabIsVisible) {
            revealMusicPlaybackBar()
            if(status == PlaybackStatus.playing) btnPlayPause?.toggle()
        }

        if(status == PlaybackStatus.paused)
            displayCurrentSong(musicService!!.nowPlayingSong)
    }

   /*
    REALM ADMIN
     */
    override fun getAllSongs(page: ThreePageAdapter.FragmentPages): RealmResults<Song>? {
        val myRealm = getRealm()
        if (myRealm != null) {
            if(songList.isLoaded)
                return songList
            else
                songList.addChangeListener(object : RealmChangeListener {
                    override fun onChange() {
                        songList.removeChangeListener(this)
                        val songFragment = adapter.getFragmentForPosition(ThreePageAdapter.FragmentPages.SONGS.ordinal) as AdapterFragment
                        songFragment.setData(songList as RealmResults<RealmObject>)
                    }
                })

        }
        return null

    }

    /*
    SCROLLABLE ACTIVIVITY
     */
    override fun getScrollListener(): RecyclerView.OnScrollListener {
        return myScrollListener
    }


}
