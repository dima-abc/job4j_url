package ru.job4j.url.repository.mem;

import org.springframework.stereotype.Repository;
import ru.job4j.url.domain.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 3.4.8. Rest
 * 3. Авторизация JWT [#9146]
 * UserStore хранилище в памяти модели Person
 *
 * @author Dmitry Stepanov, user Dima_Nout
 * @since 16.07.2022
 */
@Repository
public class UserStore {
    private final Map<String, Person> users = new ConcurrentHashMap<>();

    public void save(Person person) {
        this.users.put(person.getUsername(), person);
    }

    public Person findByUsername(String username) {
        return this.users.get(username);
    }

    public List<Person> findAll() {
        return new ArrayList<>(users.values());
    }
}
