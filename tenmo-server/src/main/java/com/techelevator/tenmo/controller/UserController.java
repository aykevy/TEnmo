package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController
{
    private final UserDao userDao;

    public UserController(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    //Gets a list of accounts from the DAO
    public List<User> getUsers()
    {
        return userDao.findAll();
    }


    @RequestMapping(path = "user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable int id)
    {
        return userDao.findByUserId(id);
    }




}
