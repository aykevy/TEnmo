package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> list(int id);
    void withdraw(int id, int accountId, BigDecimal withdrawAmount);
    void deposit(int id, int accountId, BigDecimal withdrawAmount);
    Transfer add(Transfer transfer);
}
