package com.jraska.console.timber;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import com.jraska.console.Console;
import timber.log.Timber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConsoleTree extends Timber.Tree {

  //region Constants

  private static final int CALL_STACK_INDEX = 6;
  private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

  //endregion

  //region Fields

  private final int _minPriority;

  // TODO: 27/02/16 Make this configurable
  private final int[] _priorityColorMapping = {0, 0, Color.parseColor("#909090"),
      Color.parseColor("#c88b48"), Color.parseColor("#c9c9c9"), Color.parseColor("#a97db6"),
      Color.parseColor("#ff534e"), Color.parseColor("#ff5540")};

  //endregion

  //region Constructors

  public ConsoleTree() {
    this(Log.VERBOSE);
  }

  public ConsoleTree(int minPriority) {
    _minPriority = minPriority;
  }

  //endregion

  //region Tree impl

  @Override
  protected boolean isLoggable(int priority) {
    return priority >= _minPriority && Console.consoleViewsCount() >= 1;
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
    spannableString.setSpan(new ForegroundColorSpan(_priorityColorMapping[priority]), 0, consoleMessage.length(), 0);
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
    return createStackElementTag(stackTrace[CALL_STACK_INDEX]);
  }

  protected String toPriorityString(int priority) {
    switch (priority) {
      case Log.ASSERT:
        return "WTF";
      case Log.ERROR:
        return "E";
      case Log.WARN:
        return "W";
      case Log.INFO:
        return "I";
      case Log.DEBUG:
        return "D";
      case Log.VERBOSE:
        return "V";

      default:
        throw new IllegalArgumentException();
    }
  }

  //endregion

}
