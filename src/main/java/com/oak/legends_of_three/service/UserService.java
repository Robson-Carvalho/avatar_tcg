package com.oak.legends_of_three.service;

import com.oak.legends_of_three.model.Deck;
import com.oak.legends_of_three.model.User;
import com.oak.legends_of_three.repository.DeckRepository;
import com.oak.legends_of_three.repository.UserRepository;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;

    public UserService() {
        this.userRepository = new UserRepository();
        this.deckRepository = new DeckRepository();
    }

    public User register(User user) throws SQLException, IllegalArgumentException {
        List<User> users = userRepository.findAll();

        for (User u : users) {
            if (u.getEmail().equals(user.getEmail()) || u.getNickname().equals(user.getNickname())) {
                throw new IllegalArgumentException("Username already exists");
            }
        }

        userRepository.save(user);

        Deck deck = new Deck();
        deck.setUserId(user.getId());
        deck.setCard1Id(null);  // Change from "" to null
        deck.setCard2Id(null);  // Change from "" to null
        deck.setCard3Id(null);  // Change from "" to null
        deck.setCard4Id(null);  // Change from "" to null
        deck.setCard5Id(null);  // Change from "" to null

        deckRepository.save(deck);

        return user;
    }

    public User findByEmail(String email) {
        List<User> users = userRepository.findAll();

        for (User u : users) {
            if (u.getEmail().equals(email)) {
                return u;
            }
        }

        return null;
    }

    public User findById(String id) {
        List<User> users = userRepository.findAll();

        for (User u : users) {
            if (u.getId().equals(id)) {
                return u;
            }
        }

        return null;
    }
}