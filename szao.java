
import java.io.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class szao {

    static final String FILE_NAME = "BCszao.txt";
    static Scanner sc = new Scanner(System.in);
    static long[] accountIds = new long[100];
    static String[] accountNames = new String[100];
    static String[] accountPasswords = new String[100];
    static double[] wallet = new double[100];
    static double[] savings = new double[100];
    static double[] loanBalance = new double[100];
    static LocalDate[] lastSavingsInterest = new LocalDate[100];
    static LocalDate[] loanStart = new LocalDate[100];
    static int accountCount = 0;
    static long nextId = 66671001;

    public static void main(String[] args) {
        loadData();
        System.out.println("Welcome to Banco Centro");

        while (true) {
            System.out.println("\n1. Create Account\n2. Login\n3. Currency Exchange\n4. Exit");
            System.out.print("Choose: ");
            String c = sc.nextLine();

            switch (c) {
                case "1" ->
                    createAccount();
                case "2" ->
                    login();
                case "3" ->
                    currencyExchange();
                case "4" -> {
                    saveData();
                    System.out.println("Thank you for using Banco Centro.");
                    System.exit(0);
                }
                default ->
                    System.out.println("Invalid option.");
            }
        }
    }

    static void currencyExchange() {
        while (true) {
            System.out.println("\nCURRENCY EXCHANGE");
            System.out.println("1. Peso to Dollar");
            System.out.println("2. Dollar to Peso");
            System.out.println("3. Euro to Dollar");
            System.out.println("4. Euro to Peso");
            System.out.println("5. Back");

            String c = sc.nextLine();
            if (c.equals("5")) {
                return;
            }

            System.out.print("\nEnter amount: ");
            double amt = Double.parseDouble(sc.nextLine());
            if (amt <= 0) {
                System.out.println("\nInvalid amount.");
                continue;
            }

            double result = 0;
            switch (c) {
                case "1" ->
                    result = amt / 56.0;
                case "2" ->
                    result = amt * 56.0;
                case "3" ->
                    result = amt * 1.08;
                case "4" ->
                    result = amt * 60.0;
                default ->
                    System.out.println("\nInvalid option.");
            }
            System.out.println("\nConverted amount: " + result);
        }
    }

    static void createAccount() {
        System.out.print("\nEnter name: ");
        String name = sc.nextLine();
        System.out.print("Create a password: ");
        String password = sc.nextLine();

        accountIds[accountCount] = nextId++;
        accountNames[accountCount] = name;
        accountPasswords[accountCount] = password;
        wallet[accountCount] = 0;
        savings[accountCount] = 0;
        loanBalance[accountCount] = 0;
        lastSavingsInterest[accountCount] = LocalDate.now();
        loanStart[accountCount] = null;

        System.out.println("\nAccount created, \nID: " + accountIds[accountCount]);
        accountCount++;
        saveData();
    }

    static void login() {
        System.out.print("\nEnter ID: ");
        long id = Long.parseLong(sc.nextLine());
        int accIndex = -1;

        for (int i = 0; i < accountCount; i++) {
            if (accountIds[i] == id) {
                accIndex = i;
                break;
            }
        }

        if (accIndex == -1) {
            System.out.println("\nAccount not found.");
            return;
        }

        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        if (!accountPasswords[accIndex].equals(pass)) {
            System.out.println("\nIncorrect password.");
            return;
        }

        applySavingsInterest(accIndex);
        applyLoanInterest(accIndex);

        while (true) {
            System.out.println("\n~~~~~~Welcome, " + accountNames[accIndex] + "~~~~~~");
            System.out.println("\n1.Wallet\n2.Savings\n3.Loan\n4.Summary\n5.Logout");
            System.out.print("Choose: ");
            String c = sc.nextLine();

            switch (c) {
                case "1" ->
                    walletMenu(accIndex);
                case "2" ->
                    savingsMenu(accIndex);
                case "3" ->
                    loanMenu(accIndex);
                case "4" ->
                    summary(accIndex);
                case "5" -> {
                    saveData();
                    return;
                }
                default ->
                    System.out.println("\nInvalid option.");
            }
        }
    }

    static void walletMenu(int accIndex) {
        while (true) {
            System.out.println("\n~~~~~~Wallet Balance: ₱" + wallet[accIndex] + "~~~~~~");
            System.out.println("\n1.Deposit\n2.Withdraw\n3.Back");
            String c = sc.nextLine();

            if (c.equals("3")) {
                return;
            }

            System.out.print("Amount: ");
            double amt = Double.parseDouble(sc.nextLine());
            if (amt <= 0) {
                System.out.println("Invalid amount.");
                continue;
            }

            if (c.equals("1")) {
                wallet[accIndex] += amt;
            } else if (c.equals("2")) {
                if (wallet[accIndex] >= amt) {
                    wallet[accIndex] -= amt;
                } else {
                    System.out.println("Not enough balance.");
                }
            }
        }
    }

    static void savingsMenu(int accIndex) {
        while (true) {
            applySavingsInterest(accIndex);
            System.out.println("\n~~~~~~Savings Balance: ₱" + savings[accIndex] + "~~~~~~");
            System.out.println("\n1.Deposit from Wallet\n2.Withdraw to Wallet\n3.Back");
            String c = sc.nextLine();

            if (c.equals("3")) {
                return;
            }

            System.out.print("\nAmount: ");
            double amt = Double.parseDouble(sc.nextLine());
            if (amt <= 0) {
                System.out.println("\nInvalid amount.");
                continue;
            }

            if (c.equals("1")) {
                if (wallet[accIndex] >= amt) {
                    wallet[accIndex] -= amt;
                    savings[accIndex] += amt;
                } else {
                    System.out.println("\nNot enough balance.");
                }
            } else if (c.equals("2")) {
                if (savings[accIndex] >= amt) {
                    savings[accIndex] -= amt;
                    wallet[accIndex] += amt;
                } else {
                    System.out.println("\nNot enough balance.");
                }
            }
        }
    }

    static void loanMenu(int accIndex) {
        while (true) {
            applyLoanInterest(accIndex);
            System.out.println("\n~~~~~~Loan Balance: ₱" + loanBalance[accIndex] + "~~~~~~");
            System.out.println("\n1.Request Loan\n2.Pay Loan\n3.Back");
            String c = sc.nextLine();

            if (c.equals("3")) {
                return;
            }

            if (c.equals("1")) {
                double limit = getLoanLimit(wallet[accIndex]);
                if (limit == 10000) {
                    System.out.println("\nYour balance does not qualify for a loan.");
                    continue;
                }

                System.out.println("\nMaximum loan you can borrow: ₱" + limit);
                System.out.print("\nLoan amount: ");
                double amt = Double.parseDouble(sc.nextLine());

                if (amt <= 0 || amt > limit) {
                    System.out.println("\nInvalid loan amount.");
                    continue;
                }

                loanBalance[accIndex] += amt;
                wallet[accIndex] += amt;
                loanStart[accIndex] = LocalDate.now();
                System.out.println("\nLoan sanctioned and added to wallet.");
            } else if (c.equals("2")) {
                System.out.print("\nPayment amount: ");
                double amt = Double.parseDouble(sc.nextLine());

                if (amt <= 0) {
                    System.out.println("Invalid amount.");
                    continue;
                }

                if (wallet[accIndex] >= amt) {
                    wallet[accIndex] -= amt;
                    loanBalance[accIndex] -= amt;
                    if (loanBalance[accIndex] < 0) {
                        loanBalance[accIndex] = 0;
                    }
                    System.out.println("\nLoan payment successful.");
                } else {
                    System.out.println("\nNot enough wallet balance.");
                }
            }
        }
    }

    static void applySavingsInterest(int accIndex) {
        if (lastSavingsInterest[accIndex].isBefore(LocalDate.now())) {
            savings[accIndex] *= 1.02;
            lastSavingsInterest[accIndex] = LocalDate.now();
        }
    }

    static double getLoanLimit(double walletBalance) {
        if (walletBalance >= 10000 && walletBalance <= 20000) {
            return 5000;
        } else if (walletBalance >= 20000 && walletBalance < 25000) {
            return 10000;
        } else if (walletBalance >= 30000 && walletBalance <= 50000) {
            return 20000;
        } else if (walletBalance > 50000) {
            return 30000;
        } else {
            return 0;
        }
    }

    static void applyLoanInterest(int accIndex) {
        if (loanBalance[accIndex] <= 0 || loanStart[accIndex] == null) {
            return;
        }

        long days = ChronoUnit.DAYS.between(loanStart[accIndex], LocalDate.now());

        if (days > 120) {
            loanBalance[accIndex] *= 1.15;
            loanStart[accIndex] = LocalDate.now();
        } else if (days > 30) {
            loanBalance[accIndex] *= 1.07;
            loanStart[accIndex] = LocalDate.now();
        }
    }

    static void summary(int accIndex) {
        System.out.println("\n~~~~~~SUMMARY~~~~~~");
        System.out.println("Name: " + accountNames[accIndex]);
        System.out.println("Wallet: ₱" + wallet[accIndex]);
        System.out.println("Savings: ₱" + savings[accIndex]);
        System.out.println("Loan: ₱" + loanBalance[accIndex]);
    }

    static void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            pw.println(accountCount);
            pw.println(nextId);
            for (int i = 0; i < accountCount; i++) {
                pw.println(accountIds[i] + ", " + accountNames[i] + ", "
                        + accountPasswords[i] + ", " + wallet[i] + ", " + savings[i] + ", "
                        + loanBalance[i] + ", " + lastSavingsInterest[i] + ", "
                        + (loanStart[i] != null ? loanStart[i] : "null"));
            }
        } catch (Exception e) {
            System.out.println("Save error.");
        }
    }

    static void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            accountCount = Integer.parseInt(br.readLine());
            nextId = Long.parseLong(br.readLine());
            for (int i = 0; i < accountCount; i++) {
                String[] parts = br.readLine().split(", ");
                accountIds[i] = Long.parseLong(parts[0]);
                accountNames[i] = parts[1];
                accountPasswords[i] = parts[2];
                wallet[i] = Double.parseDouble(parts[3]);
                savings[i] = Double.parseDouble(parts[4]);
                loanBalance[i] = Double.parseDouble(parts[5]);
                lastSavingsInterest[i] = LocalDate.parse(parts[6]);
                loanStart[i] = parts[7].equals("null") ? null : LocalDate.parse(parts[7]);
            }
        } catch (Exception e) {
            accountCount = 0;
        }
    }
}
