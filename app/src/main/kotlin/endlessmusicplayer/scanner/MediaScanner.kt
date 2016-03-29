package endlessmusicplayer.scanner

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import endlessmusicplayer.data.Album
import endlessmusicplayer.data.Artist
import endlessmusicplayer.data.Song
import io.realm.Realm

/**
 * Class that uses a content provider to get all the music stored in the device
 * Created by gabriel on 3/16/16.
 */

open class MediaScanner(resolver : ContentResolver) : AsyncTask<Void, Int, Int>() {

    val musicResolver = resolver

    override fun doInBackground(vararg p0: Void?): Int? {
        val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        var musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            val artistIdColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST_ID);
            val albumColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
            val durationColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
            val trackColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TRACK);
            val albumIdColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID);
            //add songs to list
            var count = 0
            var oldArtistId = 0L
            var oldAlbumId = 0L

            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            do {

                if(isCancelled) break

                val thisId = musicCursor.getLong(idColumn);
                val thisTitle = musicCursor.getString(titleColumn);
                val thisArtist = musicCursor.getString(artistColumn);
                val thisArtistId = musicCursor.getLong(artistIdColumn);
                val thisAlbum = musicCursor.getString(albumColumn);
                val thisAlbumId = musicCursor.getLong(albumIdColumn);
                val thisDuration = musicCursor.getLong(durationColumn);
                val thisTrack = musicCursor.getInt(trackColumn);

                storeNewSong(realm, thisId, thisTitle, thisAlbumId, thisAlbum, thisArtistId, thisArtist, thisDuration, thisTrack)
                if(oldAlbumId != thisAlbumId) {
                    storeNewAlbum(realm, thisAlbumId, thisAlbum)
                    oldAlbumId = thisAlbumId
                }
                if(oldArtistId != thisArtistId) {
                    storeNewArtist(realm, thisArtistId, thisArtist)
                    oldArtistId = thisArtistId
                }
                count += 1
            }
            while (musicCursor.moveToNext());

            realm.commitTransaction()
            val realmCount = realm.where(Song::class.java).findAll().count()
            realm.close()
            Log.d("MediaScanner", "done $realmCount / $count")
            return count
        }

        return -1

    }

    fun storeNewSong(realm : Realm, id : Long, title : String, albumId : Long, album : String, artistId : Long, artist : String, duration : Long, track : Int){

        val existingSong = realm.where(Song::class.java).equalTo("id", id).findFirst()
        if(existingSong == null){
            val newSong = realm.createObject(Song::class.java)
            newSong.id = id
            newSong.title = title
            newSong.album_id = albumId
            newSong.album = album
            newSong.artist_id = artistId
            newSong.artist = artist
            newSong.duration = duration
            newSong.track = track

            Log.d("MediaScanner", "added: $title")
        }

    }

    fun storeNewAlbum(realm : Realm, id : Long, name : String){
        val existingAlbum = realm.where(Album::class.java).equalTo("id", id).findFirst()
        if(existingAlbum == null){
            val newAlbum = realm.createObject(Album::class.java)
            newAlbum.id = id
            newAlbum.title = name
        }
    }

    fun storeNewArtist(realm : Realm, id : Long, name : String){
        val existingArtist = realm.where(Artist::class.java).equalTo("id", id).findFirst()
        if(existingArtist == null){
            val newArtist = realm.createObject(Artist::class.java)
            newArtist.id = id
            newArtist.title = name
        }
    }

}
