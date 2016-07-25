package com.jraska.console;

import android.content.res.Resources;
import android.view.View;

final class ViewUtil {
  private ViewUtil() {
    throw new AssertionError("No instances");
  }

  /**
   * Throws exception if the view is not found
   *
   * @return View for the id
   */
  @SuppressWarnings("unchecked") // Class cast is checked with exception catch
  static <T extends View> T findViewByIdSafe(View v, int resId) {
    View view = v.findViewById(resId);

    if (view != null) {
      try {
        return (T) view;
      }
      catch (ClassCastException ex) {
        // Just transfer message for better debug information
        String resName = getResourceName(v.getResources(), resId);
        String message = "View with id " + resName + " is of wrong type, see inner exception";
        throw new IllegalStateException(message, ex);
      }
    }

    String resName = getResourceName(v.getResources(), resId);

    String message = "There is no view with resource id" + resName + " in " + Console.class;
    throw new IllegalArgumentException(message);
  }

  private static String getResourceName(Resources resources, int resId) {
    try {
      return resources.getResourceName(resId);
    }
    catch (Resources.NotFoundException ignored) {
      // Just take hex representation of string
      return Integer.toHexString(resId);
    }
  }
}
