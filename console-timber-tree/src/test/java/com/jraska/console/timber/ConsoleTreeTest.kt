package com.jraska.console.timber

import com.jraska.console.BuildConfig
import com.jraska.console.Console
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.annotation.Config
import timber.log.Timber
import java.text.DateFormat
import java.text.FieldPosition
import java.text.ParsePosition
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ConsoleTreeTest {
  val console = Console(application)

  @After
  fun after() {
    Console.clear()
    Timber.uprootAll()
  }

  @Test
  fun extractsTagProperly() {
    Timber.plant(ConsoleTree.create())

    Timber.w("Hello")

    assertThat(console.text).isEqualTo("W/${javaClass.simpleName}: Hello\n")
  }

  @Test
  fun addsDateFormatProperly() {
    val consoleTree = ConsoleTree.builder()
      .timeFormat(AlwaysSameTimeFormat("11:22:33"))
      .build()

    Timber.plant(consoleTree)
    Timber.d("Hello")

    assertThat(console.text).isEqualTo("D/11:22:33/${javaClass.simpleName}: Hello\n")
  }

  class AlwaysSameTimeFormat(val time: String) : DateFormat() {
    override fun parse(source: String, pos: ParsePosition): Date {
      throw NotImplementedError()
    }

    override fun format(date: Date, toAppendTo: StringBuffer, fieldPosition: FieldPosition): StringBuffer {
      return toAppendTo.append(time)
    }
  }
}
