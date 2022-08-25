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

        if (accountService.hasMultipleAccounts(accounts)){
            consoleService.printAvailableAccounts(accounts);
            int selection = consoleService.promptForInt("Select the Account to view its balance");
            consoleService.displayBalanceForAccount(accounts.get(selection));
        } else {
            consoleService.displayBalanceForAccount(accounts.get(0));
        }
	}

    //Natalie's Edits
	private void viewTransferHistory() {

        int accId = 0;
        String status = "";
        //Transfer transfer = new Transfer();
        List<Transfer> transfer = new ArrayList<>();
        List<Account> account = accountService.getAccounts(currentUser.getUser());
        for (Account accounts : account){
           accId = accounts.getAccountId();
        }
        User user  = currentUser.getUser();
        Long id = user.getId();
        int useID = id.intValue();
        transfer = transferService.getTransferTransactions(useID,accId);
        if(transfer.isEmpty()) {
            consoleService.printHistoryHeader();
            System.out.println("No current transfers in the system.");
        } else {
            consoleService.printHistoryHeader();
            for (Transfer transferlist : transfer) {
                if (transferlist.getAmount() != null && transferlist.getStatusId() != TRANSFER_PENDING) {
                    boolean isFrom = (transferlist.getAccountFrom() == accId);
                    status = convertStatus(transferlist.getStatusId());
                    consoleService.printTransHistory(transferlist.getId(),((isFrom==true) ? transferlist.getAccountTo() : transferlist.getAccountFrom()),
                            transferlist.getAmount(), status,isFrom);
                }
            }
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        int accId = 0;
        String status = "";
        //Transfer transfer = new Transfer();
        List<Transfer> transfer = new ArrayList<>();
        List<Account> account = accountService.getAccounts(currentUser.getUser());
        for (Account accounts : account){
            accId = accounts.getAccountId();
        }
        User user  = currentUser.getUser();
        Long id = user.getId();
        int useID = id.intValue();
        int option = 2;
        transfer = transferService.getTransferTransactions(useID,accId);
        if(transfer.isEmpty()) {
            consoleService.printHistoryHeader();
            System.out.println("No current transfers in the system.");
        } else {
            consoleService.printHistoryHeader();
            for (Transfer transferlist : transfer) {
                if (transferlist.getAmount() != null && transferlist.getStatusId() == TRANSFER_PENDING) {
                    boolean isFrom = (transferlist.getAccountFrom() == accId);
                    status = convertStatus(transferlist.getStatusId());
                    consoleService.printTransHistory(transferlist.getId(),((isFrom==true) ? transferlist.getAccountTo() : transferlist.getAccountFrom()),
                            transferlist.getAmount(), status,isFrom);
                }

            }
        }
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
                Transfer transferWithId = generateTransferWithId(enteredId, answerAmount, 2, 2);

                //Actual Sending Portion
                //DEPOSIT INTO TARGET
                transferService.transfer(userService.getUser(enteredId), transferWithId, false);
                //WITHDRAW FROM USER
                transferService.transfer(currentUser.getUser(), transferWithId, true);
                System.out.print("TRANSFER CREATED, MONEY HAS BEEN AUTOMATICALLY APPROVED AND SENT.");
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
            Transfer transferWithId = generateTransferWithId(enteredId, answerAmount, 1, 1);
            System.out.print("TRANSFER CREATED, REQUEST HAS BEEN SENT.");
        }
        catch(Exception e)
        {
            e.getMessage();
        }
	}

    public String convertStatus(int status) {
        String newStatus = "";
        switch (status){
            case 1 : newStatus = "Pending";
                break;
            case 2 : newStatus = "Approved";
                break;
            case 3 : newStatus = "Rejected";
                break;
            default:
        }
        return newStatus;
    }
}
