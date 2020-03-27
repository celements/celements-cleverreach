package com.celements.cleverreach.exception;

import static com.celements.common.test.CelementsTestUtils.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.celements.common.test.AbstractComponentTest;

public class CssInlineExceptionTest extends AbstractComponentTest {

  @Test
  public void testScanLines_message_ampersand() {
    String result = new CssInlineException(HTML, null).scanLines(STACK_TRACE1, 1, 1,
        CssInlineException.SHORT_MESSAGE);
    assertTrue("Result should start with line number [" + LINE_NR1 + "] but is [" + result + "]",
        result.startsWith(LINE_NR1));
    assertTrue("Result should end with message. Result: [" + result + "]", result.trim().endsWith(
        MESSAGE1));
  }

  @Test
  public void testScanLines_html_ampersand() {
    String result = new CssInlineException(HTML, null).scanLines(HTML, 245, 249,
        CssInlineException.SNIPPET);
    assertTrue("Result should be around line [" + LINE_NR1 + "] but is [" + result + "]",
        result.startsWith("245: ") && result.contains("247: <b>Jazzmatizz"));
  }

  @Test
  public void testScanLines_message_nonClosingTag() throws IOException {
    String html = IOUtils.toString(this.getClass().getResourceAsStream(
        "/FailingCssInlnlineNonClosingDiv.html"), "UTF-8");
    Exception excp = createMockAndAddToDefault(Exception.class);
    expect(excp.getCause()).andReturn(excp).anyTimes();
    expect(excp.getMessage()).andReturn(MESSAGE2).anyTimes();
    excp.printStackTrace(anyObject(PrintWriter.class));
    expectLastCall();
    CssInlineException inlineExcp = new CssInlineException(html, excp);
    inlineExcp.injected_sw = new StringWriter();
    inlineExcp.injected_sw.append(STACK_TRACE2);
    inlineExcp.injected_excp = excp;
    replayDefault();
    String result = inlineExcp.getExceptionRangeSnippet();
    verifyDefault();
    assertTrue("Result should start with line number [" + MESSAGE2 + "] but is [" + result + "]",
        result.startsWith(MESSAGE2));
    assertTrue("Result should contain [" + result + "]", result.contains("691: Eintritt") && result
        .contains("694: <a href="));
  }

  @Test
  public void testScanLines_html_nonClosingTag() throws IOException {
    String html = IOUtils.toString(this.getClass().getResourceAsStream(
        "/FailingCssInlnlineNonClosingDiv.html"), "UTF-8");
    String result = new CssInlineException(html, null).scanLines(html, 690, 694,
        CssInlineException.SNIPPET);
    assertTrue("Result should be around line [" + LINE_NR2 + "] but is [" + result + "]",
        result.startsWith("690: <b>ABGESAGT") && result.contains("691: Eintritt"));
  }

  static final String LINE_NR1 = "247";

  static final String MESSAGE1 = "The entity name must immediately follow the '&' in the entity reference. "
      + "Nested exception: The entity name must immediately follow the '&' in the entity reference.";

