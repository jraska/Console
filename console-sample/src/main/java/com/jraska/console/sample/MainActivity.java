package com.jraska.console.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.console.Console;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

  //region Constants

  public static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);

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
  }

  //endregion

  //region Methods

  @OnClick(R.id.fab) void addConsoleRecord() {
    Console.writeLine(DATE_FORMAT.format(new Date()) + " Console record");
  }

  //endregion
}
