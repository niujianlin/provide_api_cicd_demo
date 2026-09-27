package com.niujl.service;

import com.niujl.bean.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        // 确保数据库中有初始数据
        User user = new User();
        user.setName("Initial User");
        user.setEmail("initial@example.com");
        userService.addUser(user);
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
//        assertNotNull(users);
        // 遍历输出
        for (User u : users) {
            System.out.println(u);
        }
    }

    @Test
    public void testGetUserById() {
        User user = userService.getUserById(1L);
        assertNotNull(user);
    }

    @Test
    public void testAddUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userService.addUser(user);
        assertNotNull(userService.getUserById(user.getId()));
    }

    @Test
    public void testUpdateUser() {
        User user = userService.getUserById(1L);
        String originalName = user.getName();
        user.setName("Updated Name");
        userService.updateUser(user);
        assertEquals("Updated Name", userService.getUserById(1L).getName());
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setName("ToDelete User");
        user.setEmail("todelete@example.com");
        userService.addUser(user);
        userService.deleteUser(user.getId());
        assertNull(userService.getUserById(user.getId()));
    }
}