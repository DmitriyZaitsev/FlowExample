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

open class BaseScreen(override val title: String) : Screen {

  override fun onViewCreated(view: View) {
    val random = Random(System.currentTimeMillis())
    view.setBackgroundColor(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
    (view as TextView).text = title
  }
}

@Layout(R.layout.view_screen) class MainScreen : BaseScreen("Main")
@Layout(R.layout.view_screen) class ImportScreen : BaseScreen("Import")
@Layout(R.layout.view_screen) class GalleryScreen : BaseScreen("Gallery")
@Layout(R.layout.view_screen) class SlideshowScreen : BaseScreen("Slideshow")
@Layout(R.layout.view_screen) class ToolsScreen : BaseScreen("Tools")

