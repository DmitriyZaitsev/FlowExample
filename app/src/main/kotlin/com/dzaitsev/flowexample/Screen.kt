package com.dzaitsev.flowexample

import android.view.View

interface Screen {
  val title: String
  fun onViewCreated(view: View)
}
