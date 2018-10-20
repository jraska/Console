package com.jraska.console.sample

import android.app.Application
import android.util.Log
import com.jraska.console.timber.ConsoleTree
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

class ConsoleApp : Application() {
  override fun onCreate() {
    super.onCreate()

    val consoleTree = ConsoleTree.builder()
      .minPriority(Log.VERBOSE)
      .verboseColor(0xff909090.toInt())
      .debugColor(0xffc88b48.toInt())
      .infoColor(0xffc9c9c9.toInt())
      .warnColor(0xffa97db6.toInt())
      .errorColor(0xffff534e.toInt())
      .assertColor(0xffff5540.toInt())
      .timeFormat(SimpleDateFormat("HH:mm:ss.SSS", Locale.US))
      .build()

    Timber.plant(consoleTree)

    Timber.i("Test message before attach of any view")
  }
}
