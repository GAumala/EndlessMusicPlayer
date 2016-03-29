package endlessmusicplayer.player

/**
 * Status of the MusicService
 * STOPPED : The service starts with this value. Service is not playing any tracks, and most likely
 * does not have any playlist. If all clients unbind during this status, service should stop itself
 * PAUSED : The service was playing music recently, but it has paused playback momentarily. If there
 * are bound clients, updates are still being posted, but they all have the same data.
 * * PLAYING : The service is currently playing music. If there are bound clients, Service will
 * post updates to the clients
 * Created by gabriel on 3/26/16.
 */

enum class PlaybackStatus {
    stopped, paused, playing
}
