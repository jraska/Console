package com.jraska.console

import android.text.SpannableStringBuilder
import android.widget.TextView

internal class ConsoleBuffer {
  private val lock = Any()

  private val buffer = SpannableStringBuilder()
  private var maxBufferSize = MAX_BUFFER_SIZE

  /**
   * @return true if buffer content changed
   */
  fun setSize(maxBufferSize: Int): Boolean {
    synchronized(lock) {
      val bufferChange = maxBufferSize < buffer.length
      this.maxBufferSize = maxBufferSize

      ensureSize()
      return bufferChange
    }
  }

  fun append(o: Any?): ConsoleBuffer {
    return append(o?.toString())
  }

  fun append(charSequence: CharSequence?): ConsoleBuffer {
    val toAppend = charSequence ?: "null"

    synchronized(lock) {
      buffer.append(toAppend)
      ensureSize()
    }
    return this
  }

  fun clear(): ConsoleBuffer {
    buffer.clear()
    return this
  }

  fun printTo(textView: TextView): ConsoleBuffer {
    synchronized(lock) {
      textView.text = buffer
    }

    return this
  }

  private fun ensureSize() {
    if (buffer.length > maxBufferSize) {
      val requiredReplacedCharacters = buffer.length - maxBufferSize
      buffer.replace(0, requiredReplacedCharacters, "")
    }
  }

  companion object {
    const val MAX_BUFFER_SIZE = 16000
  }
}
