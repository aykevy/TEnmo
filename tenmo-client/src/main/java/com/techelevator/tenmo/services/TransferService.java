package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService
{
    //Init restTemplate to call a request from the controller.
    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_URL = "http://localhost:8080/";


    //Newest Edit Made During Tech Session
    public boolean transfer(User user, Transfer transfer, boolean isWithdraw)
    {
        String transferType = isWithdraw ? "deposit" : "withdraw";
        HttpEntity<Transfer> entity = createEntityTransfer(transfer);
        boolean success = false;
        try
        {
            restTemplate.put(BASE_URL + user.getId() + "/" + transfer.getAccountTo() + "/transfer/" + transferType, entity);
            success = true;
        }
        catch (RestClientResponseException | ResourceAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    public Transfer addTransfer(Transfer newTransfer) {
        HttpEntity<Transfer> entity = createEntityTransfer(newTransfer);
        Transfer returnedTransfer= null;
        try
        {
            returnedTransfer = restTemplate.postForObject(BASE_URL + "transfer", entity, Transfer.class);
        }
        catch (RestClientResponseException | ResourceAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }

    /*
    public Transfer createTransfer(int typeId, int statusId, int accountFrom, int accountTo, BigDecimal amount)
    {
        //int id will be automatically created
        //typeId 1,  = request, 2 = receive
        //statusId, 1 = pending, 2 = approved, 3 = rejected

    }
    */


    //Helper method to create entities.
    private HttpEntity<Transfer> createEntityTransfer(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, headers);
    }
}
