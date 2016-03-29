package endlessmusicplayer

import android.app.Application
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Application class. Holds global variables
 * Created by gabriel on 3/16/16.
 */

class EndlessMusicPlayer : Application(){

    override fun onCreate() {
        super.onCreate();
        // The realm file will be located in Context.getFilesDir() with name "default.realm"
        val config = RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        Log.d("EndlessMusicPlayer", "START APP")
    }
}