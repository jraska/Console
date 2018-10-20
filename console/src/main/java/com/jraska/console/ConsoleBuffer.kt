package com.jraska.console;

import android.text.SpannableStringBuilder;
import android.widget.TextView;

final class ConsoleBuffer {
  //region Constants

  static int MAX_BUFFER_SIZE = 16_000;

  //endregion

  //region Fields

  private final Object lock = new Object();

  private final SpannableStringBuilder buffer = new SpannableStringBuilder();
  private int maxBufferSize = MAX_BUFFER_SIZE;

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
    buffer.clear();
    return this;
  }

  ConsoleBuffer printTo(TextView textView) {
    synchronized (lock) {
      textView.setText(buffer);
    }

    return this;
  }

  private void ensureSize() {
    if (buffer.length() > maxBufferSize) {
      int requiredReplacedCharacters = buffer.length() - maxBufferSize;
      buffer.replace(0, requiredReplacedCharacters, "");
    }
  }

  //endregion
}
