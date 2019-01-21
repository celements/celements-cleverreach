package com.celements.cleverreach;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;

@Immutable
public class MailingConfig {

  public static class Builder {

    private String id;
    private String subject;
    private String contentHtml;
    private String contentPlain;

    public Builder setId(@NotNull String id) {
      this.id = !isNullOrEmpty(id) ? id : null;
      return this;
    }

    public Builder setSubject(@NotNull String subject) {
      this.subject = !isNullOrEmpty(subject) ? subject : null;
      return this;
    }

    public Builder setContentHtml(@NotNull String contentHtml) {
      this.contentHtml = !isNullOrEmpty(contentHtml) ? contentHtml : null;
      return this;
    }

    public Builder setContentPlain(@NotNull String contentPlain) {
      this.contentPlain = !isNullOrEmpty(contentPlain) ? contentPlain : null;
      return this;
    }

    public MailingConfig build() {
      return new MailingConfig(id, subject, contentHtml, contentPlain);
    }

  }

  private final String id;
  private final String subject;
  private final String contentHtml;
  private final String contentPlain;

  private MailingConfig(@NotNull String id, String subject, String contentHtml,
      String contentPlain) {
    checkArgument(!isNullOrEmpty(id));
    this.id = id;
    this.subject = subject;
    this.contentHtml = contentHtml;
    this.contentPlain = contentPlain;
  }

  public String getId() {
    return id;
  }

  public String getSubject() {
    return subject;
  }

  public String getContentHtml() {
    return contentHtml;
  }

  public String getContentPlain() {
    return contentPlain;
  }

}
