package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
//import org.apache.catalina.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);
    User findByUserId(int id);
    int findUserByAccountId(int accountId,String toOrFrom, int transferId);
    int findIdByUsername(String username);

    boolean create(String username, String password);
}
