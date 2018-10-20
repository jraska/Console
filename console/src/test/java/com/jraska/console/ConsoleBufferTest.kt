package com.jraska.console

import android.widget.TextView
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [26])
class ConsoleBufferTest {

  @Test
  fun failsOnWrongBufferConcurrency() {
    val buffer = ConsoleBuffer()
    buffer.setSize(10)

    val testTextView = TextView(RuntimeEnvironment.application)

    val finishLatch = CountDownLatch(1)
    val appendRunnable = AppendRunnable(buffer, finishLatch)
    Thread(appendRunnable).start()

    for (i in 0..ATTEMPTS_COUNT) {
      buffer.printTo(testTextView)
    }

    val await = finishLatch.await(1, TimeUnit.SECONDS)
    assertThat(await).isTrue()
  }

  internal class AppendRunnable(val buffer: ConsoleBuffer, val latch: CountDownLatch) : Runnable {

    override fun run() {
      for (i in 0..ATTEMPTS_COUNT) {
        buffer.append("textToAppend")
      }

      latch.countDown()
    }
  }

  companion object {
    private const val ATTEMPTS_COUNT = 1000
  }
}
