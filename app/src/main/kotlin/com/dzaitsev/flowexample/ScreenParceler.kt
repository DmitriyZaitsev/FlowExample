package com.dzaitsev.flowexample

import android.os.Parcelable
import flow.KeyParceler
import org.parceler.Parcels

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 * @author Dmitriy Zaitsev
 * @since 2016-Feb-26, 02:29
 */
class ScreenParceler : KeyParceler {
  override fun toParcelable(key: Any?): Parcelable? = Parcels.wrap(key)

  override fun toKey(parcelable: Parcelable?): Any? = Parcels.unwrap(parcelable)
}