package com.ceitechs.dproz.jobportalservice.adapter.api.error;

import org.springframework.http.HttpStatus;

public class DprozRestException extends RuntimeException {

  private final String messageId;

  private final HttpStatus httpStatus;

  private String[] messageArgs = null;

  public DprozRestException(String messageId, HttpStatus status) {
    super( messageId );
    this.messageId = messageId;
    this.httpStatus = status;
  }

  public DprozRestException(String messageId, HttpStatus status, String... messageArgs) {
    super( messageId );
    this.messageId = messageId;
    this.httpStatus = status;
    this.messageArgs = messageArgs;
  }

  public DprozRestException(String messageId, HttpStatus status, Throwable throwable,
      String... messageArgs) {
    super( messageId, throwable );
    this.messageId = messageId;
    this.httpStatus = status;
    this.messageArgs = messageArgs;

  }

  public DprozRestException(String messageId, HttpStatus status, Throwable throwable) {
    super( messageId, throwable );
    this.messageId = messageId;
    this.httpStatus = status;
  }

  public String getMessageId() {
    return messageId;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public String[] getMessageArgs() {
    return messageArgs;
  }

  public void setMessageArgs(String[] messageArgs) {
    this.messageArgs = messageArgs;
  }

}
