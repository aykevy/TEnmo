package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


public class AccountService {
    //Init restTemplate to call a request from the controller.
    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_URL = "http://localhost:8080/";


    public List<Account> getAccounts(User user) {
        //Requests the controller to get information for the specified user in regard to their accounts.
        Account [] result = restTemplate.getForObject(BASE_URL + user.getId(), Account[].class);
        return Arrays.asList(result);
    }

    public List<Transfer> getTransactions(User user) {
        //Requests the controller to get information for the specified user in regard to their transfers.
        Transfer [] result = restTemplate.getForObject(BASE_URL + user.getId(),Transfer[].class);
        return Arrays.asList(result);
    }

    //helper method to create entities.
    private HttpEntity<Account> createEntityAccount(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(account, headers);
    }

    public boolean hasMultipleAccounts(List<Account> accounts){
        //method to check for more than 1 account for user, no implementation yet but starting framework
        return accounts.size() > 1;
    }

    public BigDecimal getVerifiedAmount(BigDecimal enteredAmount){
        //Verifies amount is not negative or zero, essentially a valid number
        if (enteredAmount.compareTo(BigDecimal.ZERO) != 1)
        {
            System.out.println("Sorry, you can not enter a negative or zero. Transfer aborted, please try again.");
            return BigDecimal.ZERO;
        }
        return enteredAmount;
    }

    public BigDecimal getVerifiedBalance(BigDecimal enteredAmount, BigDecimal userBalance){
        //Verifies balance in account can cover the withdraw
        if (enteredAmount.compareTo(userBalance) == 1)
        {
            System.out.println("Sorry, you can not enter more than your balance. Transfer aborted, please try again.");
            return BigDecimal.ZERO;
        }
        return enteredAmount;

    }


}
