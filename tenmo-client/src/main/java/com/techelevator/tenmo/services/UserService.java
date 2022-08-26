package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class UserService

{
    //Init restTemplate to call a request from the controller.
    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_URL = "http://localhost:8080/";

    public List<User> getUsers()
    {
        User[] result = restTemplate.getForObject(BASE_URL, User[].class);
        return Arrays.asList(result);
    }

    //Newest edit for single user
    public User getUser(int id){
        return restTemplate.getForObject(BASE_URL + "user/" + id, User.class);
    }

    public int getUserByAccountId(int accountId,boolean isFrom,int transferId){
        String toOrFrom = isFrom ? "fromAccount" : "toAccount";
        return restTemplate.getForObject(BASE_URL + "user/account/" + accountId +"/"+transferId+"/findUser/"+toOrFrom, int.class);
    }

    private HttpEntity<User> createEntity(User user){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(user, headers);
    }

    public int getVerifiedId(int enteredId, Long userId)
    {
        if ((long)enteredId == userId)
        {
            System.out.println("Sorry, you can not select yourself. Transfer aborted, please try Again.");
            return -1;
        }
        return enteredId;
    }
}
