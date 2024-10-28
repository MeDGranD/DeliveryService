package ru.medgrand.service.Presentaion.Handlers;

import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Application.DishService;
import ru.medgrand.service.Model.Dish;

@Component
public class DishHandler {

    @Autowired
    DishService dishService;

    public Mono<ServerResponse> getDishById(ServerRequest request){

        long dishId = Long.parseLong(request.pathVariable("id"));

        Mono<Dish> dish = dishService.getDishById(dishId);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dish, Dish.class);

    }

    public Mono<ServerResponse> getPaginationDishes(ServerRequest request){

        int skip = Integer.parseInt(request.queryParam("skip").orElse("0"));
        int limit = Integer.parseInt(request.queryParam("limit").orElse(String.valueOf(Integer.MAX_VALUE)));

        Flux<Dish> dishes = dishService.getPaginationDishes(skip, limit);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dishes, Dish.class);

    }

    public Mono<ServerResponse> getDishByName(ServerRequest request){

        String name = request.pathVariable("name");

        Mono<Dish> dish = dishService.getDishByName(name);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dish, Dish.class);

    }

    public Mono<ServerResponse> getAllDishesByOrderId(ServerRequest request){

        long orderId = Long.parseLong(request.pathVariable("id"));

        Flux<Dish> dishes = dishService.getAllDishesByOrderId(orderId);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dishes, Dish.class);

    }

    public Mono<ServerResponse> createDish(ServerRequest request){

        Mono<Dish> dish = request.bodyToMono(Dish.class);
        dish = dish.flatMap(dishService::createDish);

        Mono<Dish> finalDish = dish;
        return dish.hasElement()
                .flatMap(aBoolean -> {
                    if(aBoolean){
                        return ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(finalDish, Dish.class);
                    }
                    else{
                        return ServerResponse
                                .badRequest()
                                .build();
                    }
                });

    }

    public Mono<ServerResponse> updateDish(ServerRequest request){

        Mono<Dish> dish = request.bodyToMono(Dish.class);

        dish.flatMap(dishService::updateDish);

        return dish.hasElement()
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

    public Mono<ServerResponse> deleteDish(ServerRequest request){

        long id = Long.parseLong(request.pathVariable("id"));

        Dish dish = new Dish();
        dish.setId(id);

        Mono<Dish> deleteDish = Mono.just(dish);
        deleteDish.flatMap(dishService::deleteDish);

        return deleteDish.hasElement()
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
