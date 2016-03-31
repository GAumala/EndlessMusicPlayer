package endlessmusicplayer.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import ec.orangephi.endlessmusicplayer.R
import endlessmusicplayer.player.MusicActivity
import endlessmusicplayer.player.PlaybackListener
import endlessmusicplayer.player.PlaybackStatus
import endlessmusicplayer.ui.materialplaypause.PlayPauseView

/**
 * Created by gabriel on 3/31/16.
 */

class PlaybackActivity : MusicActivity() {
    lateinit private var ctrls_layout : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        songArtist = findViewById(R.id.song_artist) as TextView
        songTitle = findViewById(R.id.song_title) as TextView
        ctrls_layout = findViewById(R.id.ctrls)
        btnPlayPause = findViewById(R.id.play_pause_btn) as PlayPauseView
        progressbar = findViewById(R.id.progressbar) as ProgressBar
        btnPlayPause!!.setOnClickListener({
            musicService?.toggleMusicPlayback()
        })
    }

    override fun onMusicServiceBinded(status: PlaybackStatus) {

        if(status != PlaybackStatus.playing) btnPlayPause?.playing = false
        displayCurrentSong(musicService!!.nowPlayingSong)

        ctrls_layout.post({
            val ctrls_anim = ObjectAnimator.ofFloat(ctrls_layout, "y",
                    ctrls_layout.y + ctrls_layout.height, ctrls_layout.y)
            val artist_anim = ObjectAnimator.ofFloat(songArtist, "alpha", 0.0f, 1.0f)
            val title_anim = ObjectAnimator.ofFloat(songArtist, "alpha", 0.0f, 1.0f)
            val animSet = AnimatorSet()
            animSet.duration = 200
            animSet.playTogether(ctrls_anim, artist_anim, title_anim)
            animSet.start()
        })

    }

}
