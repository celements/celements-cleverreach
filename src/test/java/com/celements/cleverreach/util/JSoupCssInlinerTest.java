package com.celements.cleverreach.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.celements.cleverreach.exception.CssInlineException;
import com.celements.common.test.AbstractComponentTest;
import com.xpn.xwiki.web.Utils;

public class JSoupCssInlinerTest extends AbstractComponentTest {

  private CssInliner cssInliner;

  @Before
  public void setUp_CssInlinerTest() {
    cssInliner = Utils.getComponent(CssInliner.class, "jsoup");
  }

  @Test
  public void testInline_null() throws CssInlineException {
    try {
      cssInliner.inline(null, "");
      fail("Expecting NPE");
    } catch (NullPointerException npe) {
      // expected outcome
    }
  }

  @Test
  public void testInline_noStyles() throws CssInlineException {
    try {
      cssInliner.inline("<div></div>", (String) null);
      fail("Expecting NPE");
    } catch (NullPointerException npe) {
      // expected outcome
    }
  }

  @Test
  public void testInline_styleFile() throws CssInlineException {
    String simpleStyle = "div {\n  display: none;\n  padding-top: 3px;\n}";
    String expect = "padding-top:3px";
    String result = cssInliner.inline("<!DOCTYPE html><html><head></head><body><div></div></body>"
        + "</html>", simpleStyle);
    assertTrue(getExpectationMessage(expect, result), result.contains(expect));
    String expect2 = "^<!DOCTYPE html>[\\s\\S]*<html[\\s\\S]*";
    assertTrue(getExpectationMessage(expect2, result), Pattern.compile(expect2,
        Pattern.CASE_INSENSITIVE).matcher(result).matches());
  }

  @Test
  public void testInline_styleFiles() throws Exception {
    String result = cssInliner.inline(fileToString("/test.html"), Arrays.asList(fileToString(
        "/testStyles1.css"), fileToString("/testStyles2.css")));
    String expect = "background-color:rgb\\(255, 0, 0\\);";
    assertTrue(getExpectationMessage(expect, result), checkInResult(result, "body", null, expect));
    // TODO can't handle important
    // expect = "width:333px;";
    // assertTrue(getExpectationMessage(expect, result), checkInResult(result, "body", null,
    // expect));
    expect = "color:rgb\\(0, 0, 255\\);";
    assertTrue(getExpectationMessage(expect, result), checkInResult(result, "li",
        "class=\"listitem\"", expect, 2));
    expect = "width:800px;";
    assertTrue(getExpectationMessage(expect, result), checkInResult(result, "table", null, expect));
    expect = "background-color:rgb\\(255, 255, 0\\);";
    assertTrue(getExpectationMessage(expect, result), checkInResult(result, "table",
        "id=\"contentTable", expect));
    assertTrue(getExpectationMessage(expect, result), checkInResult(result, "ul", null, expect));
    expect = "padding-right:2px";
    assertTrue(getExpectationMessage(expect, result), checkInResult(result, "<t[dh]",
        "class=\"column[24]", expect, 5));
    expect = "color:rgb\\(255, 255, 255\\);";
    assertTrue(getExpectationMessage(expect, result), checkInResult(result, "<t[dh]",
        "class=\"column[24]", expect, 4));
    expect = "color:rgb\\(0, 0, 0\\);";
    assertTrue(getExpectationMessage(expect, result), checkInResult(result, "<t[dh]", "specialCell",
        expect));
    expect = "color:rgb\\(170, 187, 204\\);";
    assertTrue(getExpectationMessage(expect, result), checkInResult(result, "td", "class=\"column3",
        expect, 3));
  }

  @Test
  public void testInline_nbsp() throws Exception {
    String simpleStyle = "div {\n  display: none;\n  padding-top: 3px;\n}";
    String expect = "padding-top:3px";
    String result = cssInliner.inline(fileToString("/test_nbsp.html"), simpleStyle);
    assertTrue(getExpectationMessage(expect, result), result.contains(expect));
  }

  @Test
  public void testSpecificity() {
    assertSpecificity("[0, 0, 0]", "*");
    assertSpecificity("[1, 0, 0]", "#id");
    assertSpecificity("[1, 0, 2]", "div#id ul");
    assertSpecificity("[2, 0, 2]", "div#id ul#id2");
    assertSpecificity("[0, 2, 1]", "div.class .ulclass");
    assertSpecificity("[0, 1, 1]", "a[href=\"\"]");
    assertSpecificity("[1, 1, 1]", "div#id.andclass");
  }

  private void assertSpecificity(String specificity, String selector) {
    int[] result = ((JSoupCssInliner) cssInliner).RULE_SPECIFICITY.apply(selector);
    assertEquals("Selector {" + selector + "}", specificity, Arrays.toString(result));
  }

  private boolean checkInResult(String result, String tag, String addition, String expect) {
    return checkInResult(result, tag, addition, expect, 1);
  }

  private boolean checkInResult(String result, String tag, String addition, String expect,
      int times) {
    String regex = tag + "[^>]*?";
    regex += (addition != null) ? addition + "[^>]*?" : "";
    regex += expect;
    Matcher m = Pattern.compile(regex).matcher(result);
    int count = 0;
    while (m.find()) {
      count++;
    }
    return count == times;
  }

  private String fileToString(String path) throws IOException {
    return IOUtils.toString(this.getClass().getResourceAsStream(path), "UTF-8");
  }

  private String getExpectationMessage(String expected, String result) {
    return "expected result to contain [" + expected + "], but was [" + result + "]";
  }
}