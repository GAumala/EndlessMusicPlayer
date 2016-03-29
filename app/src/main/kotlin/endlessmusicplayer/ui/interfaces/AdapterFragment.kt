package endlessmusicplayer.ui.interfaces

import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Interface for fragments that use an adapter, whether its for ListView or GridView
 * Created by gabriel on 3/16/16.
 */

interface AdapterFragment {

    abstract fun setData(results : RealmResults<RealmObject>)
}
