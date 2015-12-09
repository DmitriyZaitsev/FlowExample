package com.dzaitsev.flowexample

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import java.util.Random

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~

 * @author Dmitriy Zaitsev
 * *
 * @since 2015-Dec-08, 23:26
 */
class ScreenView : FrameLayout {
  private val mClick: View by lazy { findViewById(R.id.button) }
  private val mText: TextView by lazy { findViewById(R.id.text) as TextView }
  private var mClicks: Int = 0
  private var mColor: Int = 0

  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
    View.inflate(context, R.layout.view_screen_content, this)
    mClick.setOnClickListener { mText.text = (++mClicks).toString() }

    val r = Random(System.currentTimeMillis())
    mColor = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255))
    setBackgroundColor(mColor)
  }

  override fun onSaveInstanceState(): Parcelable {
    val superState = super.onSaveInstanceState()
    val savedState = SavedState(superState)
    savedState.clicks = mClicks
    savedState.color = mColor

    return savedState
  }

  override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
    super.dispatchSaveInstanceState(container)
  }

  override fun onRestoreInstanceState(state: Parcelable) {
    when (state) {
      is SavedState -> {
        super.onRestoreInstanceState(state.superState)
        mClicks = state.clicks
        mColor = state.color
        mText.text = mClicks.toString()
        setBackgroundColor(mColor)
      }
    }
  }

  internal class SavedState : BaseSavedState {
    var clicks: Int = 0
    var color: Int = 0

    constructor(superState: Parcelable) : super(superState)

    private constructor(parcel: Parcel) : super(parcel) {
      clicks = parcel.readInt()
      color = parcel.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
      super.writeToParcel(out, flags)
      out.writeInt(clicks)
      out.writeInt(color)
    }

    companion object {
      val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
        override fun createFromParcel(parcel: Parcel): SavedState {
          return SavedState(parcel)
        }

        override fun newArray(size: Int): Array<SavedState?>? {
          return arrayOfNulls(size)
        }
      }
    }
  }
}
