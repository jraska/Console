package com.jraska.console;

import android.text.SpannableStringBuilder;
import android.widget.TextView;

final class ConsoleBuffer {
  //region Constants

  static int MAX_BUFFER_SIZE = 16_000;

  //endregion

  //region Fields

  private final Object _lock = new Object();

  private final SpannableStringBuilder _buffer = new SpannableStringBuilder();
  private int _maxBufferSize = MAX_BUFFER_SIZE;

  //endregion

  //region Properties

  /**
   * @return true if buffer content changed
   */
  boolean setSize(int maxBufferSize) {
    synchronized (_lock) {
      boolean bufferChange = maxBufferSize < _buffer.length();
      _maxBufferSize = maxBufferSize;

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
    synchronized (_lock) {
      _buffer.append(charSequence);
      ensureSize();
    }
    return this;
  }

  ConsoleBuffer clear() {
    _buffer.clear();
    return this;
  }

  ConsoleBuffer printTo(TextView textView) {
    textView.setText(_buffer);
    return this;
  }

  private void ensureSize() {
    if (_buffer.length() > _maxBufferSize) {
      int requiredReplacedCharacters = _buffer.length() - _maxBufferSize;
      _buffer.replace(0, requiredReplacedCharacters, "");
    }
  }

  //endregion
}
