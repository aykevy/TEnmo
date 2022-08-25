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

    private void viewPendingRequests() {
        //Calls the transfer Menu; will print the transfers selected and then prompt to view more details.
        transferMenu(TRANSFER_PENDING);
    }

    //MAIN FUNCTION, Still Working On It
    private void sendBucks() {
        //Prompt user to pick a transfer to from list.
        consoleService.getUserList(userService.getUsers());
        int enteredId = consoleService.promptForInt("Enter ID of user you are sending TEBucks to: ");
        try {
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
            Transfer transfer = transferService.createNewTransfer(1, 2, userAccount.getAccountId(), transferAccount.getAccountId(), answerAmount);
            Transfer transferWithGeneratedID = transferService.add(transfer);
            System.out.println(transferWithGeneratedID.getId());
            System.out.println(transferWithGeneratedID);
            System.out.print("TRANSFER OBJECT WITH GENERATED ID SUCCESSFUL, END FOR NOW");


            //DO THE ACTUAL TRANSFER NEXT
            //transferService.transfer(userAccount, transferModel, true);


        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void requestBucks() {
        // TODO Auto-generated method stub

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

        int accId = 0;
        int acctID = 0;
        String status = "";
        String name = "";

        //Get list of accounts for the current user
        List<Account> account = accountService.getAccounts(currentUser.getUser());
        for (Account accounts : account) {
            accId = accounts.getAccountId();
        }

        //Get the current users ID
        User user = currentUser.getUser();
        Long id = user.getId();
        int useID = id.intValue();

        //Request list of accounts from server using AccID and UserID
        List<Transfer> transferList = transferService.getTransferTransactions(useID, accId);

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
                    boolean isFrom = (transfer.getAccountFrom() == accId);
                    name = accountService.findUserNameByAccountId(((isFrom) ? transfer.getAccountTo() : transfer.getAccountFrom()));
                    System.out.println("this is the "+name);
                    for (Account accounts : account) {
                        accId = accounts.getAccountId();
                    }
                    status = convertStatus(transfer.getStatusId());
                    consoleService.printTransHistory(transfer.getId(),((isFrom) ? transfer.getAccountTo() : transfer.getAccountFrom()),
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
            Transfer selectedTransfer = transferService.getSelectedTransaction(transferList, userInput);
            if (userInput == 0) {
                break;
            } else if (selectedTransfer == null) {
                System.out.println("Incorrect Entry please try again");
            } else {
                consoleService.printTransferDetails(selectedTransfer);
                consoleService.pause();
            }
        }
    }
}