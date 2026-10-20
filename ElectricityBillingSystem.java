package com.BusPassManagement;


import java.util.*;
import java.io.*;

public class ElectricityBillingSystem {

    static Scanner sc = new Scanner(System.in);
    static HashMap<String, Consumer> records = new HashMap<>();

    static class Consumer implements Serializable {
        String id, name, contact, category, status;
        double units;

        Consumer(String id, String name, String contact,
                 String category, double units) {
            this.id = id;
            this.name = name;
            this.contact = contact;
            this.category = category;
            this.units = units;
            this.status = "UNPAID";
        }

        double calculateBill() {
            double bill = 0;

            if (units <= 100) {
                bill = units * 1.5;
            } else if (units <= 200) {
                bill = 100 * 1.5 + (units - 100) * 2.5;
            } else if (units <= 500) {
                bill = 100 * 1.5 + 100 * 2.5
                        + (units - 200) * 4;
            } else {
                bill = 100 * 1.5 + 100 * 2.5
                        + 300 * 4 + (units - 500) * 6;
            }

            return bill;
        }

        void display() {
            System.out.println("\nConsumer ID : " + id);
            System.out.println("Name        : " + name);
            System.out.println("Contact     : " + contact);
            System.out.println("Category    : " + category);
            System.out.println("Units       : " + units);
            System.out.printf("Bill Amount : Rs. %.2f%n",
                    calculateBill());
            System.out.println("Status      : " + status);
        }
    }

    static void saveRecords() {
        try (ObjectOutputStream out =
                new ObjectOutputStream(
                    new FileOutputStream("consumers.dat"))) {
            out.writeObject(records);
        } catch (IOException e) {
            System.out.println("Error saving records.");
        }
    }

    @SuppressWarnings("unchecked")
    static void loadRecords() {
        File file = new File("consumers.dat");

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream in =
                new ObjectInputStream(
                    new FileInputStream(file))) {
            records = (HashMap<String, Consumer>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading records.");
        }
    }

    static void addConsumer() {
        System.out.print("Enter Consumer ID: ");
        String id = sc.nextLine();

        if (records.containsKey(id)) {
            System.out.println("Consumer ID already exists.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Contact Number: ");
        String contact = sc.nextLine();

        if (!contact.matches("[0-9]{10}")) {
            System.out.println("Enter a valid 10-digit contact.");
            return;
        }

        System.out.print("Enter Category (DOMESTIC/COMMERCIAL): ");
        String category = sc.nextLine().toUpperCase();

        if (!category.equals("DOMESTIC")
                && !category.equals("COMMERCIAL")) {
            System.out.println("Invalid category.");
            return;
        }

        System.out.print("Enter Units Consumed: ");

        try {
            double units = Double.parseDouble(sc.nextLine());

            if (!Double.isFinite(units) || units < 0) {
                System.out.println("Units must be non-negative.");
                return;
            }

            Consumer c = new Consumer(
                    id, name, contact, category, units);

            records.put(id, c);
            saveRecords();

            System.out.println("Consumer added successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid units.");
        }
    }

    static void viewConsumers() {
        if (records.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Consumer c : records.values()) {
            c.display();
        }
    }

    static void searchConsumer() {
        System.out.print("Enter Consumer ID: ");
        String id = sc.nextLine();

        Consumer c = records.get(id);

        if (c != null) {
            c.display();
        } else {
            System.out.println("Consumer not found.");
        }
    }

    static void updateConsumer() {
        System.out.print("Enter Consumer ID: ");
        String id = sc.nextLine();

        Consumer c = records.get(id);

        if (c == null) {
            System.out.println("Consumer not found.");
            return;
        }

        System.out.print("Enter New Name: ");
        String name = sc.nextLine();

        System.out.print("Enter New Contact: ");
        String contact = sc.nextLine();

        if (!contact.matches("[0-9]{10}")) {
            System.out.println("Invalid contact number.");
            return;
        }

        System.out.print("Enter New Units: ");

        try {
            double units = Double.parseDouble(sc.nextLine());

            if (!Double.isFinite(units) || units < 0) {
                System.out.println("Invalid units.");
                return;
            }

            c.name = name;
            c.contact = contact;
            c.units = units;
            c.status = "UNPAID";

            saveRecords();
            System.out.println("Record updated successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid units.");
        }
    }

    static void deleteConsumer() {
        System.out.print("Enter Consumer ID: ");
        String id = sc.nextLine();

        if (records.remove(id) != null) {
            saveRecords();
            System.out.println("Record deleted successfully.");
        } else {
            System.out.println("Consumer not found.");
        }
    }

    static void payBill() {
        System.out.print("Enter Consumer ID: ");
        String id = sc.nextLine();

        Consumer c = records.get(id);

        if (c == null) {
            System.out.println("Consumer not found.");
            return;
        }

        c.display();

        if (c.status.equals("PAID")) {
            System.out.println("Bill already paid.");
            return;
        }

        System.out.print("Confirm payment (Y/N): ");

        if (sc.nextLine().equalsIgnoreCase("Y")) {
            c.status = "PAID";
            saveRecords();
            System.out.println("Payment successful.");
        } else {
            System.out.println("Payment cancelled.");
        }
    }

    static void report() {
        double totalUnits = 0;
        double totalBill = 0;
        double unpaid = 0;

        for (Consumer c : records.values()) {
            totalUnits += c.units;
            totalBill += c.calculateBill();

            if (c.status.equals("UNPAID")) {
                unpaid += c.calculateBill();
            }
        }

        System.out.println("\n===== BILLING REPORT =====");
        System.out.println("Total Consumers: " + records.size());
        System.out.printf("Total Units: %.2f%n", totalUnits);
        System.out.printf("Total Bill: Rs. %.2f%n", totalBill);
        System.out.printf("Unpaid Amount: Rs. %.2f%n", unpaid);
    }

    public static void main(String[] args) {
        loadRecords();

        while (true) {
            System.out.println("\n===== ELECTRICITY BILLING SYSTEM =====");
            System.out.println("1. Add Consumer");
            System.out.println("2. View All Consumers");
            System.out.println("3. Search Consumer");
            System.out.println("4. Update Consumer");
            System.out.println("5. Delete Consumer");
            System.out.println("6. Pay Bill");
            System.out.println("7. Generate Report");
            System.out.println("8. Exit");
            System.out.print("Enter Choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        addConsumer();
                        break;
                    case 2:
                        viewConsumers();
                        break;
                    case 3:
                        searchConsumer();
                        break;
                    case 4:
                        updateConsumer();
                        break;
                    case 5:
                        deleteConsumer();
                        break;
                    case 6:
                        payBill();
                        break;
                    case 7:
                        report();
                        break;
                    case 8:
                        saveRecords();
                        System.out.println("Thank you!");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid menu number.");
            }
        }
    }
}

