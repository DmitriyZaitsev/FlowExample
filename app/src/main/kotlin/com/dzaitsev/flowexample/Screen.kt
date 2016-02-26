package com.dzaitsev.flowexample

import org.parceler.Parcel

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~

 * @author Dmitriy Zaitsev
 * *
 * @since 2015-Nov-26, 23:45
 */
@Parcel
@Layout(R.layout.screen_view)
data class Screen(val title: String) {
  constructor() : this("")
}

