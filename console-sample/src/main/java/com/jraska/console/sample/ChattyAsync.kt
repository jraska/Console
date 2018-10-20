package com.jraska.console.sample

import android.os.AsyncTask
import com.jraska.console.Console
import timber.log.Timber
import java.lang.Thread.sleep

class ChattyAsync(val sleepTime: Long) : AsyncTask<Any, Void, Void>() {
  override fun doInBackground(vararg params: Any?): Void? {

    Console.writeLine("Async just started")
    sleep(sleepTime)
    Timber.d("Message from async after %s ms", sleepTime)
    sleep(sleepTime)
    Timber.w("Note done yet, %s ms left", sleepTime)
    sleep(sleepTime)
    Timber.i("Finally shutting up")

    return null
  }
}
