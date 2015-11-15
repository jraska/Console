package com.jraska.console;

import android.content.Context;
import android.view.View;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Test is moved here from console library module to enable Robolectric testing
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ConsoleTest {
  //region Fields

  private Console _console;

  //endregion

  //region Setup methods

  @Before
  public void setUp() throws Exception {
    _console = new Console(getAppContext());
  }

  //endregion

  //region Test Methods

  //region View methods failures tests

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_addView() throws Exception {
    _console.addView(new View(getAppContext()));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeViewAt() throws Exception {
    _console.removeViewAt(0);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeView() throws Exception {
    _console.removeView(_console.getChildAt(0));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeAllViews() throws Exception {
    _console.removeAllViews();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFailsOn_removeAllViewsInLayout() throws Exception {
    _console.removeAllViewsInLayout();
  }

  //endregion

  //endregion

  //region Properties

  public Context getAppContext() {
    return RuntimeEnvironment.application;
  }

  //endregion
}
