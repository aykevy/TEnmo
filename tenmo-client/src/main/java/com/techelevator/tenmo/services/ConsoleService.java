package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printAvailableAccounts(List<Account> accounts) {
        if (accounts.size() > 1) {
            for (Account account : accounts) {
                int count = 1;
                System.out.println("The following accounts are available:");
                System.out.println(count + ". " + account.getAccountId());
            }
        }
    }

    public void displayBalanceForAccount(Account account) {
        String accountPrint = "*Account Number: " + account.getAccountId()+ "*";
        String balancePrint = "*Total Balance: " + account.getBalance() + "*";
        printStars(accountPrint, balancePrint);
        System.out.println(accountPrint);
        System.out.println(balancePrint);
        printStars(accountPrint, balancePrint);
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    public void getUserList(List<User> users)
    {
        List<Integer> userIds = new ArrayList<>();

        System.out.println("Users ID        User Name");
        System.out.println("------------------------------");
        for (User user : users)
        {
            System.out.println(user.getId() + "          " + user.getUsername());
            userIds.add(Math.toIntExact(user.getId()));
        }
    }
    public void printTransHistory(int id, int account, BigDecimal amount,String status, Boolean isFrom){
        //System.out.println("IsFrom");
        if (isFrom) {
            System.out.println(id + "  " + "        To: " + account + "         $ " + amount+ "   "+ status);
        } else{
            System.out.println(id + "  " + "      From: " + account + "         $ " + amount+ "   "+ status);
        }
    }


    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }
    public void printHistoryHeader(){
        System.out.println("--------------------------------------------------");
        System.out.println("Transfer");
        System.out.println("ID            From/To           Amount     Status");
        System.out.println("--------------------------------------------------");
    }

    private void printStars(String account, String balance) {
        if (account.length() > balance.length()) {
            for (int i = 0; i < account.length()+1; i++) {
                if (i == account.length()) {
                    System.out.println("*");
                } else {
                    System.out.print("*");
                }

            }
        } else {
            for (int i = 0; i < balance.length()+1; i++) {
                if (i == balance.length()) {
                    System.out.println("*");
                } else {
                    System.out.print("*");
                }
            }
        }
    }
}