package com.jraska.console;

import android.text.SpannableString;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 26)
public class ConsoleApiTest {
  @Test
  public void changeHereMeansBreakingPublicApi() {
    Console.writeLine();

    Console.writeLine((Object) null);
    Console.writeLine(new Object());
    Console.writeLine((SpannableString) null);
    Console.writeLine(SpannableString.valueOf("hello"));

    Console.write(new Object());
    Console.write((Object) null);
    Console.write((SpannableString) null);
    Console.write(SpannableString.valueOf("hello"));

    Console.clear();

    Console.consoleCount();
  }
}
