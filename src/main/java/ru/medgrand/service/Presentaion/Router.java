package ru.medgrand.service.Presentaion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.medgrand.service.Application.DishService;
import ru.medgrand.service.Presentaion.Handlers.DishHandler;

@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> route(DishHandler dishHandler){
        return RouterFunctions.route()
                .GET("/dishes/{id}", dishHandler::getDishById)
                .GET("/dishes", dishHandler::getPaginationDishes)
                .GET("/dishes/named/{name}", dishHandler::getDishByName)
                .GET("/orders/{id}/dishes", dishHandler::getAllDishesByOrderId)
                .POST("/dishes", dishHandler::createDish)
                .PUT("/dishes", dishHandler::updateDish)
                .DELETE("dishes/{id}", dishHandler::deleteDish)
                .build();
    }

}
