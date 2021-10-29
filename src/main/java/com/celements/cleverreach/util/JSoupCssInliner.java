package com.celements.cleverreach.util;

import static org.apache.commons.lang.StringUtils.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.xwiki.component.annotation.Component;

import com.celements.cleverreach.exception.CssInlineException;
import com.steadystate.css.parser.CSSOMParser;

@Component("jsoup")
public class JSoupCssInliner implements CssInliner {

  final Comparator<CSSStyleRule> SPECIFICITY_COMPARATOR = new Comparator<CSSStyleRule>() {

    @Override
    public int compare(CSSStyleRule rule1, CSSStyleRule rule2) {
      int[] spec1 = RULE_SPECIFICITY.apply(rule1.getSelectorText());
      int[] spec2 = RULE_SPECIFICITY.apply(rule2.getSelectorText());
      for (short i = 0; i <= 2; i++) {
        if ((spec1[i] - spec2[i]) != 0) {
          return spec1[i] - spec2[i];
        }
      }
      return 0;
    }
  };

  final Function<String, int[]> RULE_SPECIFICITY = selector -> {
    String elementsOnly = selector.replaceAll("(\\.|#|\\[|\\*).*?(\\]| |$)", " ").replaceAll("  ",
        " ").trim();
    elementsOnly = elementsOnly.length() > 0 ? " " + elementsOnly : elementsOnly;
    /*
     * specificity [v, w, x, y, z] = [!important (irrelevant since not on rule on)
     * , style attribute (irrelevant since we work within css)
     * , id
     * , class / pseudo-class (irrelevant since filtered out) / attribute
     * , elements]
     * our specificity here => [id, class | attribute | element]
     */
    return new int[] { countMatches(selector, "#"), countMatches(selector, ".") + countMatches(
        selector, "["), countMatches(elementsOnly, " ") };
  };

  @Override
  public @NotNull String inline(@NotNull String html, @NotNull List<String> cssList)
      throws CssInlineException {
    return inline(html, String.join("\n", cssList));
  }

  @Override
  public @NotNull String inline(@NotNull String html, @NotNull String css)
      throws CssInlineException {
    final Document doc = Jsoup.parse(html);
    final Map<Element, Map<String, String>> eleStyles = new HashMap<>();
    for (final CSSStyleRule rule : buildRuleList(css)) {
      for (final Element selected : doc.select(rule.getSelectorText())) {
        if (!eleStyles.containsKey(selected)) {
          eleStyles.put(selected, new LinkedHashMap<String, String>());
        }
        final CSSStyleDeclaration styleDecl = rule.getStyle();
        for (int i = 0; i < styleDecl.getLength(); i++) {
          final String name = styleDecl.item(i);
          eleStyles.get(selected).put(name, styleDecl.getPropertyValue(name));
        }
      }
    }
    for (final Map.Entry<Element, Map<String, String>> entry : eleStyles.entrySet()) {
      final Element ele = entry.getKey();
      final StringBuilder b = new StringBuilder();
       TODO support for !important
      for (final Map.Entry<String, String> style : entry.getValue().entrySet()) {
        b.append(style.getKey()).append(":").append(style.getValue()).append(";");
      }
      b.append(ele.attr("style"));
      ele.attr("style", b.toString());
    }
    return doc.html();
  }

  public List<CSSStyleRule> buildRuleList(String css) throws CssInlineException {
    final CSSRuleList cssRules;
    try {
      CSSStyleSheet styles = new CSSOMParser().parseStyleSheet(new InputSource(new StringReader(
          css)), null, null);
      cssRules = styles.getCssRules();
    } catch (IOException ioe) {
      throw new CssInlineException(css, ioe);
    }
    if (cssRules != null) {
      List<CSSStyleRule> rules = new ArrayList<>();
      for (int i = 0; i < cssRules.getLength(); i++) {
        CSSRule rule = cssRules.item(i);
        if (rule instanceof CSSStyleRule) {
          final CSSStyleRule styleRule = (CSSStyleRule) rule;
          Arrays.stream(styleRule.getSelectorText().split(",")).forEach(new Consumer<String>() {

            @Override
            public void accept(String selector) {
              CSSStyleRule splitStyle = styleRule; TODO --> clone CSStyleRule with reduced selector
                                                  // (split by ,)
              // Pseudo selectors like e.g. :hover cannot be inlined
              if (!styleRule.getSelectorText().contains(":")) {
                rules.add(splitStyle);
              }
            }
          });
        }
      }
      rules.sort(SPECIFICITY_COMPARATOR);
      return rules;
    }
    return Collections.emptyList();
  }

}
