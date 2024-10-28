package ru.medgrand.service.Presentaion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.medgrand.service.Application.DishService;

@Configuration
public class Router {

    private final DishService dishService;

    @Autowired
    public Router(DishService dishService){
        this.dishService = dishService;
    }

    @Bean
    public RouterFunction<ServerResponse> route(){
        return RouterFunctions.route()
                .GET("/", req -> ServerResponse.ok().build())
                .build();
    }

}
