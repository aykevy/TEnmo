package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


public class AccountService {
    //Init restTemplate to call a request from the controller.
    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_URL = "http://localhost:8080/";

    //Jacobs First Edit From First Session
    public List<Account> getAccounts(User user) {
        //Requests the controller to get information for the specified user in regard to their accounts.
        Account [] result = restTemplate.getForObject(BASE_URL + user.getId(), Account[].class);
        return Arrays.asList(result);
    }

    //Natalie's Edits
    public List<Transfer> getTransactions(User user) {
        //Requests the controller to get information for the specified user in regard to their transfers.
        Transfer [] result = restTemplate.getForObject(BASE_URL + user.getId(),Transfer[].class);
        return Arrays.asList(result);
    }

    public String findUserNameByAccountId(int accountid){
        return restTemplate.getForObject(BASE_URL + accountid + "/account",String.class);

    }

    //helper method to create entities.
    private HttpEntity<Account> createEntityAccount(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(account, headers);
    }
    public boolean hasMultipleAccounts(List<Account> accounts){
        return accounts.size() > 1;
    }



}
