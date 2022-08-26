package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private final AccountService accountService = new AccountService();
    private final int TRANSFER_PENDING = 1;
    private final int TRANSFER_SUCCESS = 2;
    private final int TRANSFER_REJECT = 3;
    final int TRANSFER_SEND = 2;
    final int TRANSFER_REQUEST =1;
    //new
    private final UserService userService = new UserService();

    //new
    private final TransferService transferService = new TransferService();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        //Get a list of the users accounts, if the user has multiple accounts prompt to pick a specific account.
        List<Account> accounts = accountService.getAccounts(currentUser.getUser());

        if (accountService.hasMultipleAccounts(accounts)) {
            consoleService.printAvailableAccounts(accounts);
            int selection = consoleService.promptForInt("Select the Account to view its balance");
            consoleService.displayBalanceForAccount(accounts.get(selection));
        } else {
            consoleService.displayBalanceForAccount(accounts.get(0));
        }
    }

    //Natalie's Edits
    private void viewTransferHistory() {
        //Calls the transfer Menu; will print the transfers selected and then prompt to view more details.
        transferMenu(TRANSFER_SUCCESS);
    }


    private void approveRequests()
    {
        //After viewing your pending requests, you may approve requests here
        //1 - UPDATE THAT THE STATUS HAS BEEN RECEIVED
        //2 - IF ACCEPT, do the actual sending AND CHANGE CODE FROM PENDING TO APPROVED
        //3 - IF REJECT, do NO sending AND CHANGE CODE FROM PENDIGN TO REJECT
    }

    private Transfer generateTransferWithId(int enteredId, BigDecimal answerAmount, int transferTypeId, int statusTypeId)
    {
        //Current User Account, currentUser = AuthenticatedUser
        List<Account> currentUserAccounts = accountService.getAccounts(currentUser.getUser());
        Account userAccount = currentUserAccounts.get(0);

        //Target Account For Transfer
        User targetTransferUser = userService.getUser(enteredId);
        List<Account> transferUserAccounts = accountService.getAccounts(targetTransferUser);
        Account transferAccount = transferUserAccounts.get(0);

        //Here we create and document this transfer and add to database.
        Transfer transfer = transferService.createNewTransfer(transferTypeId,statusTypeId, userAccount.getAccountId(),transferAccount.getAccountId(),answerAmount);
        Transfer transferWithGeneratedID = transferService.add(transfer);
        return transferWithGeneratedID;
    }

	private void sendBucks() {
        //Prompt user to pick a transfer to from list.
        consoleService.getUserList(userService.getUsers());
        try
        {
            int enteredId = consoleService.promptForInt("Enter ID of user you are sending TEBucks to: ");
            if (enteredId == currentUser.getUser().getId())
            {
                System.out.println("Sorry, you can not send money to yourself. Try Again.");
            }
            else
            {
                //Get a Big Decimal from the user
                BigDecimal answerAmount = consoleService.promptForBigDecimal("How many TEBucks do you want to send to user " + enteredId + " :");
                Transfer transferWithId = generateTransferWithId(enteredId, answerAmount, TRANSFER_SUCCESS, TRANSFER_SEND);

                sendTheBucks(userService.getUser(enteredId),currentUser.getUser(),transferWithId);
//                //Actual Sending Portion
//                //DEPOSIT INTO TARGET
//                transferService.transfer(userService.getUser(enteredId), transferWithId, false);
//                //WITHDRAW FROM USER
//                transferService.transfer(currentUser.getUser(), transferWithId, true);
//                System.out.print("TRANSFER CREATED, MONEY HAS BEEN AUTOMATICALLY APPROVED AND SENT.");
            }
        }
        catch(Exception e)
        {
            System.out.println("Invalid input. Try Again.");
        }
	}

	private void requestBucks()
    {
        consoleService.getUserList(userService.getUsers());
        int enteredId = consoleService.promptForInt("Enter ID of user you want to request TEBucks from: ");
        try
        {
            //Get a Big Decimal from the user
            BigDecimal answerAmount = consoleService.promptForBigDecimal("How many TEBucks do you want to request from user " + enteredId + " :");
            Transfer transferWithId = generateTransferWithId(enteredId, answerAmount, TRANSFER_PENDING, TRANSFER_REQUEST);
            System.out.print("TRANSFER CREATED, REQUEST HAS BEEN SENT.");
        }
        catch(Exception e)
        {
            e.getMessage();
        }
	}

    private void viewPendingRequests() {
        //Calls the transfer Menu; will print the transfers selected and then prompt to view more details.
        transferMenu(TRANSFER_PENDING);
    }

    public String convertStatus(int status) {
        String newStatus = "";
        switch (status) {
            case 1:
                newStatus = "Pending";
                break;
            case 2:
                newStatus = "Approved";
                break;
            case 3:
                newStatus = "Rejected";
                break;
            default:
        }
        return newStatus;
    }

    private List<Transfer> fetchHistory(int historyType) {
        //If history input selection was for past transfers, then we call function again with rejects as well.
        if (historyType == TRANSFER_SUCCESS) {
            fetchHistory(TRANSFER_REJECT);
        }

        String status = "";
        String name = "";


        //Get the current users ID
        User user = currentUser.getUser();
        Long id = user.getId();
        int useID = id.intValue();

        //Request list of accounts from server using AccID and UserID
        List<Transfer> transferList = transferService.getTransferTransactions(useID, getAccountId());

        //check if list is empty, if so print empty history report
        if (transferList.isEmpty()) {
            consoleService.printHistoryHeader();
            consoleService.printEmptyHistory();
        } else {

            //Print the header, then check if each transaction is a "to" or "from" transfer
            //once this is known send to console print method to print correctly.
            consoleService.printHistoryHeader();
            for (Transfer transfer : transferList) {
                if (transfer.getAmount() != null && transfer.getStatusId() == historyType) {
                    boolean isFrom = isFrom(transfer,getAccountId());
                    name = accountService.findUserNameByAccountId(((isFrom) ? transfer.getAccountTo():transfer.getAccountFrom()));
                    status = convertStatus(transfer.getStatusId());
                    consoleService.printTransHistory(transfer.getId(),name,
                            transfer.getAmount(), status, isFrom);
                }
            }
        }
        return transferList;
    }

    private void transferMenu(int historySelection) {
        int userInput = -1;

        while (userInput != 0) {


            //Calls the Fetch History helper method, ask which history we are looking for;
            List<Transfer> transferList = fetchHistory(historySelection);
            consoleService.printTransferSelection();
            userInput = consoleService.promptForMenuSelection("Enter Selection: ");
            if (userInput == 0) {
                break;
            }
            Transfer selectedTransfer = transferService.getSelectedTransaction(transferList, userInput);
            String fromAccount = accountService.findUserNameByAccountId(selectedTransfer.getAccountFrom());

            if (selectedTransfer == null) {
                System.out.println("Incorrect Entry please try again");
            } else {
                consoleService.printTransferDetails(selectedTransfer, fromAccount, accountService.findUserNameByAccountId(selectedTransfer.getAccountTo()));
            }
            //Checks to see if the transaction is pending, if it is and the user is the account sending funds, prompt to approve
            if (historySelection == TRANSFER_PENDING && currentUser.getUser().getUsername().equals(fromAccount) ){
                //promptToApprove
                consoleService.printPendingMenu();
                int menuSelection = consoleService.promptForMenuSelection("Please enter your selection: ");
                if (menuSelection == 1){
                  //Deposit Money to account
                    //if its a from, then the other to must be the account ID for the other user
                    //if its a to, then the other from must be the account ID for the other user
                    boolean isThisFromOrTo = isFrom(selectedTransfer,getAccountId());

                    int targetAccount = isThisFromOrTo ? selectedTransfer.getAccountTo(): selectedTransfer.getAccountFrom();

                    User targetUser = userService.getUser(userService.getUserByAccountId(targetAccount,isThisFromOrTo,selectedTransfer.getId()));
                    sendTheBucks(targetUser, currentUser.getUser(),selectedTransfer);
                    selectedTransfer.setStatusId(TRANSFER_SUCCESS);
                    transferService.updateTransfer(selectedTransfer);

                } else if (menuSelection == 2){
                    selectedTransfer.setStatusId(TRANSFER_REJECT);
                    transferService.updateTransfer(selectedTransfer);
                } else if (menuSelection == 0) {
                    break;
                }
            }
        }
    }
    private int getAccountId(){
        List<Account> account = accountService.getAccounts(currentUser.getUser());
        int accId = 0;
        for (Account accounts : account) {
            accId = accounts.getAccountId();
        }
        return accId;
    }

    private boolean isFrom(Transfer transfer, int accountId){
        //System.out.println("In isFrom: " + transfer.getAccountFrom());
        //System.out.println("In isFrom: " + accountId);
        return (transfer.getAccountFrom() == accountId);
    }

    private void sendTheBucks(User targetUser, User sendingUser, Transfer transfer){
        //DEPOSIT INTO TARGET
        System.out.println(targetUser.getUsername() + " is getting");
        transferService.transfer(targetUser, transfer, false);
        //WITHDRAW FROM USER
        System.out.println(sendingUser.getUsername() + " is losing");
        transferService.transfer(sendingUser, transfer, true);
        System.out.println("TRANSFER APPROVED, MONEY HAS BEEN APPROVED AND SENT.");

    }
}