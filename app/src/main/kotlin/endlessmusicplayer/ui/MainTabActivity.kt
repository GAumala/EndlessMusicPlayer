package endlessmusicplayer.ui

import android.animation.*
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import com.truizlop.fabreveallayout.FABRevealLayout
import com.truizlop.fabreveallayout.OnRevealChangeListener
import ec.orangephi.endlessmusicplayer.R
import endlessmusicplayer.data.RealmActivity
import endlessmusicplayer.data.Song
import endlessmusicplayer.player.MusicActivity
import endlessmusicplayer.player.MusicService
import endlessmusicplayer.player.PlaybackListener
import endlessmusicplayer.player.PlaybackStatus
import endlessmusicplayer.ui.anim.AnimatorEndListener
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

    private val myScrollListener = object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    if(fabLayoutIsVisible && dy > 0){
                        //Toast.makeText(this@MainTabActivity, "Hide", Toast.LENGTH_SHORT).show()
                        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out_down);
                        fab_layout.startAnimation(anim)
                        fabLayoutIsVisible = false
                    } else if(!fabLayoutIsVisible && dy < 0){
                        //Toast.makeText(this@MainTabActivity, "Show", Toast.LENGTH_SHORT).show()
                        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_in_up);
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
        btnPlayPause!!.playing = false

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
            musicService?.setRandomPlaylist()
            fab_layout.setOnRevealChangeListener(object : OnRevealChangeListener{
                override fun onSecondaryViewAppeared(p0: FABRevealLayout?, p1: View?) {
                    fab_layout.setOnRevealChangeListener(null)
                    playTransition()
                }

                override fun onMainViewAppeared(p0: FABRevealLayout?, p1: View?) {
                    throw UnsupportedOperationException()
                }

            })
            revealMusicPlaybackBar()
        })

        //musicService?.playbackListener = playbackListener



    }

    override fun startPlaylist(playlist: RealmResults<Song>, position: Int) {
        super.startPlaylist(playlist, position)
        //animate fab layout
        if(fabLayoutIsVisible && fabIsVisible)
            revealMusicPlaybackBar()
        else {
            val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_in_up);
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

    private var reverSet : AnimatorSet? = null
    private fun playTransition(){
        val appLayout = findViewById(R.id.appbarlayout)
        val appLayoutAnim = ObjectAnimator.ofFloat(appLayout, "y", appLayout.y, appLayout.y - appLayout.height)
        val appLayoutAnimReverse = ObjectAnimator.ofFloat(appLayout, "y", appLayout.y - appLayout.height, appLayout.y)
        val fabLayoutAnim = ObjectAnimator.ofFloat(fab_layout, "y", fab_layout.y, fab_layout.y + appLayout.height)
        val fabLayoutAnimReverse = ObjectAnimator.ofFloat(fab_layout, "y", fab_layout.y + appLayout.height, fab_layout.y)
        val recyclerViewAnim = ObjectAnimator.ofFloat(viewPager, "alpha", 1.0f, 0f)
        val recyclerViewAnimReverse = ObjectAnimator.ofFloat(viewPager, "alpha", 0f, 1.0f)
        //val bgAnim = ObjectAnimator.ofFloat(findViewById(R.id.container), "bac")
        val bgAnim = AnimatorInflater.loadAnimator(this, R.anim.white_to_black_object) as ObjectAnimator;
        bgAnim.target = findViewById(R.id.container);
        bgAnim.setEvaluator(ArgbEvaluator());
        (bgAnim as Animator).addListener(object : AnimatorEndListener() {
            override fun onAnimationEnd(p0: Animator?) {
                startActivity(Intent(this@MainTabActivity, PlaybackActivity::class.java))
                overridePendingTransition(0,0)
            }
        });
        val bgAnimReverse = AnimatorInflater.loadAnimator(this, R.anim.black_to_white_object) as ObjectAnimator;
        bgAnimReverse.target = findViewById(R.id.container);
        bgAnimReverse.setEvaluator(ArgbEvaluator());

        val animSet = AnimatorSet()
        animSet.duration = 500
        animSet.playTogether(appLayoutAnim, fabLayoutAnim, recyclerViewAnim, bgAnim)
        animSet.startDelay = 100

        reverSet = AnimatorSet()
        reverSet!!.duration = 500
        reverSet!!.playTogether(appLayoutAnimReverse, fabLayoutAnimReverse, recyclerViewAnimReverse,
                bgAnimReverse)

        animSet.start()

    }



    override fun onMusicServiceBinded(status: PlaybackStatus) {
        if(reverSet != null){
            fab_layout.post {
                reverSet?.start()
                reverSet = null
            }

            if(status == PlaybackStatus.playing) btnPlayPause?.playing = true
        } else if(status != PlaybackStatus.stopped && fabIsVisible) {
            revealMusicPlaybackBar()
            if(status == PlaybackStatus.playing) btnPlayPause?.playing = true
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
