package com.jraska.console;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Console like output view, which allows writing via static console methods
 * from anywhere of application.
 * If you want to see the output, you should use console in any of your layouts,
 * all calls to console static write methods will affect all instantiated consoles.
 * You can also call writes directly to console view.
 */
public class Console extends FrameLayout {
  //region Constants

  static final String END_LINE = "\n";
  static final String REMOVING_UNSUPPORTED_MESSAGE
      = "Removing of Views is unsupported in " + Console.class;

  //endregion

  //region Public Static API

  /**
   * Write provided object String representation to console and starts new line
   * "null" is written if the object is null
   *
   * @param o Object to write
   */
  public static void writeLine(Object o) {
    WriteLine writeLine = new WriteLine(o);
    performAction(writeLine);
  }

  /**
   * Write provided object String representation to console
   * "null" is written if the object is null
   *
   * @param o Object to write
   */
  public static void write(Object o) {
    Write write = new Write(o);
    performAction(write);
  }

  /**
   * Clears the console text
   */
  public static void clear() {
    performAction(Clear.INSTANCE);
  }

  //endregion

  //region Fields

  private static List<WeakReference<Console>> _consoles = new ArrayList<>();

  // Handler for case writing is called from wrong thread
  private static volatile Handler __uiThreadHandler;
  private static final Object __lock = new Object();

  private TextView _text;

  // This will serve as flag for all view modifying methods
  // of Console to be suppressed from outside
  private boolean _privateLayoutInflated;

  // Fields is used to not schedule more than one runnable for scroll down
  private boolean _fullScrollScheduled;
  private Runnable _scrollDownRunnable;

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

    LayoutInflater.from(context).inflate(R.layout.console_content, this);
    _privateLayoutInflated = true;

    _text = findViewByIdSafe(R.id.console_text);

