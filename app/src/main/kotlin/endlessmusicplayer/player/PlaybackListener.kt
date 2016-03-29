package endlessmusicplayer.player

/**
 * Front end activity should implement an instance of this class and pass it to MusicService to
 * update its UI after playback changes.
 * Created by gabriel on 3/26/16.
 */

abstract class PlaybackListener {
    /**
     * This callback is called periodically while music is being played. It should update the progress
     * of the played track in the UI
     * @param id Id of the currently played track
     * @param progress The current progress of the playback, how many miliseconds of the song have
     * been played so far.
     */
    abstract fun onPlaybackTick(id : Long, progress : Long)

    /**
     * This callback is called when playback starts or resumes. Used mainly to update play/pause buttons
     */
    abstract fun onPlaybackStarted()
    /**
     * This callback is called when playback is paused. Used mainly to update play/pause buttons
     */
    abstract fun onPlaybackPaused()
}
