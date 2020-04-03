package com.jraska.console

import androidx.test.rule.ActivityTestRule
import com.jraska.console.sample.ConsoleActivity
import org.junit.Rule
import org.junit.Test

class ConsolePerformanceTest {
  @get:Rule
  var testRule = ActivityTestRule(ConsoleActivity::class.java)

  @Test(timeout = 500) // generous to update Console few times, however it fails when scheduling is improper
  fun testHighLoad() {
    for (x in 1..1000) {
      Console.writeLine("sampleText")
    }
  }
}
