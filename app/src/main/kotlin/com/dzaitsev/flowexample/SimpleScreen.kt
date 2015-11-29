package com.dzaitsev.flowexample

import android.graphics.Color
import android.view.View
import android.widget.TextView
import java.util.Random

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~

 * @author Dmitriy Zaitsev
 * *
 * @since 2015-Nov-26, 23:45
 */
@Layout(R.layout.view_screen)
class SimpleScreen(override val title: String) : Screen {
  override fun onViewCreated(view: View) {
    val random = Random(System.currentTimeMillis())
    view.setBackgroundColor(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
    (view as TextView).text = title
  }
}

