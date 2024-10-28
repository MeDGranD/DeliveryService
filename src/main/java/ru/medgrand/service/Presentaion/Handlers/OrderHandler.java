package ru.medgrand.service.Presentaion.Handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Application.OrderService;
import ru.medgrand.service.Model.Order;

@Component
public class OrderHandler {

    @Autowired
    OrderService orderService;

    public Mono<ServerResponse> getPaginationOrders(ServerRequest request){

        int skip = Integer.parseInt(request.queryParam("skip").orElse("0"));
        int limit = Integer.parseInt(request.queryParam("limit").orElse(String.valueOf(Integer.MAX_VALUE)));

        Flux<Order> orders = orderService.getPaginationOrders(skip, limit);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(orders, Order.class);

    }

    public Mono<ServerResponse> getAllOrdersByUsername(ServerRequest request){

        String username = request.pathVariable("username");

        Flux<Order> orders = orderService.getAllOrdersByUsername(username);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(orders, Order.class);

    }

    public Mono<ServerResponse> getOrderById(ServerRequest request){

        long id = Long.parseLong(request.pathVariable("id"));

        Mono<Order> order = orderService.getOrderById(id);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(order, Order.class);

    }

    public Mono<ServerResponse> createOrder(ServerRequest request){

        Mono<Order> order = request.bodyToMono(Order.class);

        return order.flatMap(orderService::createOrder)
                .hasElement()
                .flatMap(aBoolean -> {
                   if(aBoolean){
                       return ServerResponse
                               .ok()
                               .contentType(MediaType.APPLICATION_JSON)
                               .body(request.bodyToMono(Order.class), Order.class);
                   }
                   else{
                       return ServerResponse
                               .badRequest()
                               .build();
                   }
                });

    }

    public Mono<ServerResponse> updateOrder(ServerRequest request){

        Mono<Order> order = request.bodyToMono(Order.class);

        return order.flatMap(orderService::updateOrder)
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

        Order deleteOrder = new Order();
        deleteOrder.setId(id);

        return Mono.just(deleteOrder)
                .flatMap(orderService::deleteOrder)
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
