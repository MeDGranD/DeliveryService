package ru.medgrand.service.Infrastructure.Implementation.Postgre;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Infrastructure.IDishRepository;
import ru.medgrand.service.Model.Dish;

import java.util.function.BiFunction;

@Repository
public class DishRepository implements IDishRepository {

    private final R2dbcEntityTemplate r2dbcTemplate;
    private final BiFunction<Row, RowMetadata, Dish> dishMapper;

    @Autowired
    public DishRepository(R2dbcEntityTemplate r2dbcEntityTemplate){

        this.r2dbcTemplate = r2dbcEntityTemplate;

        dishMapper = (row, rowMetadata) -> {
            Dish returnDish = new Dish();
            returnDish.setId(row.get("id", Long.class));
            returnDish.setCost(row.get("cost", Long.class));
            returnDish.setName(row.get("name", String.class));
            returnDish.setDescription(row.get("description", String.class));
            return returnDish;
        };

    }

    @Override
    public Flux<Dish> getAllDishes() {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("select * from dishes")
                    .map(dishMapper)
                    .all();
        }
        catch (NullPointerException exp){
            return Flux.empty();
        }
    }

    @Override
    public Flux<Dish> getPaginationDishes(int skip, int limit) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("select * from dishes limit " + Integer.toString(limit) + " offset " + Integer.toString(skip))
                    .map(dishMapper)
                    .all();
        }
        catch (NullPointerException exp){
            return Flux.empty();
        }
    }

    @Override
    public Mono<Dish> getDishById(long id) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("select * from dishes where id = " + Long.toString(id))
                    .map(dishMapper)
                    .first();
        }
        catch (NullPointerException exp){
            return Mono.empty();
        }
    }

    @Override
    public Mono<Dish> getDishByName(String name) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("select * from dishes where name = '" + name + "'")
                    .map(dishMapper)
                    .first();
        }
        catch (NullPointerException exp){
            return Mono.empty();
        }
    }

    @Override
    public Flux<Dish> getAllDishesByOrderId(long orderId) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select dishes.id as id,
                                dishes.name as name,
                                dishes.cost as cost,
                                dishes.description as description
                            from orders_dishes
                            join dishes on dishes.id = orders_dishes.dish_id
                            where orders_dishes.order_id = """ + orderId)
                    .map(dishMapper)
                    .all();
        }
        catch (NullPointerException exp){
            return Flux.empty();
        }
    }

    @Override
    public Mono<Dish> createDish(Dish dish) {
        return this.getDishById(dish.getId())
                .hasElement()
                .flatMap(bool -> {
                    if(bool){
                        return Mono.empty();
                    }
                    else{
                        r2dbcTemplate.getDatabaseClient()
                                .sql("""
                                        insert into dishes (id, name, description, cost)
                                        values (%d, %s, %s, %d)""".formatted
                                        (
                                                dish.getId(),
                                                dish.getName(),
                                                dish.getDescription(),
                                                dish.getCost()
                                        )
                                );
                            return Mono.just(dish);
                    }
                });
    }

    @Override
    public Mono<Dish> updateDish(Dish dish) {
        return this.getDishById(dish.getId())
                .hasElement()
                .flatMap(bool -> {
                    if(bool){
                        r2dbcTemplate.getDatabaseClient()
                                    .sql("""
                                            update dishes
                                            set name = %s,
                                                description = %s,
                                                cost = %d
                                            where id = %d""".formatted
                                            (
                                                    dish.getName(),
                                                    dish.getDescription(),
                                                    dish.getCost(),
                                                    dish.getId()
                                            )
                                    );
                        return Mono.just(dish);
                    }
                    else{
                        return Mono.empty();
                    }
                });
    }

    @Override
    public Mono<Dish> deleteDish(Dish dish) {
        return this.getDishById(dish.getId())
                .hasElement()
                .flatMap(bool -> {
                    if(bool){
                        r2dbcTemplate.getDatabaseClient()
                                    .sql("""
                                            delete from dishes
                                            where id = %d""".formatted(dish.getId()));
                            return Mono.just(dish);
                    }
                    else{
                        return Mono.empty();
                    }
                });
    }
}
