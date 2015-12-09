package com.dzaitsev.flowexample

import android.view.View

interface Screen {
  val title: String
  val viewResId: Int
  fun onViewCreated(view: View)
}
