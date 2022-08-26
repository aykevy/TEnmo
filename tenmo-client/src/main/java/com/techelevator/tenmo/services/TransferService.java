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
    public boolean transfer(User user, Transfer transfer, boolean isWithdraw)
    {
        String transferType = isWithdraw ? "withdraw" : "deposit";
        HttpEntity<Transfer> entity = createEntityTransfer(transfer);
        boolean success = false;
        try
        {
            if (transferType.equals("deposit")){

                System.out.println("Depositing into "+ user.getId()+ ":" + transfer.getAccountTo());
                restTemplate.put(BASE_URL + user.getId() + "/" + transfer.getAccountTo() + "/transfer/" + transferType, entity);

            } else if (transferType.equals("withdraw")){

                System.out.println("Withdrawing from "+ user.getId()+ ":" + transfer.getAccountFrom());
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

    public Transfer add(Transfer newTransfer)
    {
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

    public Transfer createNewTransfer(int tranType, int status, int fromID, int toID, BigDecimal transferAmount)
    {
        Transfer transfer = new Transfer();
        //int id will be automatically created
        //typeId 1 = request, 2 = receive
        //statusId, 1 = pending, 2 = approved, 3 = rejected
        transfer.setTypeId(tranType);
        transfer.setStatusId(status);
        transfer.setAccountFrom(fromID);
        transfer.setAccountTo(toID);
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
        Transfer[]  result = restTemplate.getForObject(BASE_URL + useId+"/"+ accId+"/transfer", Transfer[].class);
        return Arrays.asList(result);
    }
    public Transfer getSelectedTransaction(List<Transfer> transferList, int tranID){
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