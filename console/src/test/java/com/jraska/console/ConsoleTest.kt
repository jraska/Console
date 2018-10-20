package com.jraska.console

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.isA
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.annotation.Config
import java.lang.ref.WeakReference

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [26])
class ConsoleTest {

  private lateinit var console: Console

  @Before
  fun setUp() {
    console = Console(application)
  }

  @After
  fun tearDown() {
    buffer().setSize(ConsoleBuffer.MAX_BUFFER_SIZE)
    Console.clear()
    Console.controller.consoles.clear()
  }

  @Test
  fun testWrite() {
    val testText = "asd5a6das7"
    Console.write(testText)

    assertThat(console.consoleText).contains(testText)
  }

  @Test
  fun testWriteLine() {
    val testText = "657ad52jh"
    Console.writeLine(testText)

    assertThat(console.consoleText).contains(testText)
    assertThat(console.consoleText).endsWith(ConsoleController.END_LINE)
  }

  @Test
  fun testWriteSpannable() {
    val spannableString = SpannableString("123456789")
    spannableString.setSpan(ForegroundColorSpan(Color.RED), 5, 8, 0)

    Console.write(spannableString)

    assertThat(console.consoleText).isEqualTo(spannableString.toString())
  }

  @Test
  fun testWriteLineSpannable() {
    val spannableString = SpannableString("123456789")
    spannableString.setSpan(ForegroundColorSpan(Color.BLUE), 0, 4, 0)

    Console.writeLine(spannableString)

    assertThat(console.consoleText).contains(spannableString)
    assertThat(console.consoleText).endsWith(ConsoleController.END_LINE)
  }

  @Test
  fun testClear() {
    Console.write(SpannableString("123456789"))
    Console.writeLine("as6ad77asd8")

    Console.clear()

    assertThat(console.consoleText).isEqualTo("")
  }

  @Test
  fun whenTextLongerThenBufferSize_printedTextIsShortened() {
    buffer().setSize(5)

    Console.write("123456789")

    assertThat(console.consoleText).isEqualTo("56789")

    for (i in 0..4) {
      Console.writeLine("")
    }

    assertThat(console.consoleText).isEqualTo("\n\n\n\n\n")
  }

  @Test
  fun whenBufferSizeChanges_textIsShortened() {
    Console.write("123456789")

    buffer().setSize(6)
    Console.controller.scheduleBufferPrint()

    assertThat(console.consoleText).isEqualTo("456789")
  }

  @Test
  fun whenBufferSizeChanges_thenSpannableShortened() {
    val spannableString = SpannableString("123456789")
    spannableString.setSpan(ForegroundColorSpan(Color.BLUE), 3, 6, 0)

    Console.write(spannableString)

    buffer().setSize(5)
    Console.controller.scheduleBufferPrint()

    assertThat(console.consoleText).isEqualTo("56789")
  }

  @Test
  fun whenWrittenMultipleTimes_thenScrollDownScheduledOnlyOnce() {
    val consoleSpy = Mockito.spy(console)
    Console.controller.consoles[0] = WeakReference(consoleSpy)
    consoleSpy.measure(0, 0) // simulate next frame

    Console.write("someText")
    Console.write("line")

    verify(consoleSpy).post(isA(Runnable::class.java))
  }

  @Test
  fun whenNewConsoleViewCreated_thenBufferIsPrinted() {
    Console.write("text")

    val newConsole = Console(application)
    assertThat(newConsole.consoleText).isEqualTo("text")
    assertThat(Console.consoleCount()).isEqualTo(2)
  }

  private fun buffer() = Console.controller.buffer
}
