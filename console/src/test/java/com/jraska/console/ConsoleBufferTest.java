package com.jraska.console;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ConsoleBufferTest {
  static final int ATTEMPTS_COUNT = 1000;
  private ConsoleBuffer buffer;
  private TextView testTextView;

  @Before
  public void setUp() {
    buffer = new ConsoleBuffer();
    buffer.setSize(10);
    testTextView = new TextView(RuntimeEnvironment.application);
    buffer = spy(buffer);
    testTextView = spy(testTextView);
  }

  @Test
  public void failsOnWrongBufferConcurrency() throws Exception {
    CountDownLatch finishLatch = new CountDownLatch(1);
    AppendRunnable appendRunnable = new AppendRunnable(buffer, finishLatch);
    new Thread(appendRunnable).start();

    for (int i = 0; i < ATTEMPTS_COUNT; i++) {
      buffer.printTo(testTextView);
    }

    boolean await = finishLatch.await(1, TimeUnit.SECONDS);
    assertThat(await).isTrue();
  }

  /**
   * Assert that our logs are only called twice within a 3 second window.
   *
   * @throws InterruptedException
   */
  @Test
  public void assertSetTextCalledOncePerSecond() throws InterruptedException {
    buffer.shouldBufferLogs(true);
    assertSetTextCalledAtMost(3, TimeUnit.SECONDS.toMillis(3));
    /*reset(testTextView);
    assertSetTextCalledAtMost(5, TimeUnit.SECONDS.toMillis(5));
    reset(testTextView);
    assertSetTextCalledAtMost(2, TimeUnit.SECONDS.toMillis(2));*/
  }

  @Test
  public void assertSetTextCalledOncePerHalfSecond() throws InterruptedException {
    buffer.shouldBufferLogs(true);
    buffer.setBufferDuration(500);
    assertSetTextCalledAtMost(6, TimeUnit.SECONDS.toMillis(3));
  }

  /**
   * Test that setText is called immediately even if buffering is enabled.
   * @throws InterruptedException
   */
  @Test
  public void assertSetTextCalledImmediately() throws InterruptedException {
    buffer.shouldBufferLogs(true);
    assertSetTextCalledAtMost(1, 0);
  }

  /**
   * Test that the setText method is called multiple times per second if buffering is not set.
   *
   * @throws InterruptedException
   */
  @Test
  public void assertSetTextCalledMultipleTimes() throws InterruptedException {
    buffer.shouldBufferLogs(false);
    assertSetTextCalledAtLeast(50, TimeUnit.SECONDS.toMillis(3));
  }

  private void assertSetTextCalledAtLeast(int numberOfTimes, long durationInMilliseconds) throws InterruptedException {
    assertSetTextCalled(numberOfTimes, durationInMilliseconds, true);
  }

  private void assertSetTextCalledAtMost(int numberOfTimes, long durationInMilliseconds) throws InterruptedException {
    assertSetTextCalled(numberOfTimes, durationInMilliseconds, false);
  }

  private void assertSetTextCalled(int numberOfTimes, long durationInMilliseconds, boolean atLeast) throws InterruptedException {
    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    doNothing().when(testTextView).setText(stringArgumentCaptor.capture());
    CountDownLatch finishLatch = new CountDownLatch(1);
    PrintRunnable printRunnable = new PrintRunnable(buffer, finishLatch, testTextView, durationInMilliseconds);
    new Thread(printRunnable).start();
    finishLatch.await();
    verify(testTextView, atLeast ? atLeast(numberOfTimes) : atMost(numberOfTimes))
            .setText(stringArgumentCaptor.getValue());
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

  static class PrintRunnable implements Runnable {
    private final ConsoleBuffer buffer;
    private final CountDownLatch latch;
    private final long durationInMilliseconds;
    private final TextView testTextView;

    PrintRunnable(ConsoleBuffer buffer, CountDownLatch latch, TextView testTextView, long durationInMilliseconds) {
      this.buffer = buffer;
      this.latch = latch;
      this.testTextView = testTextView;
      this.durationInMilliseconds = durationInMilliseconds;
    }

    @Override
    public void run() {
      long start = System.currentTimeMillis();
      while (true) {
        long current = System.currentTimeMillis();
        long diff = current - (start + durationInMilliseconds);
        buffer.append("x");
        buffer.printTo(testTextView);

        if (diff >= 0) {
          latch.countDown();
          break;
        }
      }
    }
  }
}