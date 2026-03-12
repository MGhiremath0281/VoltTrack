package com.volttrack.volttrack.service;

import com.volttrack.volttrack.entity.User;
import java.util.List;

public interface UserService {

    User createUser(User user);

    List<User> getAllUsers();

    void deleteUser(Long id);

    User getUserById(Long id);
}
