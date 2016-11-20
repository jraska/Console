package com.jraska.console.sample;

import android.os.AsyncTask;

import com.jraska.console.Console;

class MassTextAsync extends AsyncTask<Long, Void, Void> {
  @Override
  protected Void doInBackground(Long... params) {
    long length = params[0];
    for (int i = 0; i < length; i++) {
      Console.writeLine("Line: "+i);
    }
    return null;
  }
}
