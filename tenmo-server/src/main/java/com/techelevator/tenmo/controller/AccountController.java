package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class AccountController {
    //Controller which receives information from Account Service and then speaks to DAO to get data.
    private final AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    //Gets a list of accounts from the DAO
    public List<Account> getAccounts(@PathVariable int id){
        return accountDao.list(id);

    }

    ///update method to match signature of "User/Account/AccountID - also move to the User controller
    @RequestMapping(path = "{accountId}/account", method = RequestMethod.GET)
    public String findUserNameByAccountId (@PathVariable @RequestBody int accountId){
        return accountDao.findUserNameByAccountId(accountId);
    }

}
