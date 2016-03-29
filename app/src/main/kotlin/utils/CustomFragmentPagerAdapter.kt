package utils

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 * Implementation of [android.support.v4.view.PagerAdapter] that
 * represents each page as a [Fragment] that is persistently
 * kept in the fragment manager as long as the user can return to the page.

 *
 * This version of the pager is best for use when there are a handful of
 * typically more static fragments to be paged through, such as a set of tabs.
 * The fragment of each page the user visits will be kept in memory, though its
 * view hierarchy may be destroyed when not visible.  This can result in using
 * a significant amount of memory since fragment instances can hold on to an
 * arbitrary amount of state.  For larger sets of pages, consider
 * FragmentStatePagerAdapter.

 *
 * When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.

 *
 * Subclasses only need to implement [.getItem]
 * and [.getCount] to have a working adapter.

 *
 * Here is an example implementation of a pager containing fragments of
 * lists:

 * {@sample development/samples/Support4Demos/src/com/example/android/supportv4/app/FragmentPagerSupport.java
 * *      complete}

 *
 * The `R.layout.fragment_pager` resource of the top-level fragment is:

 * {@sample development/samples/Support4Demos/res/layout/fragment_pager.xml
 * *      complete}

 *
 * The `R.layout.fragment_pager_list` resource containing each
 * individual fragment's layout is:

 * {@sample development/samples/Support4Demos/res/layout/fragment_pager_list.xml
 * *      complete}
 */
abstract open class CustomFragmentPagerAdapter(private val mFragmentManager: FragmentManager) : PagerAdapter() {
    private var mCurTransaction: FragmentTransaction? = null
    private var mCurrentPrimaryItem: Fragment? = null
    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment

    /**
     * @return may return null if the fragment has not been instantiated yet for that position - this depends on if the fragment has been viewed
     * * yet OR is a sibling covered by [android.support.v4.view.ViewPager.setOffscreenPageLimit]. Can use this to call methods on
     * * the current positions fragment.
     */
    abstract fun getFragmentForPosition(position: Int): Fragment?

    override fun startUpdate(container: ViewGroup) {
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        val itemId = getItemId(position)
        // Do we already have this fragment?
        val name = makeFragmentName(container.id, itemId)
        var fragment: Fragment? = mFragmentManager.findFragmentByTag(name)
        if (fragment != null) {
            if (DEBUG) Log.v(TAG, "Attaching item #$itemId: f=$fragment")
            mCurTransaction!!.attach(fragment)
        } else {
            fragment = getItem(position)
            if (DEBUG) Log.v(TAG, "Adding item #$itemId: f=$fragment")
            mCurTransaction!!.add(container.id, fragment,
                    makeFragmentName(container.id, itemId))
        }
        if (fragment !== mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false)
            fragment.userVisibleHint = false
        }
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        if (DEBUG)
            Log.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + `object`
                    + " v=" + (`object` as Fragment).view)
        mCurTransaction!!.detach(`object` as Fragment)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment
        if (fragment !== mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem!!.setMenuVisibility(false)
                mCurrentPrimaryItem!!.userVisibleHint = false
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true)
                fragment.userVisibleHint = true
            }
            mCurrentPrimaryItem = fragment
        }
    }

    override fun finishUpdate(container: ViewGroup) {
        if (mCurTransaction != null) {
            mCurTransaction!!.commitAllowingStateLoss()
            mCurTransaction = null
            mFragmentManager.executePendingTransactions()
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
    }

    /**
     * Return a unique identifier for the item at the given position.

     *
     * The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.

     * @param position Position within this adapter
     * *
     * @return Unique identifier for the item at position
     */
    fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {
        private val TAG = "FragmentPagerAdapter"
        private val DEBUG = true
        fun makeFragmentName(viewId: Int, id: Long): String {
            return "android:switcher:$viewId:$id"
        }
    }

}
