package com.ceitechs.dproz.jobportalservice.adapter;

import lombok.Data;

@Data
public class DprozMessageResponse {

  private String detail;

  private Integer status;

  private String developerMessage;

  private String userReferenceId;

  public static DprozMessageResponse of(String detail, int status, String developerMessage,
      String userReferenceId) {
    DprozMessageResponse messageResponse = new DprozMessageResponse();
    messageResponse.setStatus( status );
    messageResponse.setDetail( detail );
    messageResponse.setDeveloperMessage( developerMessage );
    messageResponse.setUserReferenceId( userReferenceId );
    return messageResponse;
  }

}
