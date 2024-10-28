package ru.medgrand.service.Infrastructure.Implementation.Postgre;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Infrastructure.IDishRepository;
import ru.medgrand.service.Infrastructure.IOrderRepository;
import ru.medgrand.service.Infrastructure.IUserRepository;
import ru.medgrand.service.Model.Dish;
import ru.medgrand.service.Model.Order;
import ru.medgrand.service.Model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;

@Repository
public class OrderRepository implements IOrderRepository {

    private final R2dbcEntityTemplate r2dbcTemplate;
    private final BiFunction<Row, RowMetadata, parsedOrder> orderMapper;
    private final IDishRepository dishRepository;
    private final IUserRepository userRepository;

    @Autowired
    public OrderRepository(R2dbcEntityTemplate r2dbcEntityTemplate,
                           IDishRepository dishRepository,
                           IUserRepository userRepository){

        this.r2dbcTemplate = r2dbcEntityTemplate;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;

        orderMapper = (row, rowMetadata) -> {
            parsedOrder returnOrder = new parsedOrder();
            returnOrder.setId(row.get("id", Long.class));
            returnOrder.setAddress(row.get("address", String.class));
            returnOrder.setDeliveryDate(row.get("deliveryDate", LocalDateTime.class));
            returnOrder.setCreationDate(row.get("creationDate", LocalDateTime.class));
            returnOrder.setUserId(row.get("user_id", Long.class));

            return returnOrder;
        };

    }

