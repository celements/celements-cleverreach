package com.celements.cleverreach.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.celements.common.test.AbstractComponentTest;

public class CssInlinerTest extends AbstractComponentTest {

  @Before
  public void setUp_CssInlinerTest() {

  }

  @Test
  public void testInline_null() {
    try {
      CssInliner.inline(null, "");
      fail("Expecting NPE");
    } catch (NullPointerException npe) {
      // expected outcome
    }
  }

  @Test
  public void testInline_noStyles() {
    try {
      CssInliner.inline("<div></div>", null);
      fail("Expecting NPE");
    } catch (NullPointerException npe) {
      // expected outcome
    }
  }

  @Test
  public void testInline_styleFile() {
    String simpleStyle = "div {\n  display: none;\n  padding-top: 3px;\n}";
    String expected = "padding-top: 3px";
    String result = CssInliner.inline(
        "<!DOCTYPE html><html><head></head><body><div></div></body></html>", simpleStyle);
    assertTrue(getExpectationMessage(expected, result), result.contains(expected));
  }

  private String getExpectationMessage(String expected, String result) {
    return "expected result to contain [" + expected + "], but was [" + result + "]";
  }
}
