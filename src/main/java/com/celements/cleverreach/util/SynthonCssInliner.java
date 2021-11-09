package com.celements.cleverreach.util;

import static com.google.common.base.Preconditions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.annotation.Component;

import com.celements.cleverreach.exception.CssInlineException;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

@Component("synthon")
public class SynthonCssInliner implements CssInliner {

  private static final Logger LOGGER = LoggerFactory.getLogger(SynthonCssInliner.class);

  private static final String SYNTHON_INLINE_URL = "http://synthon.srv-inter.net:31713/cssinline";
  private static final String SERVER_SECRET = "sk/9N<JSct&^8PLn&5/KaW$uPcG$b:AZ";

  @Override
  public String inline(String html, List<String> cssList) throws CssInlineException {
    return inline(html, cssList, null);
  }

  @Override
  public @NotNull String inline(@NotNull String html, @NotNull List<String> cssList,
      Map<String, String> configs) throws CssInlineException {
    return inline(html, String.join("\n", cssList), configs);
  }

  @Override
  public @NotNull String inline(@NotNull String html, @NotNull String css)
      throws CssInlineException {
    return inline(html, css, null);
  }

  /**
   * Supported (optional) parameters:
   * removeClasses [true|false] - remove the classes from the HTML after inlining
   * inlineUrl - can be used to override the default URL used for inline service
   */
  @Override
  public String inline(String html, String css, Map<String, String> configs)
      throws CssInlineException {
    checkNotNull(html);
    checkNotNull(css);
    configs = (configs == null) ? Collections.emptyMap() : configs;
    LOGGER.trace("Applying the following CSS [{}] to HTML [{}]", css, html);
    try {
      String encodedHtml = merge(html, css);
      String postData = "secret=" + getHash(SERVER_SECRET + encodedHtml) + "&html=" + URLEncoder
          .encode(encodedHtml, "UTF-8");
      if (configs.containsKey("removeClasses")) {
        postData = "removeClasses=" + configs.get("removeClasses") + "&" + postData;
      }
      String result = inlineCss(postData.getBytes(), configs);
      LOGGER.trace("HTML with CSS INLINED [{}]", result);
      Inlined inlinedResult = new ObjectMapper().readValue(result, Inlined.class);
      if (inlinedResult.success) {
        return inlinedResult.data;
      }
      throw new CssInlineException(html, new RuntimeException(inlinedResult.error));
    } catch (IOException excp) {
      LOGGER.warn("Failed to apply CSS [{}] to HTML [{}]", css, html, excp);
      throw new CssInlineException(html, excp);
    }
  }

  private String getHash(String inStr) {
    return Hashing.sha256().hashString(inStr, StandardCharsets.UTF_8).toString();
  }

  private String inlineCss(byte[] postData, Map<String, String> configs) throws IOException,
      MalformedURLException,
      ProtocolException {
    HttpURLConnection conn = prepareConnection(configs);
    try (OutputStream out = conn.getOutputStream()) {
      out.write(postData, 0, postData.length);
    }
    StringBuilder resultBuilder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn
        .getInputStream()))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        resultBuilder.append(line);
      }
    }
    String result = resultBuilder.toString();
    return result;
  }

  private HttpURLConnection prepareConnection(Map<String, String> configs) throws IOException,
      MalformedURLException,
      ProtocolException {
    String url = configs.containsKey("inlineUrl") ? configs.get("inlineUrl") : SYNTHON_INLINE_URL;
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setRequestProperty("Accept", "application/json");
    conn.setDoOutput(true);
    return conn;
  }

  private String merge(String html, String css) {
    if (!Strings.isNullOrEmpty(css)) {
      String[] sect;
      if (html.contains("</head>")) {
        sect = html.split("(?=</head>)", 2);
      } else if (html.contains("</body>")) {
        sect = html.split("(?=</body>)", 2);
      } else if (html.contains("</html>")) {
        sect = html.split("(?=</html>)", 2);
      } else {
        sect = new String[] { "", html };
      }
      return sect[0] + "<style>" + css + "</style>" + sect[1];
    }
    return html;
  }

  private static class Inlined {

    public boolean success;

    public String data;

    public String error;

    public Inlined() {
    }
  }

}
