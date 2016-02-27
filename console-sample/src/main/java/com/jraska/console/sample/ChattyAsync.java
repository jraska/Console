package com.jraska.console.sample;

import android.os.AsyncTask;
import com.jraska.console.Console;
import timber.log.Timber;

public class ChattyAsync extends AsyncTask<Long, Void, Void> {
  @Override protected Void doInBackground(Long... params) {
    long time = params[0];

    Console.writeLine("Async just started");
    sleep(time);
    Timber.d("Message from async after %s ms", time);
    sleep(time);
    Timber.w("Note done yet, %s ms left", time);
    sleep(time);
    Timber.i("Finally shutting up");

    return null;
  }

  private void sleep(long time) {
    try {
      Thread.sleep(time);
    }
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