  static final String STACK_TRACE1 = "org.dom4j.DocumentException: Error on line " + LINE_NR1
      + " of document  : "
      + MESSAGE1 + "\n        at org.dom4j.io.SAXReader.read(SAXReader.java:482)\n"
      + "        at com.celements.cleverreach.util.DefaultCssInliner.prepareInput(DefaultCssInliner.java:65)\n"
      + "        at com.celements.cleverreach.util.DefaultCssInliner.inline(DefaultCssInliner.java:48)\n"
      + "        at com.celements.cleverreach.util.DefaultCssInliner.inline(DefaultCssInliner.java:39)\n"
      + "        at com.celements.cleverreach.MailingConfig.getContentHtmlCssInlined(MailingConfig.java:88)\n"
      + "        at com.celements.cleverreach.CleverReachRest.updateMailing(CleverReachRest.java:82)\n"
      + "        at ch.programmonline.proz.TagesagendaCleverReachUpdateJob.executeJob(TagesagendaCleverReachUpdateJob.java:69)\n"
      + "        at com.celements.scheduler.job.AbstractJob.execute(AbstractJob.java:80)\n"
      + "        at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n"
      + "        at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:525)\n"
      + "Nested exception:\n"
      + "org.xml.sax.SAXParseException; lineNumber: 247; columnNumber: 114; The entity name must immediately follow the '&' in the entity reference.\n"
      + "        at org.apache.xerces.util.ErrorHandlerWrapper.createSAXParseException(Unknown Source)\n"
      + "        at org.apache.xerces.util.ErrorHandlerWrapper.fatalError(Unknown Source)\n"
      + "        at org.apache.xerces.impl.XMLErrorReporter.reportError(Unknown Source)\n"
      + "        at org.apache.xerces.impl.XMLErrorReporter.reportError(Unknown Source)\n"
      + "        at org.apache.xerces.impl.XMLScanner.reportFatalError(Unknown Source)\n"
      + "        at org.apache.xerces.impl.XMLDocumentFragmentScannerImpl.scanEntityReference(Unknown Source)\n"
      + "        at org.apache.xerces.impl.XMLDocumentFragmentScannerImpl$FragmentContentDispatcher.dispatch(Unknown Source)\n"
      + "        at org.apache.xerces.impl.XMLDocumentFragmentScannerImpl.scanDocument(Unknown Source)\n"
      + "        at org.apache.xerces.parsers.XML11Configuration.parse(Unknown Source)\n"
      + "        at org.apache.xerces.parsers.XML11Configuration.parse(Unknown Source)\n"
      + "        at org.apache.xerces.parsers.XMLParser.parse(Unknown Source)\n"
      + "        at org.apache.xerces.parsers.AbstractSAXParser.parse(Unknown Source)\n"
      + "        at org.apache.xerces.jaxp.SAXParserImpl$JAXPSAXParser.parse(Unknown Source)\n"
      + "        at org.dom4j.io.SAXReader.read(SAXReader.java:465)\n"
      + "        at com.celements.cleverreach.util.DefaultCssInliner.prepareInput(DefaultCssInliner.java:65)\n"
      + "        at com.celements.cleverreach.util.DefaultCssInliner.inline(DefaultCssInliner.java:48)\n"
      + "        at com.celements.cleverreach.util.DefaultCssInliner.inline(DefaultCssInliner.java:39)\n"
      + "        at com.celements.cleverreach.MailingConfig.getContentHtmlCssInlined(MailingConfig.java:88)\n"
      + "        at com.celements.cleverreach.CleverReachRest.updateMailing(CleverReachRest.java:82)\n"
      + "        at ch.programmonline.proz.TagesagendaCleverReachUpdateJob.executeJob(TagesagendaCleverReachUpdateJob.java:69)\n"
      + "        at com.celements.scheduler.job.AbstractJob.execute(AbstractJob.java:80)\n"
      + "        at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n"
      + "        at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:525)";

  private static final String EXCEPTION_CAUSE_5_LINES1 = "        <td><div class=\"td\">\n"
      + "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent128127&amp;performance=453165\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      + "                    <b>Jazzmatizz</b></font></span></a>          Niko Seibold (sax) Meets Christoph Neuhaus (g) & Thomas Bauser (h-org)\n"
      + "                      &nbsp;&#x25c6;&nbsp;\n"
      + "                    Jazz. Eintritt frei\n";

