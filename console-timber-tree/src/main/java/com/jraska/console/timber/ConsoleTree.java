package com.jraska.console.timber;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import com.jraska.console.Console;
import timber.log.Timber;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Log.*;
import static java.util.Locale.US;

public final class ConsoleTree extends Timber.Tree {

  public static final String DEFAULT_TIMESTAMP = "HH:mm:ss";
  //region Constants

  private static final int PLACEHOLDER = 0;
  private static final int COLOR_VERBOSE = 0xff909090;
  private static final int COLOR_DEBUG = 0xffc88b48;
  private static final int COLOR_INFO = 0xffc9c9c9;
  private static final int COLOR_WARN = 0xffa97db6;
  private static final int COLOR_ERROR = 0xffff534e;
  private static final int COLOR_WTF = 0xffff5540;

  private static final int[] DEFAULT_COLORS = {PLACEHOLDER, PLACEHOLDER, COLOR_VERBOSE, COLOR_DEBUG,
      COLOR_INFO, COLOR_WARN, COLOR_ERROR, COLOR_WTF};
  private static final int REQUIRED_COLORS_LENGTH = DEFAULT_COLORS.length;

  private static final int CALL_STACK_INDEX = 6;
  private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");


  //endregion

  //region Fields

  private final int minPriority;

  private final int[] priorityColorMapping;

  private final String timeFormat;

  //endregion

  //region Constructors

  public ConsoleTree() {
    this(VERBOSE);
  }

  public ConsoleTree(int minPriority) {
    this(minPriority, DEFAULT_COLORS, null);
  }

  private ConsoleTree(int minPriority, int[] colors, String timeFormat) {
    if (colors.length != REQUIRED_COLORS_LENGTH) {
      throw new IllegalArgumentException("Colors array must have length=" + REQUIRED_COLORS_LENGTH);
    }

    this.minPriority = minPriority;
    priorityColorMapping = colors;
    this.timeFormat = timeFormat;
  }

  //endregion

  //region Tree impl

  @Override
  protected boolean isLoggable(int priority) {
    return priority >= minPriority;
  }

  @Override
  protected final void log(int priority, String tag, String message, Throwable t) {
    if (tag == null) {
      tag = getTag();
    }

    String consoleMessage;
    if (tag == null) {
      consoleMessage = String.format("%s: %s", toPriorityString(priority), message);
    } else {
      consoleMessage = String.format("%s/%s: %s", toPriorityString(priority), tag, message);
    }

    writeToConsole(priority, consoleMessage);
  }

  //endregion

  //region Methods

  protected void writeToConsole(int priority, String consoleMessage) {
    Console.writeLine(createSpannable(priority, consoleMessage));
  }

  SpannableString createSpannable(int priority, String consoleMessage) {
    SpannableString spannableString = new SpannableString(consoleMessage);
    spannableString.setSpan(new ForegroundColorSpan(priorityColorMapping[priority]), 0, consoleMessage.length(), 0);
    return spannableString;
  }

  String createStackElementTag(StackTraceElement element) {
    String tag = element.getClassName();
    Matcher matcher = ANONYMOUS_CLASS.matcher(tag);
    if (matcher.find()) {
      tag = matcher.replaceAll("");
    }

    return tag.substring(tag.lastIndexOf('.') + 1);
  }

  String getTag() {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    if (stackTrace.length <= CALL_STACK_INDEX) {
      return null;
    }
    if (timeFormat != null) {
      final String timeFormatted = new SimpleDateFormat(timeFormat, Locale.getDefault()).format(new Date());
      return String.format("%s: %s", timeFormatted, createStackElementTag(stackTrace[CALL_STACK_INDEX]));
    }

    return createStackElementTag(stackTrace[CALL_STACK_INDEX]);
  }

  protected String toPriorityString(int priority) {
    switch (priority) {
      case ASSERT:
        return "WTF";
      case ERROR:
        return "E";
      case WARN:
        return "W";
      case INFO:
        return "I";
      case DEBUG:
        return "D";
      case VERBOSE:
        return "V";

      default:
        throw new IllegalArgumentException();
    }
  }

  //endregion

  //region Nested classes

  public static final class Builder {
    private int minPriority = VERBOSE;
    private String timeFormat = null;
    private final int[] colors = Arrays.copyOf(DEFAULT_COLORS, REQUIRED_COLORS_LENGTH);

    public Builder minPriority(int priority) {
      if (priority < VERBOSE || priority > ASSERT) {
        String message = String.format(US, "Priority %d is not in range <VERBOSE, ASSERT>(<%d,%d>)",
            priority, VERBOSE, ASSERT);
        throw new IllegalArgumentException(message);
      }

      minPriority = priority;
      return this;
    }

    public Builder verboseColor(int color) {
      colors[VERBOSE] = color;
      return this;
    }

    public Builder debugColor(int color) {
      colors[DEBUG] = color;
      return this;
    }

    public Builder infoColor(int color) {
      colors[INFO] = color;
      return this;
    }

    public Builder warnColor(int color) {
      colors[WARN] = color;
      return this;
    }

    public Builder errorColor(int color) {
      colors[ERROR] = color;
      return this;
    }

    public Builder assertColor(int color) {
      colors[ASSERT] = color;
      return this;
    }

    public Builder useTimestamp(){
      this.timeFormat = DEFAULT_TIMESTAMP;
      return this;
    }

    public Builder useTimestamp(String timeFormat){
      this.timeFormat = timeFormat;
      return this;
    }

    public ConsoleTree build() {
      return new ConsoleTree(minPriority, Arrays.copyOf(colors, REQUIRED_COLORS_LENGTH), timeFormat);
    }
  }

  //endregion
}
