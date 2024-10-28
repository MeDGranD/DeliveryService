package ru.medgrand.service.Infrastructure;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Model.Order;

public interface IOrderRepository {

    public Flux<Order> getAllOrders();
    public Flux<Order> getPaginationOrder(int skip, int limit);
    public Flux<Order> getAllOrdersByUser(String username);
    public Mono<Order> getOrderById(long id);

    public Mono<Order> createOrder(Order order);
    public void deleteOrder(Order order);
    public void updateOrder(Order order);

}
