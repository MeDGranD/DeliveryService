package ru.medgrand.service.Application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Infrastructure.IUserRepository;
import ru.medgrand.service.Model.User;

@Service
public class UserService {

    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Flux<User> getAllUsers(){
        return userRepository.getAllUsers();
    }

    public Flux<User> getPaginationUsers(int skip, int limit){
        return userRepository.getPaginationUsers(skip, limit);
    }

    public Mono<User> getUserById(long id){
        return userRepository.getUserById(id);
    }

    public Mono<User> getUserByUserName(String username){
        return userRepository.getUserByName(username);
    }

    public Mono<User> createUser(User user){
        return userRepository.createUser(user);
    }

    public void deleteUser(User user){
        userRepository.deleteUser(user);
    }

    public void updateUser(User user){
        userRepository.updateUser(user);
    }

}
