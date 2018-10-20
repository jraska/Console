package com.jraska.console;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Console like output view, which allows writing via static console methods
 * from anywhere of application.
 * If you want to see the output, you should use console in any of your layouts,
 * all calls to console static write methods will affect all instantiated consoles.
 */
public class Console extends FrameLayout {

  //region Public Static API

  public static void writeLine() {
    controller.writeLine();
  }

  /**
   * Write provided object String representation to console and starts new line
   * "null" is written if the object is null
   *
   * @param o Object to write
   */
  public static void writeLine(Object o) {
    controller.writeLine(o);
  }

  /**
   * Write SpannableString to the console
   * "null" is written if the object is null
   *
   * @param spannableString SpannableString to write
   */
  public static void write(SpannableString spannableString) {
    controller.write(spannableString);
  }

  /**
   * Write Spannable to console and starts new line
   * "null" is written if the object is null
   *
   * @param spannableString SpannableString to write
   */
  public static void writeLine(SpannableString spannableString) {
    controller.writeLine(spannableString);
  }

  /**
   * Write provided object String representation to console
   * "null" is written if the object is null
   *
   * @param o Object to write
   */
  public static void write(Object o) {
    controller.write(o);
  }

  /**
   * Clears the console text
   */
  public static void clear() {
    controller.clear();
  }

  public static int consoleCount() {
    return controller.size();
  }

  //endregion

  static final ConsoleController controller = new ConsoleController();

  private TextView text;
  private ScrollView scrollView;

  // Fields are used to not schedule more than one runnable for scroll down
  private boolean fullScrollScheduled;
  private final Runnable scrollDownRunnable = new Runnable() {
    @Override
    public void run() {
      scrollFullDown();
    }
  };

  private UserTouchingListener userTouchingListener;
  private FlingProperty flingProperty;

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

  @SuppressLint("ClickableViewAccessibility")
  private void init(Context context) {
    // Store myself as weak reference for static method calls
    controller.add(this);

    LayoutInflater.from(context).inflate(R.layout.console_content, this);

    text = findViewById(R.id.console_text);

    scrollView = findViewById(R.id.console_scroll_view);
    flingProperty = FlingProperty.Companion.create(scrollView);
    userTouchingListener = new UserTouchingListener();
    scrollView.setOnTouchListener(userTouchingListener);

    printBuffer();
    // need to have extra post here, because scroll view is fully initialized after another frame
    post(new Runnable() {
      @Override
      public void run() {
        scrollDown();
      }
    });
  }

  CharSequence getConsoleText() {
    return text.getText().toString();
  }

  private boolean isUserInteracting() {
    return userTouchingListener.isUserTouching() || flingProperty.isFlinging();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    fullScrollScheduled = false;
  }

  void printScroll() {
    printBuffer();
    scrollDown();
  }

  private void printBuffer() {
    controller.printTo(text);
  }

  private void scrollDown() {
    if (!isUserInteracting() && !fullScrollScheduled) {
      post(scrollDownRunnable);
      fullScrollScheduled = true;
    }
  }

  private void scrollFullDown() {
    scrollView.fullScroll(View.FOCUS_DOWN);
  }
}
