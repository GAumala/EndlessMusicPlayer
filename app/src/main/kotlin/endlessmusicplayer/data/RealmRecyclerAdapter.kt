package endlessmusicplayer.data

import android.content.Context
import android.support.v7.widget.RecyclerView
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Created by gabriel on 4/3/16.
 */

abstract class RealmRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected val mContext : Context
    var realmResults : RealmResults<RealmObject>?

    constructor (ctx : Context, results : RealmResults<RealmObject>) : super (){
        mContext = ctx
        realmResults = results
    }


    override fun getItemCount(): Int {
        return realmResults?.size ?: 0
    }

}
