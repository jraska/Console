package com.jraska.console;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.ref.WeakReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ConsoleTest {
  //region Fields

  private Console console;

  //endregion

  //region Setup methods

  @Before
  public void setUp() {
    console = new Console(getAppContext());
  }

  @After
  public void tearDown() {
    Console.buffer.setSize(ConsoleBuffer.MAX_BUFFER_SIZE);
    Console.clear();
    Console.consoles.clear();
  }

  //endregion

  //region Test Methods

  @Test
  public void testWrite() throws Exception {
    String testText = "asd5a6das7";
    Console.write(testText);

    assertThat(console.getConsoleText()).contains(testText);
  }

  @Test
  public void testWriteLine() throws Exception {
    String testText = "657ad52jh";
    Console.writeLine(testText);

    assertThat(console.getConsoleText()).contains(testText);
    assertThat(console.getConsoleText()).endsWith(Console.END_LINE);
  }

  @Test
  public void testWriteSpannable() throws Exception {
    SpannableString spannableString = new SpannableString("123456789");
    spannableString.setSpan(new ForegroundColorSpan(Color.RED), 5, 8, 0);

    Console.write(spannableString);

    assertThat(console.getConsoleText()).isEqualTo(spannableString.toString());
  }

  @Test
  public void testWriteLineSpannable() throws Exception {
    SpannableString spannableString = new SpannableString("123456789");
    spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, 0);

    Console.writeLine(spannableString);

    assertThat(console.getConsoleText()).contains(spannableString);
    assertThat(console.getConsoleText()).endsWith(Console.END_LINE);
  }

  @Test
  public void testClear() throws Exception {
    Console.write(new SpannableString("123456789"));
    Console.writeLine("as6ad77asd8");

    Console.clear();

    assertThat(console.getConsoleText()).isEqualTo("");
  }

  @Test
  public void whenTextLongerThenBufferSize_printedTextIsShortened() throws Exception {
    Console.buffer.setSize(5);

    Console.write("123456789");

    assertThat(console.getConsoleText()).isEqualTo("56789");

    for (int i = 0; i < 5; i++) {
      Console.writeLine("");
    }

    assertThat(console.getConsoleText()).isEqualTo("\n\n\n\n\n");
  }

  @Test
  public void whenBufferSizeChanges_textIsShortened() throws Exception {
    Console.write("123456789");

    Console.buffer.setSize(6);
    Console.scheduleBufferPrint();

    assertThat(console.getConsoleText()).isEqualTo("456789");
  }

  @Test
  public void whenBufferSizeChanges_thenSpannableShortened() throws Exception {
    SpannableString spannableString = new SpannableString("123456789");
    spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 3, 6, 0);

    Console.write(spannableString);

    Console.buffer.setSize(5);
    Console.scheduleBufferPrint();

    assertThat(console.getConsoleText()).isEqualTo("56789");
  }

  @Test
  public void whenWrittenMultipleTimes_thenScrollDownScheduledOnlyOnce() throws Exception {
    Console consoleSpy = Mockito.spy(console);
    Console.consoles.set(0, new WeakReference<>(consoleSpy));
    consoleSpy.measure(0, 0); // simulate next frame

    Console.write("someText");
    Console.write("line");

    verify(consoleSpy).post(isA(Runnable.class));
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
    console.addView(new View(getAppContext()));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeViewAt() throws Exception {
    console.removeViewAt(0);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeView() throws Exception {
    console.removeView(console.getChildAt(0));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeAllViews() throws Exception {
    console.removeAllViews();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeAllViewsInLayout() throws Exception {
    console.removeAllViewsInLayout();
  }

  //endregion

  //endregion

  //region Properties

  public Context getAppContext() {
    return RuntimeEnvironment.application;
  }

  //endregion
}
