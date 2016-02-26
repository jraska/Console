package com.jraska.console;

final class ConsoleBuffer {
  //region Constants

  static int MAX_BUFFER_SIZE = 16_000;

  //endregion

  //region Fields

  private final StringBuffer _buffer;
  private int _maxBufferSize = MAX_BUFFER_SIZE;

  //endregion

  //region Constructors

  ConsoleBuffer(int initSize) {
    _buffer = new StringBuffer(initSize);
  }

  //endregion

  //region Properties

  /**
   * @return true if buffer content changed
   */
  boolean setSize(int maxBufferSize) {
    boolean bufferChange = maxBufferSize < _maxBufferSize;
    _maxBufferSize = maxBufferSize;

    ensureSize();

    return bufferChange;
  }

  //endregion

  //region Methods

  ConsoleBuffer append(Object o) {
    _buffer.append(o);
    ensureSize();
    return this;
  }

  ConsoleBuffer append(CharSequence text) {
    _buffer.append(text);
    ensureSize();
    return this;
  }

  ConsoleBuffer clear() {
    _buffer.setLength(0);
    return this;
  }

  CharSequence getText() {
    return _buffer.toString();
  }

  private void ensureSize() {
    if (_buffer.length() > _maxBufferSize) {
      int requiredReplacedCharacters = _buffer.length() - _maxBufferSize;
      _buffer.replace(0, requiredReplacedCharacters, "");
    }
  }

  //endregion
}
