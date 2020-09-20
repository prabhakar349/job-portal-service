package com.ceitechs.dproz.jobportalservice.adapter.api;


import com.ceitechs.dproz.jobportalservice.adapter.DprozMessageResponse;
import com.ceitechs.dproz.jobportalservice.adapter.api.error.DprozRestException;
import com.ceitechs.dproz.jobportalservice.domain.User;
import com.ceitechs.dproz.jobportalservice.domain.UserService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/api/dproz/users")
@RestController
@CrossOrigin("*")
public class UserController {

  private static final String EXPECTED_EXCEPTION = "Exception.unexpected";

  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<DprozMessageResponse>> addUser(
    @Valid @RequestBody Mono<User> userRequest) {
    return userRequest
      .flatMap(user ->
        userService
          .addUser(user)
          .map(addedUser -> new ResponseEntity<>(DprozMessageResponse
            .of("Successfully registered. An email with instruction to verify the account is sent to : "
                + addedUser.getEmail(), 201, "user created",
              addedUser.getUserReferenceId()), HttpStatus.CREATED))
          .onErrorMap(exception -> !(exception instanceof DprozRestException)
            , exception -> new DprozRestException(EXPECTED_EXCEPTION,
              HttpStatus.INTERNAL_SERVER_ERROR, exception)));
  }

}
