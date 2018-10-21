package com.jraska.console.timber

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log.ASSERT
import android.util.Log.DEBUG
import android.util.Log.ERROR
import android.util.Log.INFO
import android.util.Log.VERBOSE
import android.util.Log.WARN
import com.jraska.console.Console
import timber.log.Timber
import java.text.DateFormat
import java.util.Arrays
import java.util.Date
import java.util.regex.Pattern

class ConsoleTree private constructor(
  private val minPriority: Int,
  private val priorityColorMapping: IntArray,
  private val timeFormat: DateFormat?
) : Timber.Tree() {

  init {
    if (priorityColorMapping.size != REQUIRED_COLORS_LENGTH) {
      throw IllegalArgumentException("Colors array must have length=$REQUIRED_COLORS_LENGTH")
    }
  }

  override fun isLoggable(tag: String?, priority: Int): Boolean {
    return priority >= minPriority
  }

  override fun log(priority: Int, tagParam: String?, message: String, t: Throwable?) {
    var tag = tagParam
    if (tag == null) {
      tag = createTag()
    }

    val consoleMessage = StringBuilder(toPriorityString(priority))
    if (timeFormat != null) {
      val timeFormatted = timeFormat.format(Date())
      consoleMessage.append("/").append(timeFormatted)
    }
    if (tag != null) {
      consoleMessage.append("/").append(tag)
    }

    consoleMessage.append(": ").append(message)

    writeToConsole(priority, consoleMessage.toString())
  }

  private fun writeToConsole(priority: Int, consoleMessage: String) {
    Console.writeLine(createSpannable(priority, consoleMessage))
  }

  private fun createSpannable(priority: Int, consoleMessage: String): SpannableString {
    val spannableString = SpannableString(consoleMessage)
    spannableString.setSpan(ForegroundColorSpan(priorityColorMapping[priority]), 0, consoleMessage.length, 0)
    return spannableString
  }

  private fun createStackElementTag(element: StackTraceElement): String {
    var tag = element.className
    val matcher = ANONYMOUS_CLASS.matcher(tag)
    if (matcher.find()) {
      tag = matcher.replaceAll("")
    }

    return tag.substring(tag.lastIndexOf('.') + 1)
  }

  private fun createTag(): String? {
    val stackTrace = Throwable().stackTrace
    return if (stackTrace.size <= CALL_STACK_INDEX) {
      null
    } else createStackElementTag(stackTrace[CALL_STACK_INDEX])
  }

  private fun toPriorityString(priority: Int): String {
    when (priority) {
      ASSERT -> return "WTF"
      ERROR -> return "E"
      WARN -> return "W"
      INFO -> return "I"
      DEBUG -> return "D"
      VERBOSE -> return "V"

      else -> throw IllegalArgumentException()
    }
  }

  class Builder {
    private var minPriority = VERBOSE
    private val colors = Arrays.copyOf(DEFAULT_COLORS, REQUIRED_COLORS_LENGTH)
    private var timeFormat: DateFormat? = null

    fun minPriority(priority: Int): Builder {
      if (priority < VERBOSE || priority > ASSERT) {
        throw IllegalArgumentException("Priority $priority is not in range <VERBOSE, ASSERT>(<$VERBOSE, $ASSERT>)")
      }

      minPriority = priority
      return this
    }

    fun verboseColor(color: Int): Builder {
      colors[VERBOSE] = color
      return this
    }

    fun debugColor(color: Int): Builder {
      colors[DEBUG] = color
      return this
    }

    fun infoColor(color: Int): Builder {
      colors[INFO] = color
      return this
    }

    fun warnColor(color: Int): Builder {
      colors[WARN] = color
      return this
    }

    fun errorColor(color: Int): Builder {
      colors[ERROR] = color
      return this
    }

    fun assertColor(color: Int): Builder {
      colors[ASSERT] = color
      return this
    }

    fun timeFormat(timeFormat: DateFormat): Builder {
      this.timeFormat = timeFormat
      return this
    }

    fun build(): ConsoleTree {
      return ConsoleTree(minPriority, colors.copyOf(), timeFormat)
    }
  }

  companion object {
    private const val PLACEHOLDER = 0
    private const val COLOR_VERBOSE = 0xff909090.toInt()
    private const val COLOR_DEBUG = 0xffc88b48.toInt()
    private const val COLOR_INFO = 0xffc9c9c9.toInt()
    private const val COLOR_WARN = 0xffa97db6.toInt()
    private const val COLOR_ERROR = 0xffff534e.toInt()
    private const val COLOR_WTF = 0xffff5540.toInt()

    private val DEFAULT_COLORS = intArrayOf(PLACEHOLDER, PLACEHOLDER, COLOR_VERBOSE, COLOR_DEBUG, COLOR_INFO, COLOR_WARN, COLOR_ERROR, COLOR_WTF)
    private val REQUIRED_COLORS_LENGTH = DEFAULT_COLORS.size

    private const val CALL_STACK_INDEX = 6
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

    @JvmStatic
    fun builder(): ConsoleTree.Builder {
      return ConsoleTree.Builder()
    }

    @JvmStatic
    fun create(): ConsoleTree {
      return builder().build()
    }
  }
}
