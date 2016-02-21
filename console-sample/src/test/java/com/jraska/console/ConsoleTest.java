package com.jraska.console;

import android.content.Context;
import android.view.View;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.ref.WeakReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Test is moved here from console library module to enable Robolectric testing
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ConsoleTest {
  //region Fields

  private Console _console;

  //endregion

  //region Setup methods

  @Before
  public void setUp() {
    _console = new Console(getAppContext());
  }

  @After
  public void tearDown() {
    Console.setMaxBufferSize(Console.MAX_BUFFER_SIZE);
    Console.clear();
    Console._consoles.clear();
  }

  //endregion

  //region Test Methods

  @Test
  public void testWrite() throws Exception {
    String testText = "asd5a6das7";
    Console.write(testText);

    assertThat(_console.getConsoleText()).contains(testText);
  }

  @Test
  public void testWriteLine() throws Exception {
    String testText = "657ad52jh";
    Console.writeLine(testText);

    assertThat(_console.getConsoleText()).contains(testText);
    assertThat(_console.getConsoleText()).contains(Console.END_LINE);
  }

  @Test
  public void testClear() throws Exception {
    Console.writeLine("as6ad77asd8");

    Console.clear();

    assertThat(_console.getConsoleText()).isEqualTo("");
  }

  @Test
  public void whenTextLongerThenBufferSize_printedTextIsShortened() throws Exception {
    Console.setMaxBufferSize(5);

    Console.write("123456789");

    assertThat(_console.getConsoleText()).isEqualTo("56789");

    for (int i = 0; i < 5; i++) {
      Console.writeLine("");
    }

    assertThat(_console.getConsoleText()).isEqualTo("\n\n\n\n\n");
  }

  @Test
  public void testWhenBufferSizeChanes_textIsShortened() throws Exception {
    Console.write("123456789");

    Console.setMaxBufferSize(6);

    assertThat(_console.getConsoleText()).isEqualTo("456789");
  }

  @Test
  public void testScrollDownScheduledOnlyOnceOnMultiWrite() throws Exception {
    Console consoleSpy = spy(_console);
    Console._consoles.set(0, new WeakReference<>(consoleSpy));
    consoleSpy.measure(0, 0); // simpulate next frame

    Console.write("someText");
    Console.write("line");

    verify(consoleSpy, times(1)).post(isA(Console.ScrollDownRunnable.class));
  }

  @Test
  public void whenNewConsoleViewCreated_thenBufferIsPrinted() throws Exception {
    Console.write("text");

    Console newConsole = new Console(getAppContext());
    assertThat(newConsole.getConsoleText()).isEqualTo("text");
  }

  //region View methods failures tests

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_addView() throws Exception {
    _console.addView(new View(getAppContext()));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeViewAt() throws Exception {
    _console.removeViewAt(0);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeView() throws Exception {
    _console.removeView(_console.getChildAt(0));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeAllViews() throws Exception {
    _console.removeAllViews();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeAllViewsInLayout() throws Exception {
    _console.removeAllViewsInLayout();
  }

  //endregion

  //endregion

  //region Properties

  public Context getAppContext() {
    return RuntimeEnvironment.application;
  }

  //endregion
}
