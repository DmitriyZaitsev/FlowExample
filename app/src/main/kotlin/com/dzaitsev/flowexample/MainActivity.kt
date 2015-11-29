package com.dzaitsev.flowexample

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import flow.Flow
import flow.History
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, Flow.Dispatcher {
  private val TAG = "MainActivity"
  private val mRootView: ViewGroup by lazy { findViewById(R.id.rootView) as ViewGroup }
  private val mDrawer: DrawerLayout by lazy { findViewById(R.id.drawer_layout) as DrawerLayout }
  private val mFlow: Flow by lazy { Flow(History.single(MainScreen())) }
  private var mToggle: ActionBarDrawerToggle by Delegates.notNull<ActionBarDrawerToggle>()

  /**
   * Called when the history is about to change.  Note that Flow does not consider the
   * Traversal to be finished, and will not actually update the history, until the callback is
   * triggered. Traversals cannot be canceled.
   *
   * @param callback Must be called to indicate completion of the traversal.
   */
  override fun dispatch(traversal: Flow.Traversal, callback: Flow.TraversalCallback) {
    val destination = traversal.destination
    Log.i(TAG, "Flow.dispatch ${traversal.direction}")
    val newScreen = destination.top<Screen>()
    displayViewFor(newScreen)
    updateAppBar(newScreen, destination)
    callback.onTraversalCompleted()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar = findViewById(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)
    supportActionBar.setHomeButtonEnabled(true)
    supportActionBar.setDisplayHomeAsUpEnabled(true)

    mToggle = ActionBarDrawerToggle(this, mDrawer, toolbar, 0, 0)
    mDrawer.setDrawerListener(mToggle)

    (findViewById(R.id.nav_view) as NavigationView).setNavigationItemSelectedListener(this)

    mFlow.setDispatcher(this)
  }

  override fun onPostResume() {
    super.onPostResume()
    mToggle.syncState()
  }

  override fun onBackPressed() {
    if (mDrawer.isDrawerOpen(GravityCompat.START)) {
      mDrawer.closeDrawer(GravityCompat.START)
      return
    }
    if (!mFlow.goBack()) {
      super.onBackPressed()
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    mDrawer.closeDrawer(GravityCompat.START)

    mFlow.set(when (item.itemId) {
      R.id.nav_import -> ImportScreen()
      R.id.nav_gallery -> GalleryScreen()
      R.id.nav_slideshow -> SlideshowScreen()
      R.id.nav_tools -> ToolsScreen()
      else -> MainScreen()
    })

    return true
  }

  private fun displayViewFor(screen: Screen) {
    val layout = screen.javaClass.getAnnotation(Layout::class.java)
    val oldView: View? = mRootView.getChildAt(0)
    val newView = LayoutInflater.from(this).inflate(layout.value, mRootView, false)
    screen.onViewCreated(newView)

    mRootView.addView(newView, 0)
    if (oldView != null) {
      mRootView.removeView(oldView)
    }
  }

  private fun updateAppBar(screen: Screen, history: History) {
    supportActionBar.title = screen.title
    val isHome = history.size() == 1
    mToggle.isDrawerIndicatorEnabled = isHome;
    mToggle.toolbarNavigationClickListener = when {
      isHome -> null
      else -> View.OnClickListener {
        mFlow.setHistory(History.single(MainScreen()), Flow.Direction.BACKWARD)
      }
    }
    mToggle.syncState()
  }
}
