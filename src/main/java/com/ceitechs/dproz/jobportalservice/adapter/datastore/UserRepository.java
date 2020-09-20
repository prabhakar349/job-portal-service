package com.ceitechs.dproz.jobportalservice.adapter.datastore;

import com.ceitechs.dproz.jobportalservice.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

  Mono<User> findByEmailIgnoreCase(String email);
}
