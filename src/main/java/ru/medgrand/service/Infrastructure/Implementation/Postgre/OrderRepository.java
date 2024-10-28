package ru.medgrand.service.Infrastructure.Implementation.Postgre;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.function.BiFunction;

@Repository
public class OrderRepository implements IOrderRepository {

    private final R2dbcEntityTemplate r2dbcTemplate;
    private final BiFunction<Row, RowMetadata, Order> orderMapper;
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
            Order returnOrder = new Order();
            returnOrder.setId(row.get("id", Long.class));
            this.dishRepository.getAllDishesByOrderId(returnOrder.getId()).collectList().subscribe(returnOrder::setDishes);
            returnOrder.setAddress(row.get("address", String.class));
            returnOrder.setDeliveryDate(row.get("deliveryDate", LocalDateTime.class));
            returnOrder.setCreationDate(row.get("creationDate", LocalDateTime.class));
            this.userRepository.getUserById(row.get("user_id", Integer.class)).subscribe(returnOrder::setUser);
            returnOrder.setSum(returnOrder.getDishes().stream().mapToLong(Dish::getCost).sum());
            return returnOrder;
        };

    }

    @Override
    public Flux<Order> getAllOrders() {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select *
                            from orders""")
                    .map(orderMapper)
                    .all();
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
                            limit """ + Integer.toString(limit) +
                            "offset " + Integer.toString(skip))
                    .map(orderMapper)
                    .all();
        }
        catch (NullPointerException exp){
            return Flux.empty();
        }
    }

    @Override
    public Flux<Order> getAllOrdersByUser(String username) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("with userId as (select users.id from users where users.username = " + username + " )" + """
                            select *
                            from orders
                            where orders.user_id = userId""")
                    .map(orderMapper)
                    .all();
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
                    .first();
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
                                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                        r2dbcTemplate.getDatabaseClient()
                                                .sql("""
                                                        insert into orders (id, u_id, address, creationDate, deliveryDate)
                                                        values (%d, %d, %s, %s, %s)""".formatted
                                                        (
                                                                order.getId(),
                                                                order.getUser().getId(),
                                                                order.getAddress(),
                                                                df.format(order.getCreationDate()),
                                                                df.format(order.getDeliveryDate())
                                                        )
                                                );
                                        order.getDishes()
                                                .forEach(dish -> {
                                                    r2dbcTemplate.getDatabaseClient()
                                                            .sql("""
                                                                    insert into orders_dishes (o_id, d_id)
                                                                    values (%d, %d)""".formatted(order.getId(), dish.getId()));
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
    public void deleteOrder(Order order) {
        this.getOrderById(order.getId())
                .hasElement()
                .flatMap(bool -> {
                    if(bool){
                        r2dbcTemplate.getDatabaseClient()
                                .sql("""
                                        delete from orders
                                        where id = %d""".formatted(order.getId()));
                        r2dbcTemplate.getDatabaseClient()
                                .sql("""
                                        delete from orders_dishes
                                        where o_id = %d""".formatted(order.getId()));
                        return Mono.just(order);
                    }
                    else{
                        return Mono.empty();
                    }
                });
    }

    @Override
    public void updateOrder(Order order) {
        this.getOrderById(order.getId())
                .hasElement()
                .flatMap(bool -> {
                   if(bool){
                       DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                       r2dbcTemplate.getDatabaseClient()
                               .sql("""
                                       update orders
                                       set u_id = %d,
                                            address = %s,
                                            creationDate = %s,
                                            deliveryDate = %s,
                                       where id = %d""".formatted
                                       (
                                               order.getUser().getId(),
                                               order.getAddress(),
                                               df.format(order.getCreationDate()),
                                               df.format(order.getDeliveryDate()),
                                               order.getId()
                                       )
                               );
                       r2dbcTemplate.getDatabaseClient()
                               .sql("""
                                        delete from orders_dishes
                                        where o_id = %d""".formatted(order.getId()));
                       order.getDishes()
                               .forEach(dish -> {
                                   r2dbcTemplate.getDatabaseClient()
                                           .sql("""
                                                                    insert into orders_dishes (o_id, d_id)
                                                                    values (%d, %d)""".formatted(order.getId(), dish.getId()));
                               });

                       return Mono.just(order);
                   }
                   else{
                       return Mono.empty();
                   }
                });
    }
}
