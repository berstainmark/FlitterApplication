package org.flitter.app.service;

import org.flitter.app.dao.UserRepository;
import org.flitter.app.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

}
