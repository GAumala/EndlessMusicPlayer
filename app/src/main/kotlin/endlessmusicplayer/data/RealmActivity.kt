package endlessmusicplayer.data

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.realm.Realm
import io.realm.RealmResults

/**
 * Class for Activities that can access Realm.
 * Created by gabriel on 3/16/16.
 */

open class RealmActivity : AppCompatActivity() {
    private var realm : Realm? = null
    lateinit var songList : RealmResults<Song>
    lateinit var artistList : RealmResults<Artist>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRealm()
    }

    override fun onStop() {
        realm?.close()
        realm = null
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        if(realm == null)
            initRealm()
    }

    private fun initRealm() {
        realm = Realm.getDefaultInstance()
        songList = realm!!.where(Song::class.java).findAllAsync()
        artistList = realm!!.where(Artist::class.java).findAllAsync()
    }
    open fun getRealm() : Realm? = realm
}
