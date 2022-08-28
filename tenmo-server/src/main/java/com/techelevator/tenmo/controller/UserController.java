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

    @RequestMapping(path = "user/account/{accountId}/{transferId}/findUser/{toOrFrom}", method = RequestMethod.GET)
    public int findUserByAccountId (@PathVariable int accountId, @PathVariable int transferId, @PathVariable String toOrFrom){
        return userDao.findUserByAccountId(accountId,toOrFrom,transferId);
    }

    @RequestMapping(path = "user/account/{accountId}", method = RequestMethod.GET)
    public String findUserNameByAccountId (@PathVariable int accountId){
        return userDao.findUserNameByUserId(accountId);
    }
    @RequestMapping(path = "{userID}/name", method = RequestMethod.GET)
    public String findUserNameByUserId (@PathVariable int userID){
        return userDao.findUserNameByUserId(userID);
    }
}
