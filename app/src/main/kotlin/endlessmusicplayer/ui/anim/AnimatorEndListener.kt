package endlessmusicplayer.ui.anim

import android.animation.Animator
import android.view.animation.Animation

/**
 * class used when it is only needed to implement onAnimationEnd() of AnimationListener
 * Created by gabriel on 3/31/16.
 */

abstract class AnimatorEndListener : Animator.AnimatorListener{
    override fun onAnimationStart(p0: Animator?) {

    }

    override fun onAnimationRepeat(p0: Animator?) {

    }

    override fun onAnimationCancel(p0: Animator?) {

    }

}
