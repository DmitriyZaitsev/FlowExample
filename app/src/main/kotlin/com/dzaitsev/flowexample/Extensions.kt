package com.dzaitsev.flowexample

import android.view.animation.AnimationUtils
import android.widget.ViewAnimator
import flow.Direction

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 * @author Dmitriy Zaitsev
 * @since 2016-Apr-03, 19:36
 */
fun ViewAnimator.changeTransitionAnimation(direction: Direction) {
  when (direction) {
    Direction.FORWARD -> {
      inAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
      outAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_left)
    }
    Direction.BACKWARD -> {
      inAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
      outAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_right)
    }
    else -> {
      inAnimation = null
      outAnimation = null
    }
  }
}