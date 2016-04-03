package com.dzaitsev.flowexample

import android.content.Context
import android.view.LayoutInflater
import flow.Direction
import flow.Flow
import flow.KeyChanger
import flow.State
import flow.TraversalCallback

class ScreenKeyChanger(private val mainActivity: MainActivity) : KeyChanger() {
  override fun changeKey(outgoingState: State?, incomingState: State, direction: Direction,
                         incomingContexts: MutableMap<Any, Context>, callback: TraversalCallback) {
    val originView = mainActivity.containerView.currentView
    originView?.let { outgoingState?.save(originView) }

    val screenKey = incomingState.getKey<Screen>()
      val layout = screenKey.javaClass.getAnnotation(Layout::class.java)
      val screenContext = incomingContexts[screenKey]
      val destinationView = LayoutInflater.from(screenContext).inflate(layout.value, mainActivity.containerView, false)
      incomingState.restore(destinationView)

    with(mainActivity.containerView) {
      addView(destinationView)
      changeTransitionAnimation(direction)
      showNext()
      removeView(originView)
    }

    callback.onTraversalCompleted()

    mainActivity.updateAppBar(screenKey.title, Flow.get(mainActivity).history.size() == 1)
  }
}