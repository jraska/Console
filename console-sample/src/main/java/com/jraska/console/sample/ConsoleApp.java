package com.jraska.console.sample;

import android.app.Application;
import android.util.Log;
import com.jraska.console.Console;
import com.jraska.console.timber.ConsoleTree;
import timber.log.Timber;

public class ConsoleApp extends Application {
  @Override public void onCreate() {
    super.onCreate();

    ConsoleTree.Builder consoleBuilder = new ConsoleTree.Builder();
    consoleBuilder.minPriority(Log.VERBOSE)
        .verboseColor(0xff909090)
        .debugColor(0xffc88b48)
        .infoColor(0xffc9c9c9)
        .warnColor(0xffa97db6)
        .errorColor(0xffff534e)
        .assertColor(0xffff5540);

    Timber.plant(consoleBuilder.build());

    Timber.i("Test message before attach of any view");
  }
}