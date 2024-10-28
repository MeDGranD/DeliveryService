package ru.medgrand.service.Application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Infrastructure.IOrderRepository;
import ru.medgrand.service.Model.Order;

@Service
public class OrderService {

    private final IOrderRepository orderRepository;

    @Autowired
    public OrderService(IOrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public Flux<Order> getAllOrders(){
        return orderRepository.getAllOrders();
    }

    public Flux<Order> getPaginationOrders(int skip, int limit){
        return orderRepository.getPaginationOrder(skip, limit);
    }

    public Flux<Order> getAllOrdersByUsername(String username){
        return orderRepository.getAllOrdersByUser(username);
    }

    public Mono<Order> getOrderById(long id){
        return orderRepository.getOrderById(id);
    }

    public Mono<Order> createOrder(Order order){
        return orderRepository.createOrder(order);
    }

    public Mono<Order> updateOrder(Order order){
        return orderRepository.updateOrder(order);
    }

    public Mono<Order> deleteOrder(Order order){
        return orderRepository.deleteOrder(order);
    }

}
