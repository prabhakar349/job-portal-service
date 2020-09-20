package com.ceitechs.dproz.jobportalservice.domain;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user")
public class User {

  @Id
  private String userReferenceId;

  @NotNull
  private String email;

  @NotNull
  private String password;

  @Valid
  private Name name;

  private UserType type;

}