  private static final String HTML = "<!DOCTYPE html><html lang=\"de\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>PROGRAMMzeitung - Newsletter</title><meta name=\"x-apple-disable-message-reformatting\" /></head>\n"
      +
      "<body>\n" +
      "<center><table cellpadding=\"0\" cellspacing=\"8\" border=\"0\" class=\"wrapper\"><tbody><tr><td width=\"750\"><div class=\"header\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody><tr class=\"toprow\"><td colspan=\"2\" class=\"banner_top\"><div class=\"cel_cell banner banner_top\" id=\"bannerCell\"><!--\n"
      +
      "pageDocRef : name = [TagesagendaBanner], type = [DOCUMENT], parent = [name = [Content_TagesagendaBannerCell], type = [SPACE], parent = [name = [programmzeitung], type = [WIKI], parent = [null]]]\n"
      +
      "celPDCdocPageType : RichText\n" +
      "-->\n" +
      "      <div id=\"cellPDContent1TagesagendaBanner:true:TagesagendaBannerCell:Content_TagesagendaBannerCell.TagesagendaBanner\"   class=\"cel_cm_pagedependent_content\">\n"
      +
      "       <div class=\"cellPDContent-body cel_pagetype_RichText\">\n" +
      "<!--\n" +
      "  page_type : RichText\n" +
      "  useInlineEditorMode : false\n" +
      "  isRenderViewMode : true\n" +
      "-->\n" +
      "        <div id=\"rteContentTagesagendaBanner:Content_TagesagendaBannerCell.TagesagendaBanner\" class=\"rteContent cel_cm_rte_content\" ondblclick='window.location.href=\"/edit/Content_TagesagendaBannerCell/TagesagendaBanner?language=de&amp;xredirect=/TagesagendaBanner\"' onselectstart='javascript:return false;'>\n"
      +
      "<p><a href=\"http://www.zhaw.ch/zkm/cas-kmkv\" target=\"_blank\"><img width=\"750\" height=\"142\" class=\"celanim_addCounterNone celanim_autostartnostop celanim_addCounterOverlayNone\" src=\"https://programmzeitung.prog.online/download/Content_attachments/FileBaseDoc/Banner-TA-ZHAW-2019.gif?celwidth=750&amp;celheight=142&amp;raw=1\" border=\"0\" /></a></p>\n"
      +
      "<div class=\"rteEndClear\"><!-- IE6 --></div>\n" +
      "<!-- IE6 --></div>\n" +
      "\n" +
      "  <!-- IE6 --></div>\n" +
      "    <!-- IE6 --></div>\n" +
      "  </div></td></tr><tr class=\"bottomrow\"><td><a href=\"https://programmzeitung.ch\"><img src=\"https://programmzeitung.ch/download/Content_attachments/FileBaseDoc/ProgrammZeitung%2DLogo.png?cropX=1&amp;cropY=13&amp;cropW=359&amp;cropH=55&amp;\" border=\"0\" alt=\"Programmzeitung Logo\" /></a><br/><br/><br/><h1>Mehr als Heute</h1><br/><p class=\"theWeek\"><span class=\"link\"><a class=\"currentDay\" href=\"https://programmzeitung.ch/Tagesagenda?startDate=04.02.2020\" title=\"Dienstag 04.02.2020\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">Dienstag</font></span></a></span>&nbsp;&#x25c6;&nbsp;<span class=\"link\"><a class=\"\" href=\"https://programmzeitung.ch/Tagesagenda?startDate=05.02.2020\" title=\"Mittwoch 05.02.2020\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">Mittwoch</font></span></a></span>&nbsp;&#x25c6;&nbsp;<span class=\"link\"><a class=\"\" href=\"https://programmzeitung.ch/Tagesagenda?startDate=06.02.2020\" title=\"Donnerstag 06.02.2020\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">Donnerstag</font></span></a></span>&nbsp;&#x25c6;&nbsp;<span class=\"link\"><a class=\"\" href=\"https://programmzeitung.ch/Tagesagenda?startDate=07.02.2020\" title=\"Freitag 07.02.2020\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">Freitag</font></span></a></span>&nbsp;&#x25c6;&nbsp;<span class=\"link\"><a class=\"\" href=\"https://programmzeitung.ch/Tagesagenda?startDate=08.02.2020\" title=\"Samstag 08.02.2020\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">Samstag</font></span></a></span>&nbsp;&#x25c6;&nbsp;<span class=\"link\"><a class=\"\" href=\"https://programmzeitung.ch/Tagesagenda?startDate=09.02.2020\" title=\"Sonntag 09.02.2020\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">Sonntag</font></span></a></span>&nbsp;&#x25c6;&nbsp;<span class=\"link\"><a class=\"\" href=\"https://programmzeitung.ch/Tagesagenda?startDate=10.02.2020\" title=\"Montag 10.02.2020\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">Montag</font></span></a></span></p></td><td class=\"banner_right\"><div class=\"cel_cell banner banner_right\" id=\"bannerCell\"><!--\n"
      +
      "pageDocRef : name = [PDC-Default_Content], type = [DOCUMENT], parent = [name = [Content_TagesagendaBannerSmallCell], type = [SPACE], parent = [name = [programmzeitung], type = [WIKI], parent = [null]]]\n"
      +
      "celPDCdocPageType : RichText\n" +
      "-->\n" +
      "    <div id=\"cellPDContent2PDC-Default_Content:true:TagesagendaBannerSmallCell:Content_TagesagendaBannerSmallCell.TagesagendaBannerSmall\"   class=\"cel_cm_pagedependent_content\">\n"
      +
      "       <div class=\"cellPDContent-body cel_pagetype_RichText\">\n" +
      "<!--\n" +
      "  page_type : RichText\n" +
      "  useInlineEditorMode : false\n" +
      "  isRenderViewMode : true\n" +
      "-->\n" +
      "        <div id=\"rteContentPDC-Default_Content:Content_TagesagendaBannerSmallCell.PDC-Default_Content\" class=\"rteContent cel_cm_rte_content celRTEempty\" ondblclick='window.location.href=\"/edit/Content_TagesagendaBannerSmallCell/PDC%2DDefault_Content?language=de&amp;xredirect=/TagesagendaBannerSmall\"' onselectstart='javascript:return false;'>\n"
      +
      "\n" +
      "<div class=\"rteEndClear\"><!-- IE6 --></div>\n" +
      "<!-- IE6 --></div>\n" +
      "\n" +
      "  <!-- IE6 --></div>\n" +
      "    <!-- IE6 --></div>\n" +
      "  </div></td></tr></tbody></table></div><div class=\"tagesagenda_ueberschrift_wrapper\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody>\n"
      +
      "<td class=\"agenda\"><div class=\"td\"><span class=\"ueberschrift\">Agenda</span></div></td><td class=\"day\"><div class=\"td\"><span class=\"tagesagenda_datum\">Dienstag, 04.02.2020</span></div></td></tbody></table></div>\n"
      +
      "\n" +
      "<!--[if mso]>\n" +
      "<div style=\"font-family: Arial, sans-serif;font-size: 4px;line-height: 4px; height: 4px; min-height: 4px; max-height: 4px;\">&nbsp;</div>\n"
      +
      "<![endif]-->\n" +
      "<div class=\"tagesagenda\">\n" +
      "  <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"ueberschrift\"><tbody>\n" +
      "    <tr><td><div class=\"td\">Zeit</div></td><td><div class=\"td\">Veranstaltung</div></td><td><div class=\"td\">Ort &#x25c6; Veranstalter</div></td></tr>\n"
      +
      "  </tbody></table>\n" +
      "\n" +
      "        <div class=\"category\">\n" +
      "            <div class=\"h2\"><div>Film</div></div>\n" +
      "    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">12:15</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129447&amp;performance=448222\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Mittagskino: The Aeronauts</b></font></span></a>\n" +
      "                    Tom Harper, GB 2019\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129447&amp;performance=448222\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Kultkino Atelier\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "               </tbody></table>\n" +
      "    </div>\n" +
      "      <div class=\"category\">\n" +
      "            <div class=\"h2\"><div>Kinder&nbsp;&amp;&nbsp;Familien</div></div>\n" +
      "    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">08:00 – 18:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129151&amp;performance=446412\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Basler Jugendb&uuml;cherschiff: Sonderthema &ndash; Echt jetzt?</b></font></span></a>\n"
      +
      "                    28.1.&ndash;11.2. Programm: <a href=\"https://programmzeitung.prog.online/ProgonEvent/ProgonEvent129151?xpage=celements_ajax&amp;ajax_mode=redirectURL&amp;url=http://www.edubs.ch/buecherschiff\" rel=\"nofollow\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">www.edubs.ch/buecherschiff</font></span></a>\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129151&amp;performance=446412\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        MS Christoph Merian\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    Basler Jugendb&uuml;cherschiff\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">10:15 – 11:15</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=ggg:ProgonEvent.ProgonEvent52&amp;performance=Performance1177\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Gschichtetaxi in der Bibliothek Basel West</b></font></span></a>\n" +
      "                    <p>Die Reihe Gschichtetaxi bietet Monat f&uuml;r Monat interessante Begegnungen mit Geschichten und Sachmedien f&uuml;r Kinder ab 3 Jahren und ihre Begleitpersonen. Leitung: Iris Keller, Leseanimatorin SIKJM, <a href=\"http://www.leseanimation.ch\" target=\"_blank\" rel=\"noopener\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">www.leseanimation.ch</font></span></a>.&nbsp;Kostenlos, Anmeldung nicht erforderlich. Ausnahme f&uuml;r Gruppen mit max. 4 Kindern: Anmeldung bis am Vorabend direkt in der Bibliothek Basel West. </p>\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=ggg:ProgonEvent.ProgonEvent52&amp;performance=Performance1177\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        GGG Stadtbibliothek Basel West\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    GGG Stadtbibliothek\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">13:30 – 16:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130195&amp;performance=453649\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Ferienkurs: &lsaquo;(Selbst)Portrait zeichnen&rsaquo;</b></font></span></a>\n"
      +
      "                    Mit Laura Fl&uuml;ck (5&ndash;13 J.). Anmeldung an: kunstvermittlung@ag.ch, 062 835 23 31\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130195&amp;performance=453649\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Aargauer Kunsthaus, Aarau\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "               </tbody></table>\n" +
      "    </div>\n" +
      "      <div class=\"category\">\n" +
      "            <div class=\"h2\"><div>Kunst</div></div>\n" +
      "    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">12:15 – 12:45</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130178&amp;performance=453530\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Bild des Monats &ndash; Ren&eacute; Auberjonois</b></font></span></a>\n"
      +
      "                    Musicien aux gants jaunes, 1928. Bildbetrachtung (jeweils Di)\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130178&amp;performance=453530\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Aargauer Kunsthaus, Aarau\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">12:30 – 13:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130047&amp;performance=452622\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Rendez-vous am Mittag: Max Sulzbachner</b></font></span></a>\n" +
      "                    Sulzbi, der Laternenk&ouml;nig. Mit G&eacute;raldine Meyer (Assistenzkuratorin)\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130047&amp;performance=452622\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Kunstmuseum Basel | Hauptbau\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">12:30 – 13:15</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130028&amp;performance=452250\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Curator's Tour: Blick hinter die Kulissen!</b></font></span></a>\n" +
      "                    Mit Olivia Mooser und Jean-Marc Gaillard\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130028&amp;performance=452250\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Museum Tinguely\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">18:30</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130018&amp;performance=452144\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Abendrundgang durch die Ausstellung</b></font></span></a>\n" +
      "                    Mit Philipp Gasser (K&uuml;nstler, Dozent und Mitglied der Fachkommission Kunst BL)\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130018&amp;performance=452144\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Kunsthaus Baselland, Muttenz\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "               </tbody></table>\n" +
      "    </div>\n" +
      "      <div class=\"category\">\n" +
      "            <div class=\"h2\"><div>Literatur</div></div>\n" +
      "    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">07:00 – 21:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129923&amp;performance=451735\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Offline-Basel: &lsaquo;Sie werden lachen, die Bibel!&rsaquo; &ndash; Die Bibel in 11 Tagen</b></font></span></a>\n"
      +
      "                    2.&ndash;12.2. Bibellesung &lsaquo;Lectio continua&rsaquo; (7&ndash;21), Musik und Stille (9.00 12.00, 18.00). Begleitausstellung &lsaquo;Die Bibel &ndash; von gestern bis heute&rsaquo;. Informationen: <a href=\"https://programmzeitung.prog.online/ProgonEvent/ProgonEvent129923?xpage=celements_ajax&amp;ajax_mode=redirectURL&amp;url=http://www.offline-basel.ch\" rel=\"nofollow\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">www.offline-basel.ch</font></span></a>. Eintritt frei\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129923&amp;performance=451735\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Tituskirche\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    Offline (&Ouml;kumenisches Zentrum f&uuml;r Medidation und Seelsorge)\n"
      +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">18:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129882&amp;performance=451626\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Zeitzeugen und Pioniere der Mundartdichtung. Jonas Breitenstein und sein Vorbild Johann Peter Hebel</b></font></span></a>          Vernissage\n"
      +
      "                      &nbsp;&#x25c6;&nbsp;\n" +
      "                    5.2.&ndash;14.6.\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129882&amp;performance=451626\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Dichter- &amp; Stadtmuseum, Liestal\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">19:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129969&amp;performance=451960\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Bachtyar Ali</b></font></span></a>\n" +
      "                    Perwanas Abend\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129969&amp;performance=451960\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Literaturhaus Basel\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "               </tbody></table>\n" +
      "    </div>\n" +
      "      <div class=\"category\">\n" +
      "            <div class=\"h2\"><div>Musik, Konzerte</div></div>\n" +
      "    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">18:00</div></td>\n" + EXCEPTION_CAUSE_5_LINES1 +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent128127&amp;performance=453165\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Atlantis\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">19:30</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent125048&amp;performance=420670\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Kammermusik Basel: Preistr&auml;ger</b></font></span></a>\n" +
      "                    Banff International String Quartet Competition. Vvk: <a href=\"https://programmzeitung.prog.online/ProgonEvent/ProgonEvent125048?xpage=celements_ajax&amp;ajax_mode=redirectURL&amp;url=http://www.starticket.ch\" rel=\"nofollow\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">www.starticket.ch</font></span></a>\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent125048&amp;performance=420670\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Oekolampad\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    Kammermusik Basel\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">19:30 – 21:30</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129614&amp;performance=449141\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Supertramp &ndash; The Tribute of the century</b></font></span></a>\n"
      +
      "                    Rockband\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129614&amp;performance=449141\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Musical Theater Basel\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    Euroconcert Suisse SARL\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">20:30 – 22:45</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129721&amp;performance=450973\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Alex Hendriksen Quartet</b></font></span></a>\n" +
      "                    Alex Hendriksen (ts), Florian Favre (p), Fabian Gisler (b), Paul Amereller (dr). Reservation: 061 263 33 41, office@birdseye.ch\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129721&amp;performance=450973\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        The Bird's Eye Jazz Club\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "               </tbody></table>\n" +
      "    </div>\n" +
      "      <div class=\"category\">\n" +
      "                 <div class=\"h2\"><div>Sounds &amp; Floors</div></div>\n" +
      "    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">22:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130355&amp;performance=454461\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Instant Rave</b></font></span></a>\n" +
      "                    w/ Nosyb&eacute;\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130355&amp;performance=454461\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Das Viertel - Klub, M&uuml;nchenstein\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "               </tbody></table>\n" +
      "    </div>\n" +
      "        <div class=\"category\">\n" +
      "            <div class=\"h2\"><div>Theater</div></div>\n" +
      "    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">19:30</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent125833&amp;performance=427091\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Antigone</b></font></span></a>\n" +
      "                    Von Sophokles. Inszenierung Deborah Epstein.  Kooperation mit Theater Orchester Biel Solothurn (TOBS). Vvk: 061 702 00 83, ticket@neuestheater.ch\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent125833&amp;performance=427091\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Neuestheater.ch, Dornach\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">19:30</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent128500&amp;performance=442003\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Mim&ouml;sli &ndash; Mir schiessen is uff dr Mond</b></font></span></a>\n"
      +
      "                    Vorfasnachtsveranstaltung. 16.1.&ndash;21.2. Vvk: 061 691 44 46\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent128500&amp;performance=442003\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        H&auml;bse Theater\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    Basler Fasnacht\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">20:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent123661&amp;performance=414087\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Glaibasler Charivari</b></font></span></a>\n" +
      "                    Vorfasnachtsveranstaltung. Regie Lucien St&ouml;cklin. 1.&ndash;15.2.\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent123661&amp;performance=414087\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Volkshaus Basel\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    Basler Fasnacht\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">20:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent125289&amp;performance=422628\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Pfyfferli</b></font></span></a>\n" +
      "                    Hauseigene Vorfasnachtsveranstaltung. 11.1.&ndash;10.3.\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent125289&amp;performance=422628\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Fauteuil\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    Basler Fasnacht\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">20:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129074&amp;performance=451458\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Wiederauferstehung der V&ouml;gel</b></font></span></a>          Einführung 19.30\n"
      +
      "                      &nbsp;&#x25c6;&nbsp;\n" +
      "                    Von Thiemo Strutzenberger basierend auf &lsaquo;Tropenliebe&rsaquo; von Bernhard C. Sch&auml;r (UA, Auftragswerk). Inszenierung Katrin Hammerl\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129074&amp;performance=451458\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Theater Basel, Kleine B&uuml;hne\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "               </tbody></table>\n" +
      "    </div>\n" +
      "      <div class=\"category\">\n" +
      "            <div class=\"h2\"><div>Diverses</div></div>\n" +
      "    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">10:00 – 17:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent128555&amp;performance=450534\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Sportlich durch die R&ouml;merzeit</b></font></span></a>\n" +
      "                    Auf Postenjagd durch die R&ouml;merstadt\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent128555&amp;performance=450534\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Augusta Raurica, Augst\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">11:00 – 13:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent124900&amp;performance=452150\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Oh, a line of types</b></font></span></a>\n" +
      "                    Linotype Setz- und Giessmaschine in Betrieb\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent124900&amp;performance=452150\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Basler Papierm&uuml;hle\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">13:00 – 15:30</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent107330&amp;performance=452160\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Laborpapiermaschine</b></font></span></a>\n" +
      "                    In Betrieb\n" +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent107330&amp;performance=452160\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Basler Papierm&uuml;hle\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">17:30 – 18:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130054&amp;performance=452624\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Museumsbar: Wissen kompakt</b></font></span></a>          Barbetrieb bis 19:30\n"
      +
      "                      &nbsp;&#x25c6;&nbsp;\n" +
      "                    Farbenpr&auml;chtige Seidenb&auml;nder. Ein Blick in die Musterb&uuml;cher der Firma Seiler. Mit Therese Schaltenbrand (Kuratorin Sammlung Europ&auml;ische Ethnologie)\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130054&amp;performance=452624\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Museum.BL, Liestal\n" +
      "                          </font></span></a>\n" +
      "\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">18:30 – 19:45</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129913&amp;performance=451705\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>How Swiss politics works</b></font></span></a>\n" +
      "                    Diccon Bewes (travel writer). Reihe: Understanding Switzerland. Einzeleintritt m&ouml;glich. Anmeldung: 061 269 86 66, <a href=\"https://programmzeitung.prog.online/ProgonEvent/ProgonEvent129913?xpage=celements_ajax&amp;ajax_mode=redirectURL&amp;url=http://www.vhsbb.ch\" rel=\"nofollow\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">www.vhsbb.ch</font></span></a>\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent129913&amp;performance=451705\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Uni Basel, Kollegienhaus\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    Volkshochschule beider Basel (VHSBB)\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">19:00</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130271&amp;performance=454234\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>ZischBar</b></font></span></a>\n" +
      "                    Am Dienstag wird die KaBar zur ZischBar. <a href=\"https://programmzeitung.prog.online/ProgonEvent/ProgonEvent130271?xpage=celements_ajax&amp;ajax_mode=redirectURL&amp;url=http://www.gaybasel.org\" rel=\"nofollow\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">www.gaybasel.org</font></span></a>\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130271&amp;performance=454234\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        KaBar\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    gaybasel.ch\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "\n" +
      "      <tr>\n" +
      "        <td><div class=\"td\">19:30</div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                      <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130099&amp;performance=453229\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                    <b>Basler Zirkel: Sondeng&auml;nger, Heimatforscher und Raubgr&auml;ber &ndash;  Eine Bilanz</b></font></span></a>\n"
      +
      "                    Vortrag von Peter Henrich (Generaldirektion Kulturelles Erbe Rheinland-Pfalz). H&ouml;rsaal 118\n"
      +
      "                            </div></td>\n" +
      "        <td><div class=\"td\">\n" +
      "                                                        <a href=\"https://programmzeitung.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent130099&amp;performance=453229\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">\n"
      +
      "                        Uni Basel, Kollegienhaus\n" +
      "                          </font></span></a>\n" +
      "                                                        &nbsp;&#x25c6;&nbsp;\n" +
      "                    Basler Zirkel f&uuml;r Ur- und Fr&uuml;hgeschichte\n" +
      "                            </div></td>\n" +
      "      </tr>\n" +
      "               </tbody></table>\n" +
      "    </div>\n" +
      "\n" +
      "<p>&nbsp;</p>\n" +
      "</div>\n" +
      "\n" +
      "<p class=\"footer\"><p class=\"unsubscribe\">Um auf <span class=\"link\">{EMAIL}</span> die Tagesagenda in Zukunft nicht mehr zu erhalten können Sie sich <span class=\"link\"><a href=\"{UNSUBSCRIBE}\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">hier abmelden</font></span></a></span>.</p><br /><p class=\"impressum\">&copy; <span class=\"contact\"><a href=\"https://www.google.com/maps/place/ProgrammZeitung+Verlags+AG/@47.5489496,7.5853905,17z/data=!3m1!4b1!4m5!3m4!1s0x4791b9adcd1a0c85:0x493c0a4ad068e83f!8m2!3d47.5489496!4d7.5875845\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">ProgrammZeitung Verlags AG, Viaduktstrasse 8, 4051 Basel, Schweiz</font></span></a></span> &#x25c6; <span class=\"link\"><a href=\"https://programmzeitung.ch/Impressum\" style=\"color: #015470; text-decoration: none;\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">Impressum</font></span></a></span></p></p></td></tr></tbody></table></center>\n"
      +
      "</body></html>";

