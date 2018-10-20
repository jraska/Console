package com.jraska.console

import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.widget.TextView

import java.lang.ref.WeakReference
import java.util.ArrayList

internal class ConsoleController {

  val consoles: MutableList<WeakReference<Console>> = ArrayList()
  val buffer = ConsoleBuffer()

  private val uiThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
  private val bufferPrintRunnable = Runnable { runBufferPrint() }

  private val isUIThread: Boolean
    get() = Looper.myLooper() == Looper.getMainLooper()

  fun add(console: Console) {
    consoles.add(WeakReference(console))
  }

  fun writeLine() {
    write(END_LINE)
  }

  fun writeLine(o: Any) {
    buffer.append(o).append(END_LINE)
    scheduleBufferPrint()
  }

  fun write(spannableString: SpannableString) {
    buffer.append(spannableString)
    scheduleBufferPrint()
  }

  fun writeLine(spannableString: SpannableString) {
    buffer.append(spannableString).append(END_LINE)
    scheduleBufferPrint()
  }

  fun write(o: Any) {
    buffer.append(o)
    scheduleBufferPrint()
  }

  fun clear() {
    buffer.clear()
    scheduleBufferPrint()
  }

  fun scheduleBufferPrint() {
    runBufferPrint()
  }

  fun size(): Int {
    return consoles.size
  }

  fun printTo(text: TextView) {
    buffer.printTo(text)
  }

  private fun runBufferPrint() {
    if (!isUIThread) {
      uiThreadHandler.post(bufferPrintRunnable)
      return
    }

    val iterator = consoles.iterator()
    while (iterator.hasNext()) {
      val console = iterator.next().get()
      if (console == null) {
        iterator.remove()
      } else {
        console.printScroll()
      }
    }
  }

  companion object {
    val END_LINE = "\n"
  }
}


