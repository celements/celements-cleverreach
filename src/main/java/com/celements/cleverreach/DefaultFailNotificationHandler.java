package com.celements.cleverreach;

import static com.celements.model.util.References.*;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.model.reference.ClassReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.SpaceReference;

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
      for (BaseObject receiver : receivers) {
        if (1 == receiver.getIntValue("is_active")) {
          String content = "<h2>" + excp.getMessage() + "</h2><div>" + msg + "</div>";
          Optional<String> fromMail = getFromMail(configDoc);
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