  static final String LINE_NR2 = "692";

  static final String MESSAGE2 = "The element type \"div\" must be terminated by the matching "
      + "end-tag \"</div>\". Nested exception: The element type \"div\" must be terminated by the "
      + "matching end-tag \"</div>\".";

  static final String STACK_TRACE2 = "com.celements.cleverreach.exception.CssInlineException: CSS could not be inlined.\n"
      +
      "        at com.celements.cleverreach.util.DefaultCssInliner.inline(DefaultCssInliner.java:60)\n"
      +
      "        at com.celements.cleverreach.util.DefaultCssInliner.inline(DefaultCssInliner.java:42)\n"
      +
      "        at com.celements.cleverreach.MailingConfig.getContentHtmlCssInlined(MailingConfig.java:127)\n"
      +
      "        at com.celements.cleverreach.CleverReachRest.buildMailing(CleverReachRest.java:153)\n"
      +
      "        at com.celements.cleverreach.CleverReachRest.updateMailingInternal(CleverReachRest.java:128)\n"
      +
      "        at com.celements.cleverreach.CleverReachRest.updateMailingRehearsal(CleverReachRest.java:117)\n"
      +
      "        at ch.programmonline.proz.TagesagendaCleverReachUpdateRehearsalJob.executeJob(TagesagendaCleverReachUpdateRehearsalJob.java:31)\n"
      +
      "        at com.celements.scheduler.job.AbstractJob.execute(AbstractJob.java:80)\n" +
      "        at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n" +
      "        at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:525)\n" +
      "Caused by: org.dom4j.DocumentException: Error on line " + LINE_NR2 + " of document  : "
      + MESSAGE2 + "\n" +
      "        at org.dom4j.io.SAXReader.read(SAXReader.java:482)\n" +
      "        at com.celements.cleverreach.util.DefaultCssInliner.prepareInput(DefaultCssInliner.java:69)\n"
      +
      "        at com.celements.cleverreach.util.DefaultCssInliner.inline(DefaultCssInliner.java:52)\n"
      +
      "        ... 9 more";

