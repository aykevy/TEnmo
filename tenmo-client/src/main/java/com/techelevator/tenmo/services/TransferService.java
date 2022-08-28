package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class TransferService
{
    //Init restTemplate to call a request from the controller.
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/";
    final int TRANSFER_SEND = 2;
    final int TRANSFER_REQUEST =1;


    //Newest Edit Made During Tech Session
    public boolean transfer(User user, Transfer transfer, boolean isWithdraw){
        //Check to see if money movement will be a with draw or deposit
        String transferType = isWithdraw ? "withdraw" : "deposit";
        //create entity
        HttpEntity<Transfer> entity = createEntityTransfer(transfer);
        boolean success = false;

        //Send request to controller
        try{
            if (transferType.equals("deposit")){
                restTemplate.put(BASE_URL + user.getId() + "/" + transfer.getAccountTo() + "/transfer/" + transferType, entity);

            } else if (transferType.equals("withdraw")){
                restTemplate.put(BASE_URL + user.getId() + "/" + transfer.getAccountFrom() + "/transfer/" + transferType, entity);
            }
            success = true;
        }
        catch (RestClientResponseException | ResourceAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    public Transfer add(Transfer newTransfer){
        //Send request to controller to create new Transfer Object in the DB

        HttpEntity<Transfer> entity = createEntityTransfer(newTransfer);
        Transfer returnedTransfer = null;
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

    public Transfer createNewTransfer(int tranType, int status, int fromID, int toID, BigDecimal transferAmount){
        //Uses information provided to create a new transfer object
        Transfer transfer = new Transfer();
        transfer.setTypeId(tranType);
        transfer.setStatusId(status);
        transfer.setAccountFrom(fromID);
        transfer.setAccountTo(toID);

        //Sets the accounts from/to based on transfer type - receiving or sending
        if (tranType == TRANSFER_SEND){
            transfer.setAccountFrom(fromID);
            transfer.setAccountTo(toID);
        } else if (tranType == TRANSFER_REQUEST){
            transfer.setAccountFrom(toID);
            transfer.setAccountTo(fromID);
        }
        transfer.setAmount(transferAmount);
        return transfer;
    }

    public boolean updateTransfer (Transfer transfer){
        //calls the controller to update a specific transfer
       boolean success = false;
       try {
           restTemplate.put(BASE_URL + "transfer/" + transfer.getId() + "/", createEntityTransfer(transfer), Transfer.class);
           success = true;
       } catch (RestClientResponseException | ResourceAccessException e) {
               BasicLogger.log(e.getMessage());
       }
        return success;
    }

    public List<Transfer> getTransferTransactions(int useId, int accId) {
        //calls the controller for a list of transfers meeting UserID and AccID - AccID used for future multiple accounts need.
        Transfer[]  result = restTemplate.getForObject(BASE_URL + useId+"/"+ accId+"/transfer", Transfer[].class);
        return Arrays.asList(result);
    }
    public Transfer getSelectedTransaction(List<Transfer> transferList, int tranID){
        //Calls the controller for a specific transfer.
        for (Transfer transfer : transferList) {
            if (transfer.getId() == tranID){
                return transfer;
            }
        }
        return null;
    }
    //Helper method to create entities.
    private HttpEntity<Transfer> createEntityTransfer(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, headers);
    }

}