    ScrollView scrollView = findViewByIdSafe(R.id.console_scroll_view);
    _scrollDownRunnable = new ScrollDownRunnable(scrollView);
  }

  //endregion

  //region Properties

  String getConsoleText() {
    CharSequence text = _text.getText();
    if (text == null) {
      return "";
    }

    return text.toString();
  }

  private static Handler getUIThreadHandler() {
    if (__uiThreadHandler == null) {
      synchronized (__lock) {
        if (__uiThreadHandler == null) {
          __uiThreadHandler = new Handler(Looper.getMainLooper());
        }
      }
    }

    return __uiThreadHandler;
  }

  private static boolean isUIThread() {
    return Looper.myLooper() == Looper.getMainLooper();
  }

  //endregion

  //region FrameLayout overrides

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    _fullScrollScheduled = false;
  }

  @Override
  public void addView(View child, int index, ViewGroup.LayoutParams params) {
    // its not possible to add views to Console, allow this only on initial layout creations
    if (!_privateLayoutInflated) {
      super.addView(child, index, params);
    } else {
      throw new UnsupportedOperationException("You cannot add views to " + Console.class);
    }
  }

  @Override
  public void removeView(View view) {
    throw new UnsupportedOperationException(REMOVING_UNSUPPORTED_MESSAGE);
  }

  @Override
  public void removeViewInLayout(View view) {
    throw new UnsupportedOperationException(REMOVING_UNSUPPORTED_MESSAGE);
  }

  @Override
  public void removeViewsInLayout(int start, int count) {
    throw new UnsupportedOperationException(REMOVING_UNSUPPORTED_MESSAGE);
  }

  @Override
  public void removeViewAt(int index) {
    throw new UnsupportedOperationException(REMOVING_UNSUPPORTED_MESSAGE);
  }

  @Override
  public void removeViews(int start, int count) {
    throw new UnsupportedOperationException(REMOVING_UNSUPPORTED_MESSAGE);
  }

  @Override
  public void removeAllViews() {
    throw new UnsupportedOperationException(REMOVING_UNSUPPORTED_MESSAGE);
  }

  @Override
  public void removeAllViewsInLayout() {
    throw new UnsupportedOperationException(REMOVING_UNSUPPORTED_MESSAGE);
  }

  //endregion

  //region Methods

  void writeInternal(Object o) {
    if (o == null) {
      appendTextInternal("null");
    } else {
      appendTextInternal(o.toString());
    }
  }

  void writeLineInternal(Object o) {
    if (o == null) {
      appendLine("null");
    } else {
      appendLine(o.toString());
    }
  }

  void clearInternal() {
    _text.setText("");
  }

  void appendTextInternal(String text) {
    if (text == null) {
      throw new IllegalArgumentException("text cannot be null");
    }

    _text.append(text);

    if (!_fullScrollScheduled) {
      post(_scrollDownRunnable);
      _fullScrollScheduled = true;
    }
  }

  void appendLine(String line) {
    appendTextInternal(line);
    appendTextInternal(END_LINE);
  }

  private static void performAction(ConsoleAction action) {
    if (!isUIThread()) {
      PerformActionRunnable actionRunnable = new PerformActionRunnable(action);
      getUIThreadHandler().post(actionRunnable);
      return;
    }

    // iteration from the end to allow in place removing
    for (int consoleIndex = _consoles.size() - 1; consoleIndex >= 0; consoleIndex--) {
      WeakReference<Console> consoleReference = _consoles.get(consoleIndex);
      Console console = consoleReference.get();
      if (console == null) {
        _consoles.remove(consoleIndex);
      } else {
        action.perform(console);
      }
    }
  }

  /**
   * Throws exception if the view is not found
   *
   * @return View for the id
   */
  @SuppressWarnings("unchecked") // Class cast is checked with exception catch
  private <T extends View> T findViewByIdSafe(int resId) {
    View view = findViewById(resId);

    if (view != null) {
      try {
        return (T) view;
      }
      catch (ClassCastException ex) {
        // Just transfer message for better debug information
        String resName = getResourceName(resId);
        String message = "View with id " + resName + " is of wrong type, see inner exception";
        throw new IllegalStateException(message, ex);
      }
    }

    String resName = getResourceName(resId);

    String message = "There is no view with resource id" + resName + " in " + Console.class;
    throw new IllegalArgumentException(message);
  }

  private String getResourceName(int resId) {
    try {
      return getResources().getResourceName(resId);
    }
    catch (Resources.NotFoundException ignored) {
      // Just take hex representation of string
      return Integer.toHexString(resId);
    }
  }

  //endregion

  //region Nested classes

  static class ScrollDownRunnable implements Runnable {
    private final ScrollView _scrollView;

    ScrollDownRunnable(ScrollView scrollView) {
      if (scrollView == null) {
        throw new IllegalArgumentException("scrollView cannot be null");
      }
      _scrollView = scrollView;
    }

    @Override public void run() {
      _scrollView.fullScroll(View.FOCUS_DOWN);
    }
  }

  static class PerformActionRunnable implements Runnable {
    private final ConsoleAction _consoleAction;

    private PerformActionRunnable(ConsoleAction consoleAction) {
      _consoleAction = consoleAction;
    }

    @Override
    public void run() {
      performAction(_consoleAction);
    }
  }

  /**
   * This abstraction is here to have only one implementation of consoles
   * traversing and removing already released references.
   */
  interface ConsoleAction {
    void perform(Console console);
  }

  static final class Clear implements ConsoleAction {
    static final Clear INSTANCE = new Clear();

    @Override public void perform(Console console) {
      console.clearInternal();
    }
  }

  static final class WriteLine implements ConsoleAction {
    private final Object _value;

    public WriteLine(Object value) {
      _value = value;
    }

    @Override public void perform(Console console) {
      console.writeLineInternal(_value);
    }
  }

  static final class Write implements ConsoleAction {
    private final Object _value;

    public Write(Object value) {
      _value = value;
    }

    @Override public void perform(Console console) {
      console.writeInternal(_value);
    }
  }

  //endregion
}
