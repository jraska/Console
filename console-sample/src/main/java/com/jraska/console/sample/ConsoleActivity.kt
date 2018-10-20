package com.jraska.console.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jraska.console.Console
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.toolbar
import timber.log.Timber
import java.text.DateFormat
import java.util.Date

class ConsoleActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setSupportActionBar(toolbar)
    fab.setOnClickListener { _ -> addSampleRecord() }

    Console.writeLine("Hello Console!")
    Console.writeLine()

    Timber.d("Debugging: onCreate(%s)", savedInstanceState)
    Timber.w("Warning makes me nervous...")
    Timber.e("Some horrible ERROR!")
    Timber.wtf("WTF*!?!")
  }

  override fun onStart() {
    super.onStart()

    Timber.i("Important information")
  }

  override fun onResume() {
    super.onResume()

    Timber.v("I'm so talkative")
    Timber.v("Blah blah blah...")
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId

    if (id == R.id.action_console_clear) {
      Console.clear()
      return true
    }

    if (id == R.id.action_console_async) {
      onAsyncClicked()
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  private fun addSampleRecord() {
    Console.writeLine("Sample console record at " + currentTime())
  }

  private fun currentTime(): String {
    return DATE_FORMAT.format(Date())
  }

  internal fun onAsyncClicked() {
    val chattyAsync = ChattyAsync()
    chattyAsync.execute(1000L)
  }

  companion object {
    val DATE_FORMAT = DateFormat.getTimeInstance(DateFormat.MEDIUM)
  }
}
