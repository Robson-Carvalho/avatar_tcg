package com.oak.legends_of_three.service;

import com.oak.legends_of_three.model.User;
import com.oak.legends_of_three.repository.UserRepository;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User register(User user) throws SQLException, IllegalArgumentException {
        List<User> users = userRepository.findAll();

        for (User u : users) {
            if (u.getEmail().equals(user.getEmail()) ||  u.getNickname().equals(user.getNickname())) {
                throw new IllegalArgumentException("Username already exists");
            }
        }

        return userRepository.save(user);
    }

    public User findByEmail(String email) throws SQLException {
        List<User> users = userRepository.findAll();

        for (User u : users) {
            if (u.getEmail().equals(email)) {
                return u;
            }
        }

        return null;
    }

    public User findById(String id) throws SQLException {
        List<User> users = userRepository.findAll();

        for (User u : users) {
            if (u.getId().equals(id)) {
                return u;
            }
        }

        return null;
    }
}