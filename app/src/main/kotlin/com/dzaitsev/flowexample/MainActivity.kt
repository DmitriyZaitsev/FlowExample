package com.dzaitsev.flowexample

import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ViewAnimator
import flow.Direction
import flow.Dispatcher
import flow.Flow
import flow.History
import flow.Traversal
import flow.TraversalCallback
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, Dispatcher {
  private val mContainerView: ViewAnimator by lazy { findViewById(R.id.rootView) as ViewAnimator }
  private val mDrawer: DrawerLayout by lazy { findViewById(R.id.drawer_layout) as DrawerLayout }
  private var mToggle: ActionBarDrawerToggle by Delegates.notNull<ActionBarDrawerToggle>()

  /*
   * Install Flow into your Activity
   */
  override fun attachBaseContext(newBase: Context?) {
    val flowContext = Flow.configure(newBase, this)
        .dispatcher(this)
        .defaultKey(Screen("Main"))
        .install()
    super.attachBaseContext(flowContext)
  }

  /**
   * Called when the history is about to change.  Note that Flow does not consider the
   * Traversal to be finished, and will not actually update the history, until the callback is
   * triggered. Traversals cannot be canceled.
   *
   * @param callback Must be called to indicate completion of the traversal.
   */
  override fun dispatch(traversal: Traversal, callback: TraversalCallback) {
    val originView = mContainerView.currentView
    originView?.let {
      val originScreen = traversal.origin?.top<Screen>()
      traversal.getState(originScreen).save(originView)
    }

    val destination = traversal.destination
    val destinationScreen = destination.top<Screen>()
    val layout = destinationScreen.javaClass.getAnnotation(Layout::class.java)
    val flowContext = traversal.createContext(destinationScreen, this)
    val destinationView = LayoutInflater.from(flowContext).inflate(layout.value, mContainerView, false)
    traversal.getState(destinationScreen).restore(destinationView)

    with(mContainerView) {
      addView(destinationView)
      changeTransitionAnimation(traversal.direction)
      showNext()
      removeView(originView)
    }
    updateAppBar(destinationScreen.title, destination.size() == 1)
    callback.onTraversalCompleted()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar = findViewById(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    mToggle = ActionBarDrawerToggle(this, mDrawer, toolbar, 0, 0)
    mDrawer.setDrawerListener(mToggle)

    (findViewById(R.id.nav_view) as NavigationView).setNavigationItemSelectedListener(this)
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
    if (!Flow.get(this).goBack()) {
      super.onBackPressed()
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    mDrawer.closeDrawer(GravityCompat.START)

    Flow.get(this).set(when (item.itemId) {
      R.id.nav_import -> Screen("Import")
      R.id.nav_gallery -> Screen("Gallery")
      R.id.nav_slideshow -> Screen("Slideshow")
      R.id.nav_tools -> Screen("Tools")
      else -> Screen("Main")
    })

    return true
  }

  private fun updateAppBar(title: String, isHome: Boolean) {
    supportActionBar?.title = title
    with(mToggle) {
      isDrawerIndicatorEnabled = isHome;
      toolbarNavigationClickListener = when {
        isHome -> null
        else -> View.OnClickListener {
          Flow.get(this@MainActivity).setHistory(History.single(Screen("Main")), Direction.BACKWARD)
        }
      }
      syncState()
    }
  }

  private fun ViewAnimator.changeTransitionAnimation(direction: Direction) {
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
}
