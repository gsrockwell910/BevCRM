package com.gavinrockwell.bevcrm.ui;

import com.gavinrockwell.bevcrm.dao.AccountDAO;
import com.gavinrockwell.bevcrm.dao.ContactDAO;
import com.gavinrockwell.bevcrm.dao.OrderDAO;
import com.gavinrockwell.bevcrm.model.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based user interface for the BevCRM application.
 *
 * <p>Presents a hierarchical menu system that allows the user to manage
 * accounts, contacts, and orders, and to view summary reports. All input
 * is read from {@link System#in} and output is written to {@link System#out}.
 *
 * <p>This class follows the Single Responsibility Principle — it handles
 * only presentation and user input; all data operations are delegated to
 * the appropriate DAO class.
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public class ConsoleUI {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final String  DIVIDER   = "─".repeat(60);
    private static final String  THIN      = "·".repeat(60);
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy");

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final Scanner     scanner;
    private final AccountDAO  accountDAO;
    private final ContactDAO  contactDAO;
    private final OrderDAO    orderDAO;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Constructs the UI with its required DAOs.
     *
     * @param accountDAO  DAO for account operations
     * @param contactDAO  DAO for contact operations
     * @param orderDAO    DAO for order operations
     */
    public ConsoleUI(AccountDAO accountDAO, ContactDAO contactDAO, OrderDAO orderDAO) {
        this.scanner    = new Scanner(System.in);
        this.accountDAO = accountDAO;
        this.contactDAO = contactDAO;
        this.orderDAO   = orderDAO;
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    /**
     * Launches the main menu loop. Runs until the user selects Exit.
     */
    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            running = showMainMenu();
        }
        System.out.println("\n  Goodbye.\n");
    }

    // ── Menus ─────────────────────────────────────────────────────────────────

    /**
     * Displays the main menu and routes the user's selection.
     *
     * @return {@code false} when the user chooses to exit
     */
    private boolean showMainMenu() {
        System.out.println("\n" + DIVIDER);
        System.out.println("  MAIN MENU");
        System.out.println(DIVIDER);
        System.out.println("  1. Accounts");
        System.out.println("  2. Contacts");
        System.out.println("  3. Orders");
        System.out.println("  4. Reports");
        System.out.println("  0. Exit");
        System.out.println(DIVIDER);

        switch (promptInt("Select: ", 0, 4)) {
            case 1 -> showAccountMenu();
            case 2 -> showContactMenu();
            case 3 -> showOrderMenu();
            case 4 -> showReports();
            case 0 -> { return false; }
        }
        return true;
    }

    // ── Account menu ──────────────────────────────────────────────────────────

    private void showAccountMenu() {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ACCOUNTS");
        System.out.println(DIVIDER);
        System.out.println("  1. List all accounts");
        System.out.println("  2. View account detail");
        System.out.println("  3. Add account");
        System.out.println("  4. Edit account");
        System.out.println("  5. Delete account");
        System.out.println("  0. Back");
        System.out.println(DIVIDER);

        switch (promptInt("Select: ", 0, 5)) {
            case 1 -> listAccounts();
            case 2 -> viewAccount();
            case 3 -> addAccount();
            case 4 -> editAccount();
            case 5 -> deleteAccount();
            case 0 -> { /* back */ }
        }
    }

    private void listAccounts() {
        try {
            List<Account> accounts = accountDAO.findAll();
            if (accounts.isEmpty()) { System.out.println("\n  No accounts found."); return; }
            System.out.println();
            System.out.printf("  %-4s %-30s %-14s %-18s%n", "ID", "Name", "Type", "City, State");
            System.out.println("  " + THIN);
            for (Account a : accounts) {
                System.out.printf("  %-4d %-30s %-14s %-18s%n",
                        a.getId(), a.getName(), a.getType().label(),
                        formatLocation(a.getCity(), a.getState()));
            }
        } catch (SQLException e) {
            printError("Could not load accounts", e);
        }
    }

    private void viewAccount() {
        int id = promptInt("\n  Account ID: ", 1, Integer.MAX_VALUE);
        try {
            Optional<Account> opt = accountDAO.findById(id);
            if (opt.isEmpty()) { System.out.println("  Account not found."); return; }
            Account a = opt.get();

            System.out.println("\n" + DIVIDER);
            System.out.printf("  %s%n", a.getName());
            System.out.println(THIN);
            System.out.printf("  Type    : %s%n", a.getType().label());
            System.out.printf("  Phone   : %s%n", nullSafe(a.getPhone()));
            System.out.printf("  Email   : %s%n", nullSafe(a.getEmail()));
            System.out.printf("  Location: %s%n", formatLocation(a.getCity(), a.getState()));
            System.out.printf("  Created : %s%n", a.getCreatedAt().format(DATE_FMT));

            // Contacts
            List<Contact> contacts = contactDAO.findByAccountId(id);
            System.out.println("\n  Contacts (" + contacts.size() + ")");
            System.out.println("  " + THIN);
            if (contacts.isEmpty()) {
                System.out.println("  No contacts.");
            } else {
                for (Contact c : contacts) {
                    System.out.printf("  %-25s  %s%n", c.getFullName(), nullSafe(c.getTitle()));
                }
            }

            // Orders
            List<Order> orders = orderDAO.findByAccountId(id);
            System.out.println("\n  Orders (" + orders.size() + ")");
            System.out.println("  " + THIN);
            if (orders.isEmpty()) {
                System.out.println("  No orders.");
            } else {
                for (Order o : orders) {
                    System.out.printf("  #%-4d %-30s  Qty: %-5d  $%-10.2f  %s%n",
                            o.getId(), o.getProductName(), o.getQuantity(),
                            o.getTotalValue(), o.getStatus().label());
                }
            }
            System.out.println(DIVIDER);

        } catch (SQLException e) {
            printError("Could not load account", e);
        }
    }

    private void addAccount() {
        System.out.println("\n  New Account");
        System.out.println("  " + THIN);

        String name  = promptString("  Name  : ");
        AccountType type = promptAccountType();
        String phone = promptString("  Phone : ");
        String email = promptString("  Email : ");
        String city  = promptString("  City  : ");
        String state = promptString("  State : ");

        Account account = new Account.Builder(name, type)
                .phone(phone).email(email).city(city).state(state).build();

        try {
            Account saved = accountDAO.insert(account);
            System.out.printf("%n  ✓ Account '%s' created (ID %d).%n", saved.getName(), saved.getId());
        } catch (SQLException e) {
            printError("Could not create account", e);
        }
    }

    private void editAccount() {
        int id = promptInt("\n  Account ID to edit: ", 1, Integer.MAX_VALUE);
        try {
            Optional<Account> opt = accountDAO.findById(id);
            if (opt.isEmpty()) { System.out.println("  Account not found."); return; }
            Account a = opt.get();

            System.out.println("  (Press Enter to keep current value)\n");
            a.setName(promptOrKeep("  Name  [" + a.getName()  + "]: ", a.getName()));
            a.setType(promptAccountTypeOrKeep(a.getType()));
            a.setPhone(promptOrKeep("  Phone [" + nullSafe(a.getPhone()) + "]: ", a.getPhone()));
            a.setEmail(promptOrKeep("  Email [" + nullSafe(a.getEmail()) + "]: ", a.getEmail()));
            a.setCity(promptOrKeep("  City  [" + nullSafe(a.getCity())  + "]: ", a.getCity()));
            a.setState(promptOrKeep("  State [" + nullSafe(a.getState()) + "]: ", a.getState()));

            if (accountDAO.update(a)) {
                System.out.println("\n  ✓ Account updated.");
            } else {
                System.out.println("\n  Nothing updated.");
            }
        } catch (SQLException e) {
            printError("Could not update account", e);
        }
    }

    private void deleteAccount() {
        int id = promptInt("\n  Account ID to delete: ", 1, Integer.MAX_VALUE);
        System.out.print("  This will also delete all contacts and orders. Confirm? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("  Cancelled.");
            return;
        }
        try {
            if (accountDAO.delete(id)) {
                System.out.println("  ✓ Account deleted.");
            } else {
                System.out.println("  Account not found.");
            }
        } catch (SQLException e) {
            printError("Could not delete account", e);
        }
    }

    // ── Contact menu ──────────────────────────────────────────────────────────

    private void showContactMenu() {
        System.out.println("\n" + DIVIDER);
        System.out.println("  CONTACTS");
        System.out.println(DIVIDER);
        System.out.println("  1. List all contacts");
        System.out.println("  2. Add contact");
        System.out.println("  3. Edit contact");
        System.out.println("  4. Delete contact");
        System.out.println("  0. Back");
        System.out.println(DIVIDER);

        switch (promptInt("Select: ", 0, 4)) {
            case 1 -> listContacts();
            case 2 -> addContact();
            case 3 -> editContact();
            case 4 -> deleteContact();
            case 0 -> { /* back */ }
        }
    }

    private void listContacts() {
        try {
            List<Contact> contacts = contactDAO.findAll();
            if (contacts.isEmpty()) { System.out.println("\n  No contacts found."); return; }
            System.out.println();
            System.out.printf("  %-4s %-25s %-22s %-10s%n", "ID", "Name", "Title", "Account ID");
            System.out.println("  " + THIN);
            for (Contact c : contacts) {
                System.out.printf("  %-4d %-25s %-22s %-10d%n",
                        c.getId(), c.getFullName(), nullSafe(c.getTitle()), c.getAccountId());
            }
        } catch (SQLException e) {
            printError("Could not load contacts", e);
        }
    }

    private void addContact() {
        System.out.println("\n  New Contact");
        System.out.println("  " + THIN);

        int    accountId = promptInt("  Account ID : ", 1, Integer.MAX_VALUE);
        String firstName = promptString("  First Name : ");
        String lastName  = promptString("  Last Name  : ");
        String title     = promptString("  Title      : ");
        String phone     = promptString("  Phone      : ");
        String email     = promptString("  Email      : ");

        Contact contact = new Contact(0, accountId, firstName, lastName,
                title, phone, email, null);
        try {
            Contact saved = contactDAO.insert(contact);
            System.out.printf("%n  ✓ Contact '%s' created (ID %d).%n",
                    saved.getFullName(), saved.getId());
        } catch (SQLException e) {
            printError("Could not create contact", e);
        }
    }

    private void editContact() {
        int id = promptInt("\n  Contact ID to edit: ", 1, Integer.MAX_VALUE);
        try {
            Optional<Contact> opt = contactDAO.findById(id);
            if (opt.isEmpty()) { System.out.println("  Contact not found."); return; }
            Contact c = opt.get();

            System.out.println("  (Press Enter to keep current value)\n");
            c.setFirstName(promptOrKeep("  First Name [" + c.getFirstName()       + "]: ", c.getFirstName()));
            c.setLastName(promptOrKeep("  Last Name  [" + c.getLastName()        + "]: ", c.getLastName()));
            c.setTitle(promptOrKeep("  Title      [" + nullSafe(c.getTitle())   + "]: ", c.getTitle()));
            c.setPhone(promptOrKeep("  Phone      [" + nullSafe(c.getPhone())   + "]: ", c.getPhone()));
            c.setEmail(promptOrKeep("  Email      [" + nullSafe(c.getEmail())   + "]: ", c.getEmail()));

            if (contactDAO.update(c)) {
                System.out.println("\n  ✓ Contact updated.");
            }
        } catch (SQLException e) {
            printError("Could not update contact", e);
        }
    }

    private void deleteContact() {
        int id = promptInt("\n  Contact ID to delete: ", 1, Integer.MAX_VALUE);
        try {
            if (contactDAO.delete(id)) {
                System.out.println("  ✓ Contact deleted.");
            } else {
                System.out.println("  Contact not found.");
            }
        } catch (SQLException e) {
            printError("Could not delete contact", e);
        }
    }

    // ── Order menu ────────────────────────────────────────────────────────────

    private void showOrderMenu() {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ORDERS");
        System.out.println(DIVIDER);
        System.out.println("  1. List all orders");
        System.out.println("  2. Add order");
        System.out.println("  3. Update order status");
        System.out.println("  4. Delete order");
        System.out.println("  0. Back");
        System.out.println(DIVIDER);

        switch (promptInt("Select: ", 0, 4)) {
            case 1 -> listOrders();
            case 2 -> addOrder();
            case 3 -> updateOrderStatus();
            case 4 -> deleteOrder();
            case 0 -> { /* back */ }
        }
    }

    private void listOrders() {
        try {
            List<Order> orders = orderDAO.findAll();
            if (orders.isEmpty()) { System.out.println("\n  No orders found."); return; }
            System.out.println();
            System.out.printf("  %-4s %-5s %-30s %-6s %-12s %-12s%n",
                    "ID", "AccID", "Product", "Qty", "Total", "Status");
            System.out.println("  " + THIN);
            for (Order o : orders) {
                System.out.printf("  %-4d %-5d %-30s %-6d $%-11.2f %s%n",
                        o.getId(), o.getAccountId(), o.getProductName(),
                        o.getQuantity(), o.getTotalValue(), o.getStatus().label());
            }
        } catch (SQLException e) {
            printError("Could not load orders", e);
        }
    }

    private void addOrder() {
        System.out.println("\n  New Order");
        System.out.println("  " + THIN);

        int        accountId   = promptInt("  Account ID   : ", 1, Integer.MAX_VALUE);
        String     productName = promptString("  Product Name : ");
        int        quantity    = promptInt("  Quantity     : ", 1, Integer.MAX_VALUE);
        BigDecimal unitPrice   = promptDecimal("  Unit Price   : $");

        Order order = new Order(0, accountId, productName, quantity,
                unitPrice, OrderStatus.PENDING, null);
        try {
            Order saved = orderDAO.insert(order);
            System.out.printf("%n  ✓ Order #%d created — Total: $%.2f%n",
                    saved.getId(), saved.getTotalValue());
        } catch (SQLException e) {
            printError("Could not create order", e);
        }
    }

    private void updateOrderStatus() {
        int id = promptInt("\n  Order ID: ", 1, Integer.MAX_VALUE);
        System.out.println("  New status:");
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, statuses[i].label());
        }
        int choice = promptInt("  Select: ", 1, statuses.length);
        OrderStatus newStatus = statuses[choice - 1];

        try {
            if (orderDAO.updateStatus(id, newStatus)) {
                System.out.printf("  ✓ Order #%d status updated to %s.%n", id, newStatus.label());
            } else {
                System.out.println("  Order not found.");
            }
        } catch (SQLException e) {
            printError("Could not update order status", e);
        }
    }

    private void deleteOrder() {
        int id = promptInt("\n  Order ID to delete: ", 1, Integer.MAX_VALUE);
        try {
            if (orderDAO.delete(id)) {
                System.out.println("  ✓ Order deleted.");
            } else {
                System.out.println("  Order not found.");
            }
        } catch (SQLException e) {
            printError("Could not delete order", e);
        }
    }

    // ── Reports ───────────────────────────────────────────────────────────────

    /**
     * Displays a summary dashboard: account counts by type, order pipeline
     * by status, and total revenue from delivered orders.
     */
    private void showReports() {
        System.out.println("\n" + DIVIDER);
        System.out.println("  REPORTS");
        System.out.println(DIVIDER);

        try {
            // Account summary
            List<Account> accounts = accountDAO.findAll();
            long distributors = accounts.stream().filter(a -> a.getType() == AccountType.DISTRIBUTOR).count();
            long retailers    = accounts.stream().filter(a -> a.getType() == AccountType.RETAILER).count();
            long suppliers    = accounts.stream().filter(a -> a.getType() == AccountType.SUPPLIER).count();

            System.out.println("\n  Account Summary");
            System.out.println("  " + THIN);
            System.out.printf("  Total Accounts  : %d%n",   accounts.size());
            System.out.printf("  Distributors    : %d%n",   distributors);
            System.out.printf("  Retailers       : %d%n",   retailers);
            System.out.printf("  Suppliers       : %d%n",   suppliers);

            // Order pipeline
            List<Order> orders = orderDAO.findAll();
            System.out.println("\n  Order Pipeline");
            System.out.println("  " + THIN);
            for (OrderStatus status : OrderStatus.values()) {
                long count = orders.stream().filter(o -> o.getStatus() == status).count();
                BigDecimal value = orders.stream()
                        .filter(o -> o.getStatus() == status)
                        .map(Order::getTotalValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                System.out.printf("  %-12s : %3d orders   $%.2f%n",
                        status.label(), count, value);
            }

            // Revenue from delivered orders
            BigDecimal delivered = orders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                    .map(Order::getTotalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            System.out.println("\n  " + THIN);
            System.out.printf("  Total Delivered Revenue : $%.2f%n", delivered);
            System.out.println(DIVIDER);

        } catch (SQLException e) {
            printError("Could not generate report", e);
        }
    }

    // ── Input helpers ─────────────────────────────────────────────────────────

    private int promptInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
                System.out.printf("  Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a whole number.");
            }
        }
    }

    private String promptString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private BigDecimal promptDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                BigDecimal value = new BigDecimal(scanner.nextLine().trim());
                if (value.compareTo(BigDecimal.ZERO) > 0) return value;
                System.out.println("  Value must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a number (e.g. 19.99).");
            }
        }
    }

    private String promptOrKeep(String prompt, String current) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? current : input;
    }

    private AccountType promptAccountType() {
        System.out.println("  Type: 1. Distributor  2. Retailer  3. Supplier");
        int choice = promptInt("  Select: ", 1, 3);
        return AccountType.values()[choice - 1];
    }

    private AccountType promptAccountTypeOrKeep(AccountType current) {
        System.out.printf("  Type [%s] (1. Distributor / 2. Retailer / 3. Supplier, Enter to keep): ",
                current.label());
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return current;
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= 3) return AccountType.values()[choice - 1];
        } catch (NumberFormatException ignored) { /* fall through to return current */ }
        return current;
    }

    // ── Display helpers ───────────────────────────────────────────────────────

    private String formatLocation(String city, String state) {
        if (city == null && state == null) return "—";
        if (city == null) return state;
        if (state == null) return city;
        return city + ", " + state;
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private void printError(String context, SQLException e) {
        System.err.println("\n  ✗ Error: " + context + " — " + e.getMessage());
    }

    private void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║              BevCRM — Beverage Distribution CRM          ║");
        System.out.println("  ║                      by Gavin Rockwell                   ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");
        System.out.println("  Connected to database. Type a number to navigate.\n");
    }
}
