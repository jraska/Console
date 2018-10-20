package com.jraska.console

import android.widget.OverScroller
import android.widget.ScrollView

/**
 * Utility using reflection to pull up OverScroller and then use it as fling indicator.
 */
internal class FlingProperty private constructor(private val overScroller: OverScroller) {

  val isFlinging: Boolean
    get() = !overScroller.isFinished

  companion object {
    fun create(scrollView: ScrollView): FlingProperty {
      val scrollerField = ScrollView::class.java.getDeclaredField("mScroller")
      scrollerField.isAccessible = true

      val scroller = scrollerField.get(scrollView) as OverScroller
      return FlingProperty(scroller)
    }
  }
}
