package com.jraska.console;

import android.view.MotionEvent;
import android.view.View;

final class UserTouchingListener implements View.OnTouchListener {
  private boolean _userHolds;

  boolean isUserTouching() {
    return _userHolds;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_MOVE:
        _userHolds = true;
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        _userHolds = false;
    }

    return false;
  }
}
