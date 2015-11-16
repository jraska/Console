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

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

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

    Console.write(currentTime() + " onCreate(");
    Console.write(savedInstanceState);
    Console.writeLine(")");
  }

  @Override
  protected void onStart() {
    super.onStart();

    Console.writeLine(currentTime() + " onStart()");
  }

  @Override protected void onResume() {
    super.onResume();

    Console.writeLine(currentTime() + " onResume()");
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

  //endregion
}
