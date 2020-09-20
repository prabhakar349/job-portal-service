package com.ceitechs.dproz.jobportalservice.domain;

import com.ceitechs.dproz.jobportalservice.adapter.datastore.UserRepository;
import com.ceitechs.dproz.jobportalservice.utils.DprozUtility;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Mono<User> addUser(User user) {
    return Mono.just(user.getEmail())
      .flatMap(userRepository::findByEmailIgnoreCase)
      .flatMap(existingUser -> Mono.just(Boolean.TRUE))
      .defaultIfEmpty(Boolean.FALSE)
      .flatMap(userAlreadyExists -> {
        if (!userAlreadyExists) {
          user.setUserReferenceId(DprozUtility.generateIdAsString());
          user.setPassword(passwordEncoder.encode(user.getPassword()));
          return userRepository.save(user);
        } else {
          return Mono.error(new RuntimeException("user.AlreadyExists"));
        }
      });
  }
}
