package ru.medgrand.service.Application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Infrastructure.IDishRepository;
import ru.medgrand.service.Model.Dish;

@Service
public class DishService {

    private final IDishRepository dishRepository;

    @Autowired
    public DishService(IDishRepository dishRepository){
        this.dishRepository = dishRepository;
    }

    public Flux<Dish> getAllDishes(){
        return dishRepository.getAllDishes();
    }

    public Flux<Dish> getPaginationDishes(int skip, int limit){
        return dishRepository.getPaginationDishes(skip, limit);
    }

    public Mono<Dish> getDishById(long id){
        return dishRepository.getDishById(id);
    }

    public Mono<Dish> getDishByName(String name){
        return dishRepository.getDishByName(name);
    }

    public Flux<Dish> getAllDishesByOrderId(long orderId){
        return dishRepository.getAllDishesByOrderId(orderId);
    }

    public Mono<Dish> createDish(Dish dish){
        return dishRepository.createDish(dish);
    }

    public Mono<Dish> updateDish(Dish dish){
        return dishRepository.updateDish(dish);
    }

    public Mono<Dish> deleteDish(Dish dish){
        return dishRepository.deleteDish(dish);
    }

}