  static final String STACK_TRACE3 = "com.celements.cleverreach.exception.CssInlineException: CSS could not be inlined.\n"
      +
      "        at com.celements.cleverreach.util.DefaultCssInliner.inline(DefaultCssInliner.java:60)\n"
      +
      "        at com.celements.cleverreach.util.DefaultCssInliner.inline(DefaultCssInliner.java:42)\n"
      +
      "        at com.celements.cleverreach.MailingConfig.getContentHtmlCssInlined(MailingConfig.java:127)\n"
      +
      "        at com.celements.cleverreach.CleverReachRest.buildMailing(CleverReachRest.java:153)\n"
      +
      "        at com.celements.cleverreach.CleverReachRest.updateMailingInternal(CleverReachRest.java:128)\n"
      +
      "        at com.celements.cleverreach.CleverReachRest.updateMailingRehearsal(CleverReachRest.java:117)\n"
      +
      "        at ch.programmonline.proz.TagesagendaCleverReachUpdateRehearsalJob.executeJob(TagesagendaCleverReachUpdateRehearsalJob.java:31)\n"
      +
      "        at com.celements.scheduler.job.AbstractJob.execute(AbstractJob.java:80)\n" +
      "        at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n" +
      "        at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:525)\n" +
      "        ... 9 more";
  /*
   * String EXCEPTION_CAUSE2 =
   * "                    <b>ABGESAGT: &amp; Vortrag der NGiB</b></font></span></a>\n" +
   * "                    Eintritt frei. <div>&amp; Infos: <a href=\"http://programmzeitung.progdev.sneakapeek.ch:8015/ProgonEvent/ProgonEvent138884?xpage=celements_ajax&amp;ajax_mode=redirectURL&amp;url=http://www.ngib.ch\" rel=\"nofollow\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">www.ngib.ch</font></span></a>\n"
   * +
   * "                            </div></td>\n"+
   * "        <td><div class=\"td\">\n"+
   * "                                                        <a href=\"http://programmzeitung.cel.sneakapeek.ch/app/Veranstaltung?col=Content.Webseite&amp;event=programmzeitung:ProgonEvent.ProgonEvent138884&amp;performance=454596\" class=\"veranstaltung\" target=\"_blank\"><span style=\"color: #015470; text-decoration: none !important;\"><font color=\"#015470\" style=\"border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; text-decoration: none !important;\">"
   * ;
   */
}
