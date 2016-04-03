package com.dzaitsev.flowexample

import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ViewAnimator
import flow.Direction
import flow.Flow
import flow.KeyDispatcher
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  internal val containerView: ViewAnimator by lazy { findViewById(R.id.rootView) as ViewAnimator }
  private val mDrawer: DrawerLayout by lazy { findViewById(R.id.drawer_layout) as DrawerLayout }
  private var mToggle: ActionBarDrawerToggle by Delegates.notNull<ActionBarDrawerToggle>()

  /*
   * Install Flow into your Activity
   */
  override fun attachBaseContext(newBase: Context?) {
    val flowContext = Flow.configure(newBase, this)
        .dispatcher(KeyDispatcher.configure(this, ScreenKeyChanger(this)).build())
        .defaultKey(Screen("Main"))
        .keyParceler(ScreenParceler())
        .install()
    super.attachBaseContext(flowContext)
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

  fun updateAppBar(title: String, isHome: Boolean) {
    supportActionBar?.title = title
    with(mToggle) {
      isDrawerIndicatorEnabled = isHome;
      toolbarNavigationClickListener = when {
        isHome -> null
        else -> View.OnClickListener {
          Flow.get(this@MainActivity).replaceHistory(Screen("Main"), Direction.BACKWARD)
        }
      }
      syncState()
    }
  }
}
