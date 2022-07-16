package ru.job4j.url.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.url.domain.Person;
import ru.job4j.url.repository.mem.UserStore;

import java.util.List;

/**
 * 3.4.8. Rest
 * 3. Авторизация JWT [#9146]
 * UserController контроллер для регистрации пользователя
 * и получения списках всех пользователей системы.
 *
 * @author Dmitry Stepanov, user Dima_Nout
 * @since 16.07.2022
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStore users;
    private final BCryptPasswordEncoder encoder;

    public UserController(UserStore users, BCryptPasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        users.save(person);
        System.out.println(users.findAll());
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        System.out.println("********************" + users.findAll() + "****************");
        return users.findAll();
    }
}
