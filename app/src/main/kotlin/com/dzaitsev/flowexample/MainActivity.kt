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
import android.view.animation.AnimationUtils
import android.widget.ViewAnimator
import flow.Flow
import flow.History
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, Flow.Dispatcher {
  private val TAG = "MainActivity"
  private val mSceneRoot: ViewAnimator by lazy { findViewById(R.id.rootView) as ViewAnimator }
  private val mDrawer: DrawerLayout by lazy { findViewById(R.id.drawer_layout) as DrawerLayout }
  private val mFlow: Flow by lazy { Flow(History.single(Screen("Main"))) }
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

    val destinationScreen = destination.top<Screen>()
    val layout = destinationScreen.javaClass.getAnnotation(Layout::class.java)
    val destinationView = LayoutInflater.from(this).inflate(layout.value, mSceneRoot, false)
    destination.currentViewState()?.restore(destinationView)

    setupTransitionAnimation(traversal.direction)

    val originView = mSceneRoot.currentView
    mSceneRoot.addView(destinationView)
    mSceneRoot.showNext()
    if (originView != null) {
      traversal.origin.currentViewState()?.save(originView)
      mSceneRoot.removeView(originView)
    }
    updateAppBar(destinationScreen.title, destination.size() == 1)
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
      R.id.nav_import -> Screen("Import")
      R.id.nav_gallery -> Screen("Gallery")
      R.id.nav_slideshow -> Screen("Slideshow")
      R.id.nav_tools -> Screen("Tools")
      else -> Screen("Main")
    })

    return true
  }

  private fun updateAppBar(title: String, isHome: Boolean) {
    supportActionBar.title = title
    mToggle.isDrawerIndicatorEnabled = isHome;
    mToggle.toolbarNavigationClickListener = when {
      isHome -> null
      else -> View.OnClickListener {
        mFlow.setHistory(History.single(Screen("Main")), Flow.Direction.BACKWARD)
      }
    }
    mToggle.syncState()
  }

  private fun setupTransitionAnimation(direction: Flow.Direction) {
    // use 'if' instead of 'when' because of https://youtrack.jetbrains.com/issue/KT-10341
    if (direction == Flow.Direction.FORWARD) {
      mSceneRoot.inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
      mSceneRoot.outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
    } else if (direction == Flow.Direction.BACKWARD) {
      mSceneRoot.inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
      mSceneRoot.outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
    } else {
      mSceneRoot.inAnimation = null
      mSceneRoot.outAnimation = null
    }
  }
}
