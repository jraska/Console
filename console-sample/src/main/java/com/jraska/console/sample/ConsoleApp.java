package com.jraska.console.sample;

import android.app.Application;
import android.util.Log;

import com.jraska.console.timber.ConsoleTree;

import java.text.SimpleDateFormat;
import java.util.Locale;

import timber.log.Timber;

public class ConsoleApp extends Application {
  @Override public void onCreate() {
    super.onCreate();

    ConsoleTree consoleTree = ConsoleTree.builder()
      .minPriority(Log.VERBOSE)
      .verboseColor(0xff909090)
      .debugColor(0xffc88b48)
      .infoColor(0xffc9c9c9)
      .warnColor(0xffa97db6)
      .errorColor(0xffff534e)
      .assertColor(0xffff5540)
      .timeFormat(new SimpleDateFormat("HH:mm:ss.SSS", Locale.US))
      .build();

    Timber.plant(consoleTree);

    Timber.i("Test message before attach of any view");
  }
}
