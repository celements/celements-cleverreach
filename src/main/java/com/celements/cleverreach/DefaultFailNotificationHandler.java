package com.celements.cleverreach;

import static com.celements.model.util.References.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.model.reference.ClassReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.SpaceReference;

import com.celements.cleverreach.exception.CleverReachRequestFailedException;
import com.celements.cleverreach.exception.CssInlineException;
import com.celements.common.classes.IClassCollectionRole;
import com.celements.mailsender.IMailSenderRole;
import com.celements.model.access.IModelAccessFacade;
import com.celements.model.access.exception.DocumentNotExistsException;
import com.celements.model.context.ModelContext;
import com.celements.model.object.xwiki.XWikiObjectFetcher;
import com.celements.web.classcollections.OldCoreClasses;
import com.celements.web.classes.FormMailClass;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

@Component
public class DefaultFailNotificationHandler implements FailNotificationHandlerRole {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      DefaultFailNotificationHandler.class);

  @Requirement
  private ModelContext context;

  @Requirement
  private IModelAccessFacade modelAccess;

  @Requirement
  private IMailSenderRole mailSender;

  @Requirement("celements.oldCoreClasses")
  private IClassCollectionRole oldCoreClasses;

  @Override
  public void send(String msg, Exception excp) {
    LOGGER.error(msg, excp);
    try {
      XWikiDocument configDoc = modelAccess.getDocument(getConfigDocRef());
      List<BaseObject> receivers = XWikiObjectFetcher.on(configDoc).filter(
          getReceiverEmailClassRef()).list();
      String content = getMailingContent(msg, excp);
      Optional<String> fromMail = getFromMail(configDoc);
      for (BaseObject receiver : receivers) {
        if (1 == receiver.getIntValue("is_active")) {
          if (fromMail.isPresent()) {
            mailSender.sendMail(fromMail.get(), null, receiver.getStringValue("email"), null,
                null, "TAGESAGENDA UPDATE FAILED!", content, content, null, null);
          } else {
            LOGGER.error("Missing 'from' mail configuration for 'Tagesagenda update failed' "
                + "notification");
          }
        }
      }
    } catch (DocumentNotExistsException dnee) {
      LOGGER.error("Unable to read failed notification configuration. Doc does not exist.", dnee);
    }
  }

  String getMailingContent(String msg, Exception excp) {
    StringWriter content = new StringWriter();
    PrintWriter pw = new PrintWriter(new StringWriter());
    excp.printStackTrace(pw);
    content.append("<h2>")
        .append(excp.getMessage())
        .append("</h2><div>")
        .append(msg).append("</div><hr /><pre>");
    if ((excp instanceof CleverReachRequestFailedException)
        && (((CleverReachRequestFailedException) excp).getResponse() != null)) {
      Response resp = ((CleverReachRequestFailedException) excp).getResponse();
      content.append("Status Code: ").append(Integer.toString(resp.getStatus())).append("\n");
      content.append("Length: ").append(Integer.toString(resp.getLength())).append("\n");
      String respHeaders = resp.getStringHeaders().entrySet().stream().collect(
          Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().collect(Collectors
              .joining(" | ")))).entrySet().stream().map(entry -> entry.getKey() + " = " + entry
                  .getValue() + "\n").collect(Collectors.joining());
      content.append("Header String:\n").append(respHeaders).append("\n");
      content.append("Body:\n").append(resp.readEntity(String.class));
    } else if (excp instanceof CssInlineException) {
      content.append(((CssInlineException) excp).getExtendedMessage());
    }
    content.append("</pre><hr /><pre>")
        .append(pw.toString())
        .append("</pre>");
    pw.close();
    return content.toString();
  }

  Optional<String> getFromMail(XWikiDocument configDoc) {
    return XWikiObjectFetcher.on(configDoc).fetchField(FormMailClass.FIELD_EMAIL_FROM).first()
        .toJavaUtil();
  }

  private ClassReference getReceiverEmailClassRef() {
    return new ClassReference(((OldCoreClasses) oldCoreClasses).getReceiverEmailClassRef(
        context.getWikiRef().getName()));
  }

  public DocumentReference getConfigDocRef() {
    return create(DocumentReference.class, CleverReachRest.REST_CONFIG_DOC_NAME,
        create(SpaceReference.class, CleverReachRest.REST_CONFIG_SPACE_NAME, context.getWikiRef()));
  }

}
