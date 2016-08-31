package com.jraska.console;

import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ConsoleBufferTest {
  static final int ATTEMPTS_COUNT = 1000;

  @Test
  public void failsOnWrongBufferConcurrency() throws Exception {
    final ConsoleBuffer buffer = new ConsoleBuffer();
    buffer.setSize(10);

    TextView testTextView = new TextView(RuntimeEnvironment.application);

    CountDownLatch finishLatch = new CountDownLatch(1);
    AppendRunnable appendRunnable = new AppendRunnable(buffer, finishLatch);
    new Thread(appendRunnable).start();

    for (int i = 0; i < ATTEMPTS_COUNT; i++) {
      buffer.printTo(testTextView);
    }

    boolean await = finishLatch.await(1, TimeUnit.SECONDS);
    assertThat(await).isTrue();
  }

  static class AppendRunnable implements Runnable {
    private final ConsoleBuffer buffer;
    private final CountDownLatch latch;

    AppendRunnable(ConsoleBuffer buffer, CountDownLatch latch) {
      this.buffer = buffer;
      this.latch = latch;
    }

    @Override public void run() {
      for (int i = 0; i < ATTEMPTS_COUNT; i++) {
        buffer.append("textToAppend");
        latch.countDown();
      }
    }
  }
}