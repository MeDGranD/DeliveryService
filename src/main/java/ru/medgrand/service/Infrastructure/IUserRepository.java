package ru.medgrand.service.Infrastructure;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Model.User;

public interface IUserRepository {

    public Flux<User> getAllUsers();
    public Flux<User> getPaginationUsers(int skip, int limit);
    public Mono<User> getUserById(long id);
    public Mono<User> getUserByName(String name);

    public Mono<User> createUser(User user);
    public void deleteUser(User user);
    public void updateUser(User user);

}
