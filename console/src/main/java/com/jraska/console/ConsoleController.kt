package com.jraska.console

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.SpannableString
import android.widget.TextView

import java.lang.ref.WeakReference
import java.util.ArrayList

internal class ConsoleController {

  val consoles: MutableList<WeakReference<Console>> = ArrayList()
  val buffer = ConsoleBuffer()

  private val printBufferHandler: Handler by lazy { PrintBufferHandler(this) }

  private val isUIThread: Boolean
    get() = Looper.myLooper() == Looper.getMainLooper()

  fun add(console: Console) {
    consoles.add(WeakReference(console))
  }

  fun writeLine() {
    write(END_LINE)
  }

  fun writeLine(o: Any?) {
    buffer.append(o).append(END_LINE)
    scheduleBufferPrint()
  }

  fun write(spannableString: SpannableString?) {
    buffer.append(spannableString)
    scheduleBufferPrint()
  }

  fun writeLine(spannableString: SpannableString?) {
    buffer.append(spannableString).append(END_LINE)
    scheduleBufferPrint()
  }

  fun write(o: Any?) {
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
      if (!printBufferHandler.hasMessages(PRINT_BUFFER)) {
        printBufferHandler.obtainMessage(PRINT_BUFFER).sendToTarget()
      }
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

  private class PrintBufferHandler(val controller: ConsoleController) : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
      if (msg.what == PRINT_BUFFER) {
        controller.runBufferPrint()
      }
    }
  }

  companion object {
    val END_LINE = "\n"
    const val PRINT_BUFFER = 653276
  }
}
