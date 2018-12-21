package com.celements.cleverreach;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.web.Utils;

@Component(CleverReachScriptService.COMPONENT_NAME)
public class CleverReachScriptService implements ScriptService {

  public static final String COMPONENT_NAME = "cleverreach";

  public String debugWhoami(String clientId, String clientSecret) {
    CleverReachService client = getNewCleverReachRestService();
    if (client.init(clientId, clientSecret)) {
      return "<h3>WHOAMI:</h3><div><pre>" + formateDebugOutput(client.whoami())
          + "</pre></div><h3>TTL:</h3><div>" + "<pre>" + formateDebugOutput(client.ttl())
          + "</pre></div>";
    }
    return "Login failed with given credentials.";
  }

  String formateDebugOutput(String debugStr) {
    return debugStr.replaceAll("\\{", "{\n").replaceAll(",", ",\n  ").replaceAll("\\}", "\n}");
  }

  CleverReachService getNewCleverReachRestService() {
    return Utils.getComponent(CleverReachService.class, CleverReachRest.COMPONENT_NAME);
  }
}
