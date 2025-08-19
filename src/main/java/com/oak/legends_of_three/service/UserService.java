package com.oak.legends_of_three.service;

import com.oak.legends_of_three.model.User;
import com.oak.legends_of_three.repository.UserRepository;
import java.sql.SQLException;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User register(User user) throws SQLException, IllegalArgumentException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (userRepository.findByNickname(user.getNickname()) != null) {
            throw new IllegalArgumentException("Nickname already in use");
        }

        // obter 10 cartas comum e com user_id null aleatórias para adicionar na conta do

        return userRepository.save(user);
    }

    public User findByEmail(String email) throws SQLException {
        return userRepository.findByEmail(email);
    }

    public User findByNickname(String nickname) throws SQLException {
        return userRepository.findByNickname(nickname);
    }
}