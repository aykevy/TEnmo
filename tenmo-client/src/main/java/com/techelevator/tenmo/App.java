package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
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
        List<Transfer> transfers = accountService.getTransactions(currentUser.getUser());
        if(transfers != (null)) {
            System.out.println("---------------------------------------------");
            System.out.println("Transfers");
            System.out.println("ID                 From/To           Amount  ");
            System.out.println("---------------------------------------------");
            for (Transfer transfer : transfers) {
                if (transfer.getAmount() != null) {
                    System.out.println(transfer.getId() + "  " + (transfer.getId() == 1 ? "To:    " + transfer.getAccountTo() :  "From: " +
                            transfer.getAccountFrom()) + "         $ " + transfer.getAmount());
                } else System.out.println("No current transfers in the system.");
            }
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

    //Kevin's Edits, Helper Function For User List (TO BE MOVED LATER)
    private List<Integer> getUserList()
    {
        List<User> users = userService.getUsers();
        List<Integer> userIds = new ArrayList<>();

        System.out.println("Users ID        User Name");
        System.out.println("------------------------------");
        for (User user : users)
        {
            System.out.println(user.getId() + "          " + user.getUsername());
            userIds.add(Math.toIntExact(user.getId()));
        }
        return userIds;
    }

    //MAIN FUNCTION, Still Working On It
	private void sendBucks() {
        List<Integer> userIds = getUserList();
        System.out.println("Enter ID of user you are sending to (0 to cancel)");
        Scanner userInput = new Scanner(System.in);
        String answerId = userInput.nextLine();
        try
        {
            Integer enteredId = Integer.parseInt(answerId);
            System.out.println("How much do you want to send to user: " + enteredId + " ?");
            BigDecimal answerAmount = userInput.nextBigDecimal();

            //Current User Account
            List<Account> userAccounts = accountService.getAccounts(currentUser.getUser());
            Account userAccount = null;
            if (userAccounts.size() == 1)
            {
                userAccount = userAccounts.get(0);
            }

            //Target Account For Transfer
            User targetTransferUser = userService.getUser(Integer.parseInt(answerId));
            List<Account> transferUserAccounts = accountService.getAccounts(targetTransferUser);
            Account transferAccount = null;
            if (transferUserAccounts.size() == 1)
            {
                transferAccount = userAccounts.get(0);
            }

            //CONTINUE FORM HERE

            //HERE CREATE AND DOCUMENT THIS CURRENT TRANSFER

            //transferService.createTransfer(


            //DO THE ACTUAL TRANSFER
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
