package ru.medgrand.service.Presentaion.Handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Application.UserService;
import ru.medgrand.service.Model.Order;
import ru.medgrand.service.Model.User;

@Component
public class UserHandler {

    @Autowired
    UserService userService;

    public Mono<ServerResponse> getPaginationUser(ServerRequest request){

        int skip = Integer.parseInt(request.queryParam("skip").orElse("0"));
        int limit = Integer.parseInt(request.queryParam("limit").orElse(String.valueOf(Integer.MAX_VALUE)));

        Flux<User> users = userService.getPaginationUsers(skip, limit);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(users, User.class);

    }

    public Mono<ServerResponse> getUserById(ServerRequest request){

        long id = Long.parseLong(request.pathVariable("id"));

        Mono<User> user = userService.getUserById(id);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user, User.class);

    }

    public Mono<ServerResponse> getUserByUsername(ServerRequest request){

        String username = request.pathVariable("username");

        Mono<User> user = userService.getUserByUserName(username);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user, User.class);

    }

    public Mono<ServerResponse> createOrder(ServerRequest request){

        Mono<User> user = request.bodyToMono(User.class);

        return user.flatMap(userService::createUser)
                .hasElement()
                .flatMap(aBoolean -> {
                    if(aBoolean){
                        return ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request.bodyToMono(User.class), User.class);
                    }
                    else{
                        return ServerResponse
                                .badRequest()
                                .build();
                    }
                });

    }

    public Mono<ServerResponse> updateOrder(ServerRequest request){

        Mono<User> user = request.bodyToMono(User.class);

        return user.flatMap(userService::updateUser)
                .hasElement()
                .flatMap(aBoolean -> {
                    if(aBoolean){
                        return ServerResponse
                                .ok()
                                .build();
                    }
                    else{
                        return ServerResponse
                                .badRequest()
                                .build();
                    }
                });

    }

    public Mono<ServerResponse> deleteOrder(ServerRequest request){

        long id = Long.parseLong(request.pathVariable("id"));

        User deleteUser = new User();
        deleteUser.setId(id);

        return Mono.just(deleteUser)
                .flatMap(userService::deleteUser)
                .hasElement()
                .flatMap(aBoolean -> {
                    if(aBoolean){
                        return ServerResponse
                                .ok()
                                .build();
                    }
                    else{
                        return ServerResponse
                                .badRequest()
                                .build();
                    }
                });

    }

}
