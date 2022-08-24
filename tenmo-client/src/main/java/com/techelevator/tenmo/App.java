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
        // TODO Auto-generated method stub
       int accId = 0;
        List<Transfer> transfer = new ArrayList<>();
        List<Account> account = accountService.getAccounts(currentUser.getUser());
        for (Account accounts : account){
           accId = accounts.getAccountId();
        }
        User user  = currentUser.getUser();
        Long id = user.getId();
        int useID = id.intValue();
        System.out.println("this is id: "+id + " Int value "+ useID + " acctid "+accId);
        transfer = transferService.getTransferTransactions(useID,accId);
        System.out.println("before if " + transfer);
        if(transfer != (null)) {
            consoleService.printHistoryHeader();
            for (Transfer transferlist : transfer) {
                System.out.println("before If");
                if (transferlist.getAmount() != null && transferlist.getStatusId() != TRANSFER_PENDING) {
                    System.out.println("before isFrom");
                    boolean isFrom = (transferlist.getId() == 1);
                    consoleService.printTransHistory(transferlist.getId(),(transferlist.getId() == 1 ? transferlist.getAccountTo() : transferlist.getAccountFrom()),transferlist.getAmount(),isFrom);
                    System.out.println("console services");
                } else {
                    System.out.println("No current transfers in the system.");
                }
            }
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}


    //MAIN FUNCTION, Still Working On It
	private void sendBucks() {
        //Prompt user to pick a transfer to from list.
        consoleService.getUserList(userService.getUsers());
        int enteredId = consoleService.promptForInt("Enter ID of user you are sending TEBucks to: ");
        try
        {
            //Get a Big Decimal from the user
            BigDecimal answerAmount = consoleService.promptForBigDecimal("How many TEBucks do you want to send to user " + enteredId + " :");

            //Current User Account, currentUser = AuthenticatedUser
            List<Account> currentUserAccounts = accountService.getAccounts(currentUser.getUser());
            Account userAccount = currentUserAccounts.get(0);

            //Target Account For Transfer
            User targetTransferUser = userService.getUser(enteredId);
            List<Account> transferUserAccounts = accountService.getAccounts(targetTransferUser);
            Account transferAccount = transferUserAccounts.get(0);

            //HERE CREATE AND DOCUMENT THIS CURRENT TRANSFER
            //Use Transfer services to create a new transfer
            Transfer transfer = transferService.createNewTransfer(1,2, userAccount.getAccountId(),transferAccount.getAccountId(),answerAmount);
            Transfer transferWithGeneratedID = transferService.add(transfer);
            System.out.println(transferWithGeneratedID.getId());
            System.out.println(transferWithGeneratedID);
            System.out.print("TRANSFER OBJECT WITH GENERATED ID SUCCESSFUL, END FOR NOW");


            //DO THE ACTUAL TRANSFER NEXT
            //transferService.transfer(userAccount, transferModel, true);


        }
        catch(Exception e)
        {
            e.getMessage();
        }
		
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
