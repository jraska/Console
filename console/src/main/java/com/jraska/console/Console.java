package com.jraska.console;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Console like output view, which allows writing via static console methods
 * from anywhere of application.
 * <p/>
 * If you want to see the output, you should use console in any of your layouts,
 * all calls to console static write methods will affect all instantiated consoles.
 * You can also call writes directly to console view.
 */
public class Console extends FrameLayout {
  //region Static

  private static List<WeakReference<Console>> _consoles = new ArrayList<>();

  public static void writeLine(Object o) {
    // iteration from the end to allow in place removing
    for (int consoleIndex = _consoles.size() - 1; consoleIndex >= 0; consoleIndex--) {
      WeakReference<Console> consoleReference = _consoles.get(consoleIndex);
      Console console = consoleReference.get();
      if (console == null) {
        _consoles.remove(consoleIndex);
      } else {
        console.writeLn(o);
      }
    }
  }

  //endregion

  //region Fields

  private TextView _text;
  private ScrollView _scrollView;

  //endregion

  //region Constructors

  public Console(Context context) {
    super(context);
    init(context);
  }

  public Console(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public Console(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public Console(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);
  }

  private void init(Context context) {
    // Store myself as weak reference for static method calls
    _consoles.add(new WeakReference<>(this));

    LayoutInflater.from(context).inflate(R.layout.content_console, this);

    _text = (TextView) findViewById(R.id.console_text);
    if (_text == null) {
      throw new IllegalStateException("There is no TextView with id 'console_text' in Console");
    }

    _scrollView = (ScrollView) findViewById(R.id.console_scroll_view);
    if (_scrollView == null) {
      throw new IllegalStateException("There is no ScrollView with id 'console_scroll_view' in Console");
    }
  }

  //endregion

  //region Methods

  protected void writeLn(Object o) {
    if (o == null) {
      appendLine("null");
    } else {
      appendLine(o.toString());
    }
  }

  protected void appendText(String text) {
    if (text == null) {
      throw new IllegalArgumentException("text cannot be null");
    }

    _text.append(text);
    _scrollView.fullScroll(View.FOCUS_DOWN);
  }

  protected void appendLine(String line) {
    appendText(line);
    appendText("\n");
  }

  //endregion
}
