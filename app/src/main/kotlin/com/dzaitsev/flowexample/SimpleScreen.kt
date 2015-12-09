package com.dzaitsev.flowexample

import android.view.View

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~

 * @author Dmitriy Zaitsev
 * *
 * @since 2015-Nov-26, 23:45
 */
data class SimpleScreen(override val title: String) : Screen {
  override val viewResId = R.layout.screen_view

  override fun onViewCreated(view: View) {
    // do smth with view
  }
}

