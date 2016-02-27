package com.jraska.console.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.console.Console;
import timber.log.Timber;

import java.text.DateFormat;
import java.util.Date;

public class ConsoleActivity extends AppCompatActivity {

  //region Constants

  public static final DateFormat DATE_FORMAT
      = DateFormat.getTimeInstance(DateFormat.MEDIUM);

  //endregion

  //region Fields

  @Bind(R.id.toolbar) Toolbar _toolbar;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    setSupportActionBar(_toolbar);

    Console.writeLine("Hello Console!");
    Console.writeLine();

    Timber.d("Debugging: onCreate(%s)", savedInstanceState);
    Timber.w("Warning makes me nervous...");
    Timber.e("Some horrible ERROR!");
    Timber.wtf("WTF*!?!");
  }

  @Override
  protected void onStart() {
    super.onStart();

    Timber.i("Important information");
  }

  @Override protected void onResume() {
    super.onResume();

    Timber.v("I'm so talkative");
    Timber.v("Blah blah blah...");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_console_clear) {
      Console.clear();
      return true;
    }

    if (id == R.id.action_console_async) {
      onAsyncClicked();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  //endregion

  //region Methods

  @OnClick(R.id.fab) void addSampleRecord() {
    Console.writeLine("Sample console record at " + currentTime());
  }

  private String currentTime() {
    return DATE_FORMAT.format(new Date());
  }

  void onAsyncClicked() {
    ChattyAsync chattyAsync = new ChattyAsync();
    chattyAsync.execute(1000L);
  }

  //endregion

  //region Nested classes

  //endregion
}
