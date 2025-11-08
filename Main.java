package org.example;



import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;


class Main {
    public static void main (String[] args) {
        Tracker tracker = new Tracker();
        tracker.createTable();
        Scanner input = new Scanner(System.in);
        System.out.println("write something (-readme, -help)");
        while (true) {
        System.out.print("\npress :\n" +
                "0 - exit\n\n" +
                "1 - add\n\n" +
                "2 - actions with records\n\n" +
                "3 - actions with balance\n\n" +
                "4 - create backup\n" +
                "                     ");
        String mainMenuChoice = input.next();
            input.nextLine();



        if (mainMenuChoice.equals("0")) {
            break;
        }



        else if (mainMenuChoice.equals("1")) {
            while (true) {

            System.out.println("press |0| - to return");
            System.out.print("Enter year, month, day separated by a space\n" +
                            "                      ");

            String dateString = input.nextLine();

            if (dateString.equals("0")) {
                break;
            }

            String[] dateArray = dateString.split(" ");

            int year = Integer.parseInt(dateArray[0]);

            int month = Integer.parseInt(dateArray[1]);
            int day = Integer.parseInt(dateArray[2]);
            System.out.print("Write expenses: ");
            String expenses = input.next();
            System.out.print("Write income: ");
            String income = input.next();
            try {
                tracker.add(year, month, day, Integer.parseInt(expenses), Integer.parseInt(income));
                System.out.println("\nrecord added");
            } catch (Throwable e) {
                System.out.println("error, " + e.getMessage());
            }
            break;
            }

        }



        else if (mainMenuChoice.equals("2")) {
            while (true) {
            System.out.print("\npress: \n" +
                    "0 - to return\n\n" +
                    "1 - show all records\n\n" +
                    "2 - report for a period\n\n" +
                    "3 - sort records\n\n" +
                    "4 - find record\n\n" +
                    "5 - delete record\n" +
                    "                   ");
            String actRecordsChoice = input.next();
            input.nextLine();
            if (actRecordsChoice.equals("0")) {
                break;
            }

            else if (actRecordsChoice.equals("1")) {
                tracker.showTable();
            }
            else if (actRecordsChoice.equals("2")) {

                // ПРОДОЛЖАЮ ПИСАТЬ ЗДЕСЬ
            }

            }
        }





        else if (mainMenuChoice.equals("4")) {
            while (true) {
                System.out.println("press |0| - to return\n");
                System.out.print("Write path where you want to make the backup DataBase\n" +
                                "               ");
                String path = input.nextLine();
                if (path.equals("0")) {
                    break;
                }
                try {
                    tracker.createBackup(path);
                    System.out.print("\nBackup added\n");
                    break;

                }catch (Throwable e) {
                    System.out.print("\nerror, "+e.getMessage() + "\n");
                    break;
                }


            }

        }



        }




}
}
