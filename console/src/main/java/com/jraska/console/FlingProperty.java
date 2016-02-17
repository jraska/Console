package com.jraska.console;

import android.widget.OverScroller;
import android.widget.ScrollView;

import java.lang.reflect.Field;

/**
 * Utility using reflection to pull up OverScroller and then use it as fling indicator.
 */
class FlingProperty {
  private final OverScroller _overScroller;

  private FlingProperty(OverScroller overScroller) {
    _overScroller = overScroller;
  }

  boolean isFlinging() {
    return !_overScroller.isFinished();
  }

  static FlingProperty create(ScrollView scrollView) {
    try {
      return createUnchecked(scrollView);
    }
    // catching generic exception is se as workaround to reflective exception changes on API changes
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static FlingProperty createUnchecked(ScrollView forScrollView)
      throws NoSuchFieldException, IllegalAccessException {
    Field scrollerField = ScrollView.class.getDeclaredField("mScroller");
    scrollerField.setAccessible(true);
    OverScroller scroller = (OverScroller) scrollerField.get(forScrollView);

    return new FlingProperty(scroller);
  }
}
