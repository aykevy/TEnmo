package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

//Generic DAO interface
public interface AccountDao {
    List<Account> list(int id);

}
