package endlessmusicplayer.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Data class to represent music artists
 * Created by gabriel on 3/16/16.
 */

open class Artist (
    @PrimaryKey open var id : Long = 0L,
    open var title : String = "",
    open var hasPhoto : Boolean = false
) : RealmObject(){

}
