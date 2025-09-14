package com.oak.avatar_tcg.service;

import com.oak.avatar_tcg.model.Deck;
import com.oak.avatar_tcg.model.User;
import com.oak.avatar_tcg.repository.DeckRepository;
import com.oak.avatar_tcg.repository.UserRepository;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;

    public UserService() {
        this.userRepository = new UserRepository();
        this.deckRepository = new DeckRepository();
    }

    public void delete(String id){
        User user;

        synchronized (userRepository){
             user = userRepository.findById(id);
        }

        if(user != null) {
            synchronized (userRepository){
                userRepository.delete(id);
            }

            return;
        }

        throw new IllegalArgumentException("User not found");
    }

    public User register(User user) throws SQLException, IllegalArgumentException {
        if (userRepository.findByNickname(user.getNickname()) != null) {
            if(userRepository.findByEmail(user.getEmail()) != null){
                throw new IllegalArgumentException("Username already exists");
            }
        }


        userRepository.save(user);

        Deck deck = new Deck();
        deck.setUserId(user.getId());
        deck.setCard1Id(null);
        deck.setCard2Id(null);
        deck.setCard3Id(null);
        deck.setCard4Id(null);
        deck.setCard5Id(null);

        synchronized (userRepository) {
            deckRepository.save(deck);
        }

        return user;
    }

    public User findByEmail(String email) {
        User user;

        synchronized (userRepository) {
            user = userRepository.findByEmail(email);
        }

        return user;
    }

    public User findById(String id) {
        User user;

        synchronized (userRepository){
            user = userRepository.findById(id);
        }

        return user;
    }
}