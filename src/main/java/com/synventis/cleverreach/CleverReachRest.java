package com.synventis.cleverreach;

import static com.celements.model.util.References.*;
import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.SpaceReference;

import com.celements.auth.classes.RemoteLoginClass;
import com.celements.model.access.IModelAccessFacade;
import com.celements.model.access.exception.DocumentNotExistsException;
import com.celements.model.classes.ClassDefinition;
import com.celements.model.context.ModelContext;
import com.celements.model.object.xwiki.XWikiObjectFetcher;
import com.google.common.base.Optional;
import com.sun.syndication.io.impl.Base64;
import com.xpn.xwiki.objects.BaseObject;

@Component(CleverReachRest.COMPONENT_NAME)
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class CleverReachRest implements CleverReachService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CleverReachRest.class);

  public static final String COMPONENT_NAME = "rest";

  private static final String DEFAULT_REST_URL = "https://rest.cleverreach.com/";
  private static final String PATH_VERSION = "v3/";
  private static final String PATH_LOGIN = "oauth/token.php";
  private static final String PATH_MAILINGS = "mailings.json/";
  private static final String PATH_WHOAMI = "debug/whoami.json";
  private static final String PATH_TTL = "debug/ttl.json";

  private static enum SubmitMethod {
    GET, POST, PUT, DELETE
  };

  @Requirement
  private IModelAccessFacade modelAccess;

  @Requirement
  private ModelContext modelContext;

  @Requirement(RemoteLoginClass.CLASS_DEF_HINT)
  private ClassDefinition remoteLoginClass;

  private String restBaseUrl;
  private String clientId;
  private String clientSecret;
  private CleverReachToken token;

  @Override
  public boolean init(String clientId, String clientSecret) {
    return init(DEFAULT_REST_URL, clientId, clientSecret);
  }

  @Override
  public boolean init(String restBaseUrl, String clientId, String clientSecret) {
    checkArgument(!isNullOrEmpty(restBaseUrl));
    this.restBaseUrl = restBaseUrl;
    checkArgument(!isNullOrEmpty(clientId));
    this.clientId = clientId;
    checkArgument(!isNullOrEmpty(clientSecret));
    this.clientSecret = clientSecret;
    token = new CleverReachToken(0);
    return initRequest();
  }

  @Override
  public boolean initFromConfig() {
    Optional<BaseObject> configObj = Optional.absent();
    try {
      configObj = XWikiObjectFetcher.on(modelAccess.getDocument(
          getXWikiPreferencesDocRef())).filter(remoteLoginClass).first();
    } catch (DocumentNotExistsException dnee) {
      LOGGER.warn("Document XWikiPreferences does not exist", dnee);
    }
    if (configObj.isPresent()) {
      Optional<String> restBaseUrl = modelAccess.getFieldValue(configObj.get(),
          RemoteLoginClass.FIELD_URL);
      Optional<String> clientId = modelAccess.getFieldValue(configObj.get(),
          RemoteLoginClass.FIELD_USERNAME);
      Optional<String> clientSecret = modelAccess.getFieldValue(configObj.get(),
          RemoteLoginClass.FIELD_PASSWORD);
      return init(restBaseUrl.orNull(), clientId.orNull(), clientSecret.orNull());
    }
    LOGGER.warn("No config found on XWikiPreferences");
    return false;
  }

  @Override
  public boolean updateMailing(String mailingId, String subject, String contentHtml,
      String contentPlain) {
    Mailing formData = new Mailing();
    formData.subject = subject;
    formData.content.html = contentHtml;
    formData.content.text = contentPlain;
    Response response = sendRestRequest(PATH_MAILINGS + mailingId, formData, SubmitMethod.PUT);
    LOGGER.debug("Tagesagenda response [{}]", response);
    if ((response != null) && response.hasEntity()) {
      String content = response.readEntity(String.class);
      LOGGER.debug("Tagesagenda response content [{}]", content);
      return content.contains(mailingId) && content.matches(".*\"success\".{0,1}:.{0,1}true.*");
    }
    return false;
  }

  @Override
  public String whoami() {
    return runDebugRequest(PATH_WHOAMI);
  }

  @Override
  public String ttl() {
    return runDebugRequest(PATH_TTL);
  }

  void initCheck() {
    checkArgument(token != null, "CleverReachRest is not initialized!");
  }

  Response sendRestRequest(String path, Object data, SubmitMethod method) {
    if (initRequest()) {
      method = (method != null) ? method : SubmitMethod.POST;
      String authHeader = token.getTokenType() + " " + token.getToken();
      if (data instanceof MultivaluedMap) {
        getMultivalueMapFromOjb(data).add("token", token.getToken());
      }
      Response response = sendRequest(PATH_VERSION + path, data, authHeader, method);
      if (response.getStatus() == 200) {
        return response;
      } else {
        LOGGER.warn("Request response status != 200. Response [{}]", response);
        if (response.hasEntity()) {
          LOGGER.warn("Response content [{}]", response.readEntity(String.class));
        }
      }
    }
    return null;
  }

  Response sendRequest(String path, Object data, String authHeader, SubmitMethod method) {
    WebTarget target = ClientBuilder.newClient().target(restBaseUrl).path(path);
    addGetParameters(data, target, method);
    Builder request = target.request().header("Authorization", authHeader);
    if (method == SubmitMethod.GET) {
      return request.get();
    } else if (method == SubmitMethod.PUT) {
      return request.put(getRequestDataEntity(data));
    } else if (method == SubmitMethod.DELETE) {
      return request.delete();
    }
    // Default to SubmitMethod.POST
    return request.post(getRequestDataEntity(data));
  }

  boolean initRequest() {
    if (!token.isValid()) {
      LOGGER.debug("initializing login");
      login();
    }
    return token.isValid();
  }

  void login() {
    MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
    formData.putSingle("grant_type", "client_credentials");
    String authHeader = "Basic " + Base64.encode(clientId + ":" + clientSecret);
    Response response = sendRequest(PATH_LOGIN, formData, authHeader, SubmitMethod.POST);
    boolean hasContent = response.hasEntity();
    String content = null;
    if (hasContent) {
      // readEntity closes the response stream, so it can only be read once
      content = response.readEntity(String.class);
    }
    traceLogLoginResponse(response, content);
    if (hasContent) {
      initializeToken(response, content);
    } else {
      LOGGER.warn("Unable to connect and receive token. Response [{}]", response);
    }
  }

  void initializeToken(Response response, String jsonResponse) {
    ObjectMapper objMapper = new ObjectMapper();
    try {
      token = objMapper.readValue(jsonResponse.getBytes(), CleverReachToken.class);
      if (token.isValid()) {
        LOGGER.debug("new token received [{}]", token);
      } else {
        LOGGER.warn("Unable to receive token. Response [{}]", response);
      }
    } catch (IOException ioe) {
      LOGGER.warn("IOException caught. Unable to connect and receive token. Response [{}].",
          response);
      if (LOGGER.isTraceEnabled()) {
        // Workaround to log stack trace in trace despite NoStackTracePatternLayout
        LOGGER.error("Exception Reason", ioe);
      }
    }
  }

  String runDebugRequest(String path) {
    initCheck();
    Response response = sendRestRequest(path, new MultivaluedHashMap<String, String>(),
        SubmitMethod.GET);
    if ((response != null) && (response.getStatus() == 200) && response.hasEntity()) {
      return response.readEntity(String.class);
    }
    return "Response status [" + ((response != null) ? response.getStatus() : "?")
        + "]. See log for details.";
  }

  Entity<?> getRequestDataEntity(Object data) {
    if (data instanceof MultivaluedMap) {
      return Entity.form(getMultivalueMapFromOjb(data));
    } else { // POJO -> build JSON
      ObjectMapper mapper = new ObjectMapper();
      try {
        String dataJson = mapper.writeValueAsString(data);
        LOGGER.trace("JSON for request: [{}]", dataJson);
        return Entity.json(dataJson);
      } catch (IOException ioe) {
        LOGGER.error("Exception serializing data to json. Data [{}]", data, ioe);
      }
    }
    return Entity.text("");
  }

  void addGetParameters(Object data, WebTarget target, SubmitMethod method) {
    if ((method == SubmitMethod.GET) && (data instanceof MultivaluedMap)) {
      MultivaluedMap<String, String> fromData = getMultivalueMapFromOjb(data);
      for (String key : fromData.keySet()) {
        target.queryParam(key, fromData.get(key).toArray());
      }
    }
  }

  @SuppressWarnings("unchecked")
  MultivaluedMap<String, String> getMultivalueMapFromOjb(Object data) {
    return (MultivaluedMap<String, String>) data;
  }

  DocumentReference getXWikiPreferencesDocRef() {
    return create(DocumentReference.class, "XWikiPreferences", create(SpaceReference.class, "XWiki",
        modelContext.getWikiRef()));
  }

  void traceLogLoginResponse(Response response, String content) {
    if (LOGGER.isTraceEnabled()) {
      MultivaluedMap<String, Object> headers = response.getHeaders();
      LOGGER.trace("Headers:");
      for (String header : headers.keySet()) {
        LOGGER.trace("header [{}] = [{}]", header, headers.get(header));
      }
      Map<String, NewCookie> cookies = response.getCookies();
      LOGGER.trace("Cookies:");
      for (String cookie : cookies.keySet()) {
        LOGGER.trace("cookie [{}] = [{}]", cookie, cookies.get(cookie));
      }
      LOGGER.trace("Content [{}]", content.replaceAll("^(.*\"access_token\":\")[^\"]*(.*)$",
          "$1********$2"));
    }
  }
}
