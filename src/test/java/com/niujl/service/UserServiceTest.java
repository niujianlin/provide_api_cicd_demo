package com.niujl.service;

import com.niujl.bean.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        for (User u : users) {
            System.out.println(u);
        }
    }

    @Test
    public void testAddAndGetUserById() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userService.addUser(user);

        assertNotNull(user.getId(), "插入后应回填自增主键");
        User saved = userService.getUserById(user.getId());
        assertNotNull(saved);
        assertEquals("Test User", saved.getName());
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setName("Before Update");
        user.setEmail("before@example.com");
        userService.addUser(user);

        user.setName("Updated Name");
        userService.updateUser(user);
        assertEquals("Updated Name", userService.getUserById(user.getId()).getName());
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setName("ToDelete User");
        user.setEmail("todelete@example.com");
        userService.addUser(user);

        Long id = user.getId();
        userService.deleteUser(id);
        assertNull(userService.getUserById(id));
    }
}
