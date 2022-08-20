package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class TransferController
{
    private final TransferDao transferDao;

    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "{id}/{accountId}/transfer/withdraw", method = RequestMethod.PUT)
    public void withdraw(@PathVariable int id, @PathVariable int accountId, Transfer transfer)
    {
        transferDao.withdraw(id, accountId, transfer.getAmount());
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "{id}/{accountId}/transfer/deposit", method = RequestMethod.PUT)
    public void deposit(@PathVariable int id, @PathVariable int accountId, Transfer transfer)
    {
        transferDao.deposit(id, accountId, transfer.getAmount());
    }
}
