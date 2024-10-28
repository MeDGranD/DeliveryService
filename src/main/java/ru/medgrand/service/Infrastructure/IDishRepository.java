package ru.medgrand.service.Infrastructure;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Model.Dish;

public interface IDishRepository {

    public Flux<Dish> getAllDishes();
    public Flux<Dish> getPaginationDishes(int skip, int limit);
    public Mono<Dish> getDishById(long id);
    public Mono<Dish> getDishByName(String name);
    public Flux<Dish> getAllDishesByOrderId(long orderId);


    public Mono<Dish> createDish(Dish dish);
    public Mono<Dish> updateDish(Dish dish);
    public Mono<Dish> deleteDish(Dish dish);

}
