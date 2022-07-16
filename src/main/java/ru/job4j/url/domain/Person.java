package ru.job4j.url.domain;

import java.util.Objects;

/**
 * 3.4.8. Rest
 * 3. Авторизация JWT [#9146]
 * Модель данных Person
 *
 * @author Dmitry Stepanov, user Dima_Nout
 * @since 16.07.2022
 */
public class Person {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return Objects.equals(username, person.username) && Objects.equals(password, person.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "Person{username='" + username + '\''
                + ", password='" + password + '\'' + '}';
    }
}
