package ru.medgrand.service.Infrastructure.Implementation.Postgre;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.medgrand.service.Infrastructure.IUserRepository;
import ru.medgrand.service.Model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.function.BiFunction;

@Repository
public class UserRepository implements IUserRepository {

    private final R2dbcEntityTemplate r2dbcTemplate;
    private final BiFunction<Row, RowMetadata, User> userMapper;

    @Autowired
    public UserRepository(R2dbcEntityTemplate r2dbcEntityTemplate){

        this.r2dbcTemplate = r2dbcEntityTemplate;

        userMapper = (row, rowMetadata) -> {
            User returnUser = new User();
            returnUser.setId(row.get("id", Long.class));
            returnUser.setRole(row.get("role", String.class));
            returnUser.setUsername(row.get("username", String.class));
            returnUser.setPassword(row.get("password", String.class));
            returnUser.setCreationDate(row.get("creationDate", LocalDateTime.class));
            returnUser.setCreationDate(row.get("birthday", LocalDateTime.class));
            return returnUser;
        };

    }

    @Override
    public Flux<User> getAllUsers() {

        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select *
                            from users
                            join roles on roles.id = users.role_id
                            """)
                    .map(userMapper)
                    .all();
        }
        catch (NullPointerException exp){
            return Flux.empty();
        }
    }

    @Override
    public Flux<User> getPaginationUsers(int skip, int limit) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select *
                            from users
                            join roles on roles.id = users.role_id
                            limit """ + Integer.toString(limit) +
                            "offset " + Integer.toString(skip))
                    .map(userMapper)
                    .all();
        }
        catch (NullPointerException exp){
            return Flux.empty();
        }
    }

    @Override
    public Mono<User> getUserById(long id) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select *
                            from users
                            join roles on roles.id = users.role_id
                            where users.id = """ + Long.toString(id))
                    .map(userMapper)
                    .first();
        }
        catch (NullPointerException exp){
            return Mono.empty();
        }
    }

    @Override
    public Mono<User> getUserByName(String name) {
        try {
            return r2dbcTemplate.getDatabaseClient()
                    .sql("""
                            select *
                            from users
                            join roles on roles.id = users.role_id
                            where users.name = """ + name)
                    .map(userMapper)
                    .first();
        }
        catch (NullPointerException exp){
            return Mono.empty();
        }
    }

    @Override
    public Mono<User> createUser(User user) {
        return this.getUserById(user.getId())
                .hasElement()
                .flatMap(bool -> {
                    if(bool){
                        return Mono.empty();
                    }
                    else{
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        r2dbcTemplate.getDatabaseClient()
                                .sql("""
                                        with roleId as (select id from roles where role = %s)
                                        insert into users (id, username, password, role_id, creationDate, birthday)
                                        values (%d, %s, %s, roleId, %s, %s)""".formatted
                                        (
                                                user.getRole(),
                                                user.getId(),
                                                user.getUsername(),
                                                user.getPassword(),
                                                df.format(user.getCreationDate()),
                                                df.format(user.getBirthday())
                                        )
                                );
                        return Mono.just(user);
                    }
                });
    }

    @Override
    public void deleteUser(User user) {
        this.getUserById(user.getId())
                .hasElement()
                .flatMap(bool -> {
                    if(bool){
                        r2dbcTemplate.getDatabaseClient()
                                .sql("""
                                        delete from users
                                        where id = %d""".formatted(user.getId()));
                        return Mono.just(user);
                    }
                    else{
                        return Mono.empty();
                    }
                });
    }

    @Override
    public void updateUser(User user) {
        this.getUserById(user.getId())
                .hasElement()
                .flatMap(bool -> {
                    if(bool){
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        r2dbcTemplate.getDatabaseClient()
                                .sql("""
                                        with roleId as (select id from roles where role = %s)
                                        update users
                                        set username = %s,
                                            password = %s,
                                            role_id = roleId,
                                            creationDate = %s,
                                            birthday = %s
                                        where id = %d""".formatted
                                        (
                                                user.getRole(),
                                                user.getUsername(),
                                                user.getPassword(),
                                                df.format(user.getCreationDate()),
                                                df.format(user.getBirthday()),
                                                user.getId()
                                        )
                                );
                        return Mono.just(user);
                    }
                    else{
                        return Mono.empty();
                    }
                });
    }


}
