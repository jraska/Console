package com.jraska.console.sample;

import android.app.Application;
import com.jraska.console.timber.ConsoleTree;
import timber.log.Timber;

public class ConsoleApp extends Application{
  @Override public void onCreate() {
    super.onCreate();

    Timber.plant(new ConsoleTree());
  }
}
