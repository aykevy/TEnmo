package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
public class TransferController
{
    private final TransferDao transferDao;

    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    /*
        This function creates a record for the transfer database for a transaction.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public Transfer add(@Valid @RequestBody Transfer transfer)
    {
        return transferDao.add(transfer);
    }

    /*
        This function deposits money into account. Requires the user id and account id of a user.
     */
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "{id}/{accountId}/transfer/withdraw", method = RequestMethod.PUT)
    public void withdraw(@PathVariable int id, @PathVariable int accountId, @Valid @RequestBody Transfer transfer)
    {
        transferDao.withdraw(id, accountId, transfer.getAmount());
    }

    /*
        This function deposits money into account. Requires the user id and account id of a user.
     */
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "{id}/{accountId}/transfer/deposit", method = RequestMethod.PUT)
    public void deposit(@PathVariable int id, @PathVariable int accountId, @Valid @RequestBody Transfer transfer)
    {
        transferDao.deposit(id, accountId, transfer.getAmount());
    }

    //@ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "{id}/{accountId}/transfer", method = RequestMethod.GET)
    public List<Transfer> getTransferTransactions(@PathVariable int id, @PathVariable int accountId){
        System.out.println("here in controller");
        return transferDao.getTransferTransactions(accountId);
    }
}