package com.celements.cleverreach.exception;

import java.io.IOException;

import javax.ws.rs.core.Response;

public class CleverReachRequestFailedException extends IOException {

  private static final long serialVersionUID = 1L;

  private Response response;

  public CleverReachRequestFailedException(String message, Response response) {
    super(message);
    this.response = response;
  }

  public Response getResponse() {
    return response;
  }

  @Override
  public void printStackTrace() {
    System.err.println("Response status|length: [" + ((response != null) ? response.getStatus()
        + "|" + response.getLength() : "null"));
    super.printStackTrace(System.err);
  }
}
