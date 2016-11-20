package com.jraska.console;

import android.text.SpannableStringBuilder;
import android.widget.TextView;

import java.lang.ref.WeakReference;

class ConsoleBuffer {
  //region Constants

  static int MAX_BUFFER_SIZE = 16_000;

  //endregion

  //region Fields

  private final Object lock = new Object();

  private final SpannableStringBuilder buffer = new SpannableStringBuilder();
  private int maxBufferSize = MAX_BUFFER_SIZE;

  private long interval = 1000; // one second.
  private long lastCall = -1;
  private boolean waiting = false, bufferText = false;
  private Runnable runnable;

  //endregion

  //region Properties

  /**
   * @return true if buffer content changed
   */
  boolean setSize(int maxBufferSize) {
    synchronized (lock) {
      boolean bufferChange = maxBufferSize < buffer.length();
      this.maxBufferSize = maxBufferSize;

      ensureSize();
      return bufferChange;
    }
  }

  //endregion

  //region Methods

  void shouldBufferLogs(boolean enabled) {
    bufferText = enabled;
  }

  void setBufferDuration(long bufferDuration) {
    if(interval <= 0) {
      throw new IllegalArgumentException("Duration must be a positive number");
    }
    interval = bufferDuration;
  }

  ConsoleBuffer append(Object o) {
    return append(o == null ? "null" : o.toString());
  }

  ConsoleBuffer append(CharSequence charSequence) {
    synchronized (lock) {
      buffer.append(charSequence);
      ensureSize();
    }
    return this;
  }

  ConsoleBuffer clear() {
    synchronized (lock) {
      buffer.clear();
    }
    return this;
  }

  ConsoleBuffer printTo(TextView textView) {
    if(bufferText) {
      bufferedLogging(textView);
    } else {
      // Use the default synchronized implementation.
      synchronized(lock) {
        textView.setText(buffer);
      }
    }

    return this;
  }

  private void bufferedLogging(final TextView textView) {
    // delay the logging
    long current = System.currentTimeMillis();
    if(lastCall != -1 && ((current - lastCall) <= interval)) {
      if(!waiting) {
        // run this after n seconds.
        waiting = true;
        final WeakReference<TextView> weakTextview = new WeakReference<>(textView);
        runnable = new Runnable() {
          @Override
          public void run() {
            synchronized(lock) {
              if (weakTextview.get() != null) {
                // Text view still exists.
                weakTextview.get().setText(buffer);
                runnable = null;
                waiting = false;
              }
            }

          }
        };
        Console.getUIThreadHandler().postDelayed(runnable, interval);
      }
    } else {
      // Cancel any existing setText runnables if any.
      if(runnable != null) {
        Console.getUIThreadHandler().removeCallbacks(runnable);
      }
      synchronized (lock) {
        textView.setText(buffer);
      }
    }
    lastCall = current;
  }

  private void ensureSize() {
    if (buffer.length() > maxBufferSize) {
      int requiredReplacedCharacters = buffer.length() - maxBufferSize;
      buffer.replace(0, requiredReplacedCharacters, "");
    }
  }

  //endregion
}
