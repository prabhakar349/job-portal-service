package com.ceitechs.dproz.jobportalservice.domain;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Name {

  @NotNull
  private String firstName;

  @NotNull
  private String lastName;

}
