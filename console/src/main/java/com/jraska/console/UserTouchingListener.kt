package com.jraska.console;

import android.view.MotionEvent;
import android.view.View;

final class UserTouchingListener implements View.OnTouchListener {
  private boolean userHolds;

  boolean isUserTouching() {
    return userHolds;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_MOVE:
        userHolds = true;
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        userHolds = false;
    }

    return false;
  }
}