    @Override
    public Flux<Order> getAllOrders() {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select *
                            from orders
                            """)
                    .map(orderMapper)
                    .all()
                    .flatMap(parsedOrder -> {

                        Order order = new Order();
                        order.setId(parsedOrder.getId());
                        order.setAddress(parsedOrder.getAddress());
                        order.setDeliveryDate(parsedOrder.getDeliveryDate());
                        order.setCreationDate(parsedOrder.getCreationDate());

                        Mono<User> user = this.userRepository.getUserById(parsedOrder.getUserId());
                        Flux<Dish> dishes = this.dishRepository.getAllDishesByOrderId(order.getId());

                        user.subscribe(order::setUser);
                        dishes.collectList().subscribe(order::setDishes);

                        return user.thenMany(dishes).then(Mono.just(order));

                    });
        }
        catch (NullPointerException exp){
            return Flux.empty();
        }
    }

    @Override
    public Flux<Order> getPaginationOrder(int skip, int limit) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select *
                            from orders
                            limit""" + " " + Integer.toString(limit) +
                            " offset " + Integer.toString(skip))
                    .map(orderMapper)
                    .all()
                    .flatMap(parsedOrder -> {

                        Order order = new Order();
                        order.setId(parsedOrder.getId());
                        order.setAddress(parsedOrder.getAddress());
                        order.setDeliveryDate(parsedOrder.getDeliveryDate());
                        order.setCreationDate(parsedOrder.getCreationDate());

                        Mono<User> user = this.userRepository.getUserById(parsedOrder.getUserId());
                        Flux<Dish> dishes = this.dishRepository.getAllDishesByOrderId(order.getId());

                        user.subscribe(order::setUser);
                        dishes.collectList().subscribe(order::setDishes);

                        return user.thenMany(dishes).then(Mono.just(order));

                    });
        }
        catch (NullPointerException exp){
            return Flux.empty();
        }
    }

    @Override
    public Flux<Order> getAllOrdersByUser(String username) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select *
                            from orders
                            join users on orders.user_id = users.id
                            where users.username =""" + "'" + username + "'")
                    .map(orderMapper)
                    .all()
                    .flatMap(parsedOrder -> {

                        Order order = new Order();
                        order.setId(parsedOrder.getId());
                        order.setAddress(parsedOrder.getAddress());
                        order.setDeliveryDate(parsedOrder.getDeliveryDate());
                        order.setCreationDate(parsedOrder.getCreationDate());

                        Mono<User> user = this.userRepository.getUserById(parsedOrder.getUserId());
                        Flux<Dish> dishes = this.dishRepository.getAllDishesByOrderId(order.getId());

                        user.subscribe(order::setUser);
                        dishes.collectList().subscribe(order::setDishes);

                        return user.thenMany(dishes).then(Mono.just(order));

                    });
        }
        catch (NullPointerException exp){
            return Flux.empty();
        }
    }

    @Override
    public Mono<Order> getOrderById(long id) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select *
                            from orders
                            where orders.id = """ + Long.toString(id))
                    .map(orderMapper)
                    .first()
                    .flatMap(parsedOrder -> {

                        Order order = new Order();
                        order.setId(parsedOrder.getId());
                        order.setAddress(parsedOrder.getAddress());
                        order.setDeliveryDate(parsedOrder.getDeliveryDate());
                        order.setCreationDate(parsedOrder.getCreationDate());

                        Mono<User> user = this.userRepository.getUserById(parsedOrder.getUserId());
                        Flux<Dish> dishes = this.dishRepository.getAllDishesByOrderId(order.getId());

                        user.subscribe(order::setUser);
                        dishes.collectList().subscribe(order::setDishes);

                        return user.thenMany(dishes).then(Mono.just(order));

                    });
        }
        catch (NullPointerException exp){
            return Mono.empty();
        }
    }

    @Override
    public Mono<Order> createOrder(Order order) {
        return this.getOrderById(order.getId())
                .hasElement()
                .flatMap(bool -> {
                    if(bool){
                        return Mono.empty();
                    }
                    else{
                        return this.userRepository.getUserById(order.getUser().getId())
                                .hasElement()
                                .flatMap(userBool -> {
                                    if(userBool){
                                        r2dbcTemplate.getDatabaseClient()
                                                .sql("""
                                                        insert into orders (id, user_id, address, creationDate, deliveryDate)
                                                        values (%d, %d, '%s', '%s', '%s')""".formatted
                                                        (
                                                                order.getId(),
                                                                order.getUser().getId(),
                                                                order.getAddress(),
                                                                order.getCreationDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                                                                order.getDeliveryDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))
                                                        )
                                                ).then().subscribe();
                                        order.getDishes()
                                                .forEach(dish -> {
                                                    r2dbcTemplate.getDatabaseClient()
                                                            .sql("""
                                                                    insert into orders_dishes (order_id, dish_id, quantity)
                                                                    values (%d, %d, 1)""".formatted(order.getId(), dish.getId())).then().subscribe();
                                                });
                                        return Mono.just(order);
                                    }
                                    else{
                                        return Mono.empty();
                                    }
                                });
                    }
                });
    }

    @Override
    public Mono<Order> deleteOrder(Order order) {
        return this.getOrderById(order.getId())
                .hasElement()
                .flatMap(bool -> {
                    if(bool){
                        r2dbcTemplate.getDatabaseClient()
                                .sql("""
                                        delete from orders
                                        where id = %d""".formatted(order.getId())).then().subscribe();
                        r2dbcTemplate.getDatabaseClient()
                                .sql("""
                                        delete from orders_dishes
                                        where order_id = %d""".formatted(order.getId())).then().subscribe();
                        return Mono.just(order);
                    }
                    else{
                        return Mono.empty();
                    }
                });
    }

    @Override
    public Mono<Order> updateOrder(Order order) {
        return this.getOrderById(order.getId())
                .hasElement()
                .flatMap(bool -> {
                   if(bool){
                       r2dbcTemplate.getDatabaseClient()
                               .sql("""
                                       update orders
                                       set user_id = %d,
                                            address = '%s',
                                            creationDate = '%s',
                                            deliveryDate = '%s',
                                       where id = %d""".formatted
                                       (
                                               order.getUser().getId(),
                                               order.getAddress(),
                                               order.getCreationDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                                               order.getDeliveryDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                                               order.getId()
                                       )
                               ).then().subscribe();
                       r2dbcTemplate.getDatabaseClient()
                               .sql("""
                                        delete from orders_dishes
                                        where order_id = %d""".formatted(order.getId())).then().subscribe();
                       order.getDishes()
                               .forEach(dish -> {
                                   r2dbcTemplate.getDatabaseClient()
                                           .sql("""
                                                                    insert into orders_dishes (order_id, dish_id, quantity)
                                                                    values (%d, %d, 1)""".formatted(order.getId(), dish.getId())).then().subscribe();
                               });

                       return Mono.just(order);
                   }
                   else{
                       return Mono.empty();
                   }
                });
    }
}

@Data
class parsedOrder {

    private Long Id;
    private Long UserId;
    private String address;

    private LocalDateTime creationDate;
    private LocalDateTime deliveryDate;

}
