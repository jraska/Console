package com.jraska.console

import android.view.MotionEvent
import android.view.View

internal class UserTouchingListener : View.OnTouchListener {
  var isUserTouching: Boolean = false
    private set

  override fun onTouch(v: View, event: MotionEvent): Boolean {
    when (event.actionMasked) {
      MotionEvent.ACTION_MOVE -> isUserTouching = true
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isUserTouching = false
    }

    return false
  }
}
