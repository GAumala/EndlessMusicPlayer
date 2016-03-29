package endlessmusicplayer.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * This class should contain all the information that describes a song in the user's device
 * Created by gabriel on 3/16/16.
 */

open class Song (
    @PrimaryKey open var id : Long = 0L,
    open var title : String = "",
    open var album : String = "",
    open var album_id : Long = 0L,
    open var track : Int = 0,
    open var artist : String = "",
    open var artist_id : Long = 0L,
    open var duration : Long = 0L,
    open var enabled : Boolean = true
) : RealmObject(){

}
