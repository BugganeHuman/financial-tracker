package org.example;



import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Scanner;


class Main {
    public static void main (String[] args) {
        Scanner input = new Scanner(System.in);
        Tracker tracker = new Tracker();
        tracker.createTable();
        Path pathToBalance = Path.of("balance.txt");
        try {
            if (!Files.exists(pathToBalance)) {
                System.out.print("Enter the amount you already have on your balance, if your balance is empty, enter 0\n" +
                                "                                           ");
                String amountBalance = input.next();
                input.nextLine();
                tracker.createBalance(Integer.parseInt(amountBalance));
                System.out.println("Balance created\n");
            }
        }catch (Throwable e) {
            System.out.println("error, "+e.getMessage());
        }
        System.out.println("you can write -readme or -help");
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






            else if (actRecordsChoice.equals("2")) { //  РАБОТАЕТ НЕ КОРЕКТНО, НАДО ПОЧЕНИТЬ
                while (true) {
                System.out.println("\nPress |0| - to return");
                System.out.print("Enter the start date for report, separated by a space\n" +
                                "                       ");
                String startDateReport = input.nextLine();
                if (startDateReport.equals("0")) {
                    break;
                }
                System.out.print("\nEnter the finish date for report, separated by a space\n" +
                                "                       ");
                String finishDateReport = input.nextLine();
                String [] startDateArray = startDateReport.split(" ");
                String [] finishDateArray = finishDateReport.split(" ");
                int startYear = Integer.parseInt(startDateArray[0]);
                int startMonth = Integer.parseInt(startDateArray[1]);
                int startDay = Integer.parseInt(startDateArray[2]);
                int finishYear = Integer.parseInt(finishDateArray[0]);
                int finishMonth = Integer.parseInt(finishDateArray[1]);
                int finishDay = Integer.parseInt(finishDateArray[2]);
                try {
                    tracker.reportForAPeriod(startYear, startMonth, startDay, finishYear, finishMonth, finishDay);
                    break;
                } catch (Throwable e) {
                    System.out.println("\nError, " + e.getMessage());
                    break;
                }

                }





            }
            else if (actRecordsChoice.equals("3")) {
                while (true) {
                    System.out.println("Press |0| - to return");
                    System.out.print("Press |1| - sorting to years, |2| - sorting to months, |3| - sorting to days\n" +
                                    "                                      ");
                    String periodSort = input.next();
                    input.nextLine();
                    String period = "";
                    if (periodSort.equals("0")) {
                        break;
                    } else if (periodSort.equals("1")) {
                        period = "year";
                    }else if (periodSort.equals("2")) {
                        period = "month";
                    }else if (periodSort.equals("3")) {
                        period = "day";
                    }
                    System.out.print("Press |1| - to sorting on profitable, |2| - sorting on unprofitable\n" +
                                    "                                       ");
                    String sortingOnChoice = input.next();
                    input.nextLine();
                    String sortingOn = "";
                    if (sortingOnChoice.equals("1")) {
                        sortingOn = "profitable";
                    }else if (sortingOnChoice.equals("2")) {
                        sortingOn = "unprofitable";
                    }
                    try {
                    tracker.sortingTable(sortingOn, period);
                        break;
                    }catch (Throwable e) {
                        System.out.println("error, "+e.getMessage());
                        break;
                    }


                }


            }
            else if (actRecordsChoice.equals("4")) {
                while (true) {
                    System.out.println("Press |0| - to return");
                    System.out.print("Enter the date which record you want to find, separated by a space\n" +
                                    "                           ");
                    String findDate = input.nextLine();
                    if (findDate.equals("0")) {break;}
                    String[] findDateArray = findDate.split(" ");
                    int year = Integer.parseInt(findDateArray[0]);
                    int month = Integer.parseInt(findDateArray[1]);
                    int day = Integer.parseInt(findDateArray[2]);

                    try {
                        tracker.findRow(year, month, day);
                        break;
                    } catch (Throwable e) {
                        System.out.println("Error, "+e.getMessage() +"\n");
                        break;
                    }







                }



            }

            else if (actRecordsChoice.equals("5")) {
                while (true) {
                    System.out.println("Press |0| - to return");
                    System.out.print("Enter the date which record you want to delete, separated by a space\n" +
                            "                           ");
                    String deleteDate = input.nextLine();
                    if (deleteDate.equals("0")) {break;}
                    String[] deleteDateArray = deleteDate.split(" ");
                    int year = Integer.parseInt(deleteDateArray[0]);
                    int month = Integer.parseInt(deleteDateArray[1]);
                    int day = Integer.parseInt(deleteDateArray[2]);

                    try {
                    tracker.deleteRow(year, month, day);
                    System.out.println("the record deleted");
                        break;
                    }catch (Throwable e) {
                        System.out.println("error, "+e.getMessage());
                        break;
                    }


                }
            }

            }
        }

        else if (mainMenuChoice.equals("3")) {
            while (true) {
            System.out.print("\npress:" +
                    "\n0 - to return\n\n" +
                    "1 - show balance\n\n" +
                    "2 - rewrite balance sum of profits\n\n" +
                    "3 - add in balance\n" +
                    "                       ");
            String choiceInBalanceOptions = input.next();
            input.nextLine();
            if (choiceInBalanceOptions.equals("0")) {break;}
            else if (choiceInBalanceOptions.equals("1")) {tracker.showBalance();}
            else if (choiceInBalanceOptions.equals("2")) {tracker.rewriteBalanceSumOfProfits();System.out.println("\nDone\n");}
            else if (choiceInBalanceOptions.equals("3")) {
                while (true) {
                    System.out.println("Press |0| - to return");
                    System.out.print("write the amount you need to add\n" +
                                    "               ");
                    String amountAdd = input.next();
                    input.nextLine();
                    if (amountAdd.equals("0")) {break;}
                    try {
                        tracker.addInBalance(0,Integer.parseInt(amountAdd));
                        System.out.println ("\nDone\n");
                        break;
                    }catch (Throwable e) {
                        System.out.println("error, "+e.getMessage());
                    }

                }
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



else if (mainMenuChoice.equals("-readme")) {
    System.out.println("\nHi dude, it is simply financial-tracker, \n" +
                        "which uses SQLite, for keeping your records\n" +
                        "I did it for me, if you don't like something \n" +
                        "you can rewrite by yourself, because it is opensource\n" +
                        "link to repository https://github.com/BugganeHuman/financial-tracker\n" +
                            "                   enjoy using it :)");

        }



else if (mainMenuChoice.equals("-help")) {
            System.out.println("\nErrors which can be come and how fix their:\n" +
                                "1-In the backup method, you need to enter the path without quotes and in the uppercase\n" +
                                "2-Dates must be written without leading zeros, for example: 2000 1 9\n" +
                                "3-The numbers in | | mean that if you enter these numbers, what is written will happen\n" +
                                "4-If your balance is somehow broken, you can use the |rewrite balance sum of profits method|,\n" +
                                "  This will overwrite the balance with the sum of all profits in the database, after that,\n" +
                                "  you can use the |add in balance| method to add funds that were already on the balance before using the tracker" );

        }


        }




}
}
