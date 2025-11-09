package org.example;



import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

class Tracker {



    public void createTable () {
        String url = "jdbc:sqlite:financialDatabase.db";
        String sql = "CREATE TABLE IF NOT EXISTS finance (" +
                "year INTEGER NOT NULL," +
                "month INTEGER NOT NULL," +
                "day INTEGER NOT NULL," +
                "expenses INTEGER," +
                "income INTEGER," +
                "profit INTEGER" +
                ");";


        try (var connection = DriverManager.getConnection(url); var statement = connection.createStatement();) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("error in tracker1 " + e.getMessage());
        }
    }




    public void add (int year, int month, int day, int expenses, int income) {
        String url = "jdbc:sqlite:financialDatabase.db";
        try {
            String sql = "SELECT * FROM finance WHERE year = ? AND month = ? AND day = ?";
            try (var conn = DriverManager.getConnection(url); var prStmt = conn.prepareStatement(sql);) {
                prStmt.setInt(1, year);
                prStmt.setInt(2, month);
                prStmt.setInt(3, day);
                var rs = prStmt.executeQuery();
                boolean haveDuplicate = rs.next();

                if (haveDuplicate) {
                    reduceInBalance(rs.getInt("expenses"), rs.getInt("income"));
                    rs.close();
                    String urlUpdate = "jdbc:sqlite:finance";
                    String sqlUpdate = "UPDATE finance SET expenses = ?, income = ?, profit = ? WHERE year = ? AND month = ? AND day = ? ";
                    try (var prSt = conn.prepareStatement(sqlUpdate);) {
                        addInBalance(expenses, income);
                        prSt.setInt(1, expenses);
                        prSt.setInt(2, income);
                        prSt.setInt(3, income - expenses);
                        prSt.setInt(4, year);
                        prSt.setInt(5, month);
                        prSt.setInt(6, day);
                        prSt.executeUpdate();
                        return;

                    } catch (SQLException e) {
                        System.out.println("error in UPDATE "+e.getMessage());
                    }
                }
                else {
                    rs.close();
                }
            }
        } catch (SQLException e) {
            System.out.println("error in tracker2 " + e.getMessage() + e.getErrorCode() );
        }
        String sql = "INSERT INTO finance (year, month, day, expenses, income, profit) VALUES (?,?,?,?,?,?)";
        addInBalance(expenses,income);
        try(var connection = DriverManager.getConnection(url); var prStmt = connection.prepareStatement(sql)) {
            prStmt.setInt(1, year);
            prStmt.setInt(2, month);
            prStmt.setInt(3, day);
            prStmt.setInt(4, expenses);
            prStmt.setInt(5, income);
            prStmt.setInt(6, income - expenses);
            prStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error in tracker3 " + e.getMessage());
        }
    }




    // надо что бы перебирал все года но допустим - start 2000 5 23 end 2003 8 11, и он должен начать перебирать так -
    // в первом году он перебирает начаная с указаного месяца и дня, и в последнем он пиребирает до указаного месяца и дня
    // а все годо которые между первым и последним он перебирает полнастью.
    public void reportForAPeriod (int startYear, int startMonth, int startDay,
                                         int finishYear, int finishMonth, int finishDay) { // РАБОТАЕТ НЕ КОРЕКТНО
        String url = "jdbc:sqlite:financialDatabase.db";

        String sql = "SELECT * FROM finance WHERE year >= ? AND year <= ?" +
                " AND month >= ? AND month <= ?" +
                " AND day >= ? AND day <= ?";

        try(var conn = DriverManager.getConnection(url); var prSt = conn.prepareStatement(sql);) {
            prSt.setInt(1, startYear);
            prSt.setInt(2, finishYear);
            prSt.setInt(3, startMonth);
            prSt.setInt(4, finishMonth);
            prSt.setInt(5, startDay);
            prSt.setInt(6, finishDay);
            var rs = prSt.executeQuery();
            int allExpenses = 0;
            int allIncome = 0;

            while (rs.next()) {
                allExpenses += rs.getInt("expenses");
                allIncome += rs.getInt ("income");
            }
            System.out.printf("Expenses - %d%nIncome - %d%n", allExpenses, allIncome);

            rs.close();
        } catch (SQLException e) {
            System.out.println("error in reportForAPeriod " + e.getMessage());
        }

    }





    public void createBalance (int moneyNow) {
        Path pathToFileWithBalance = Path.of("balance.txt");
        String moneyNowString = String.valueOf(moneyNow);
        try {
            if (!Files.exists(pathToFileWithBalance)) {
                Files.createFile(pathToFileWithBalance);
            }
            Files.writeString(pathToFileWithBalance,moneyNowString );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }





    public void addInBalance (int expenses, int income) {
        Path pathToFileWithBalance = Path.of("balance.txt");
        try {
            String balanceString = Files.readString(pathToFileWithBalance);
            int balance = Integer.parseInt(balanceString);
            balance = balance + (income - expenses);
            Files.write(pathToFileWithBalance, new byte[0]);
            Files.writeString(pathToFileWithBalance, String.valueOf(balance));


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }





    public void reduceInBalance (int expenses, int income) {
        Path pathToFileWithBalance = Path.of("balance.txt");
        try {
            String balanceString = Files.readString(pathToFileWithBalance);
            int balance = Integer.parseInt(balanceString);
            balance = balance - (income - expenses);
            Files.write(pathToFileWithBalance, new byte[0]);
            Files.writeString(pathToFileWithBalance, String.valueOf(balance));


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }





    public void showBalance () {
        Path pathToFileWithBalance = Path.of("balance.txt");

        try {
            String balance = Files.readString(pathToFileWithBalance);
            System.out.println("\nBalance now - "+Integer.parseInt(balance)+"\n");

        } catch (IOException e) {
            System.out.println("error in showBalance(), "+e.getMessage());
        }
    }





    public void sortingTable (String sortingOn, String period) {
        String url = "jdbc:sqlite:financialDatabase.db";
        String sql = "";

        if (period.equals("day")) {
            if (sortingOn.equals("profitable")) {
                sql = "SELECT * FROM finance ORDER BY profit DESC";
            } else if (sortingOn.equals("unprofitable")) {
                sql = "SELECT * FROM finance ORDER BY profit ASC";
            }


            try (var conn = DriverManager.getConnection(url);
                 var prSt = conn.prepareStatement(sql);
                 var rs = prSt.executeQuery();) {

                while (rs.next()) {
                    System.out.printf("Year - %d  Month - %d  Date - %d  expenses - %d  income - %d  profit - %d%n%n",
                            rs.getInt("year"),
                            rs.getInt("month"),
                            rs.getInt("day"),
                            rs.getInt("expenses"),
                            rs.getInt("income"),
                            rs.getInt("profit")

                    );

                }
            } catch (SQLException e) {
                System.out.println("error in showBalance () " + e.getMessage());
            }
        } else if (period.equals("year")) {
            List<YearProfit> yearsAndProfitsArray = new ArrayList<>();

                sql = "SELECT year FROM finance ORDER BY year DESC";

            try (var conn = DriverManager.getConnection(url);
                 var prSt = conn.prepareStatement(sql);
                 var rs = prSt.executeQuery();) {

                Set<Integer> years = new HashSet<>();

                while (rs.next()) {
                    years.add(rs.getInt("year"));
                }



                for (int year : years) {

                    String sqlInForYears = "SELECT * FROM finance WHERE year = ?";
                    try (var prStInYears = conn.prepareStatement(sqlInForYears);) {
                        prStInYears.setInt(1, year);
                        var rsInYears = prStInYears.executeQuery();
                        int profitForYear = 0;
                        while (rsInYears.next()) {
                            profitForYear += rsInYears.getInt("profit");
                        }

                        yearsAndProfitsArray.add(new YearProfit(year, profitForYear));

                        rsInYears.close();
                    } catch (SQLException e) {
                        System.out.println("error in sortingTable (years) in for years, " + e.getMessage());
                    }
                }


            } catch (SQLException e) {
                System.out.println("error in sortingTable(year), " + e.getMessage());
            }

            Collections.sort(yearsAndProfitsArray, (q, w) -> Integer.compare(q.profit, w.profit));

            if (sortingOn.equals("unprofitable")) {
                for (YearProfit elem : yearsAndProfitsArray) {
                    System.out.println(elem+"\n");
                }


            } else if (sortingOn.equals("profitable")) {
                Collections.reverse(yearsAndProfitsArray);
                for (YearProfit elem : yearsAndProfitsArray) {
                    System.out.println(elem+"\n");

                }
            }
        }




        else if (period.equals("month")) {


            Set<MonthProfit> setOfMonth = new HashSet<>() ;


            sql = "SELECT year, month FROM finance";

            try(var conn = DriverManager.getConnection(url);

                var prSt = conn.prepareStatement(sql);
                var rs = prSt.executeQuery();) {

                while (rs.next()) {


                   var sqlSearchMonthProfit = "SELECT profit FROM finance WHERE year = ? AND month = ?";

                    try (var prStSearchMonth = conn.prepareStatement(sqlSearchMonthProfit)) {

                        prStSearchMonth.setInt(1,rs.getInt("year") );

                        prStSearchMonth.setInt(2,rs.getInt("month"));

                      var rsMonthProfit = prStSearchMonth.executeQuery();

                      int profitOfMonth = 0;
                      while(rsMonthProfit.next()) {

                          profitOfMonth += rsMonthProfit.getInt("profit");
                      }
                      setOfMonth.add(new MonthProfit(rs.getInt("year"), rs.getInt("month"), profitOfMonth));



                    } catch (SQLException e) {
                        System.out.println("error in sortingTable (month) in while rs.next for search profit of month, "+e.getMessage());
                    }
                }


            } catch ( SQLException e) {
                System.out.println("error in sortingTable (month), "+e.getMessage());
            }

            List <MonthProfit> monthProfitsArray = new ArrayList<>(setOfMonth);

            Collections.sort(monthProfitsArray, (q, w) -> Integer.compare(q.profit, w.profit));
            if (sortingOn.equals("profitable")) {
                Collections.reverse(monthProfitsArray);
                for (MonthProfit month: monthProfitsArray) {
                    System.out.println(month+"\n");
                }

            }else if (sortingOn.equals("unprofitable")) {
            for (MonthProfit month: monthProfitsArray) {
                System.out.println(month+"\n");
            }

            }
        }
    }





    public void rewriteBalanceSumOfProfits () {
        String url = "jdbc:sqlite:financialDatabase.db";
        String sql = "SELECT * FROM finance";
        try (var conn = DriverManager.getConnection(url);
             var prSt = conn.prepareStatement(sql);
             var rs = prSt.executeQuery();) {
            int sumProfits = 0;
            while (rs.next()) {
                sumProfits += rs.getInt("profit");
            }
            createBalance(sumProfits);
        }catch (SQLException e) {
            System.out.println ("error in rewriteBalanceSumOfProfits (), " + e.getMessage());
        }
    }





public void showTable() {
    String url = "jdbc:sqlite:financialDatabase.db";
    String sql = "SELECT * FROM finance";
    try (var conn = DriverManager.getConnection(url);
         var prSt = conn.prepareStatement(sql);
         var rs = prSt.executeQuery();) {
        while (rs.next()) {
            System.out.printf("Year - %d  Month - %d  Date - %d  expenses - %d  income - %d  profit - %d%n%n",
                    rs.getInt("year"),
                    rs.getInt("month"),
                    rs.getInt("day"),
                    rs.getInt("expenses"),
                    rs.getInt("income"),
                    rs.getInt("profit"));
        }


    } catch (SQLException e) {
        System.out.println("error in showTable(), " + e.getMessage());
    }
}





public void deleteRow (int year, int month, int day) {
        String url = "jdbc:sqlite:financialDatabase.db";
        String sql = "DELETE FROM finance WHERE year = ? AND month = ? AND day = ?";
        try (var conn = DriverManager.getConnection(url);
             var prSt = conn.prepareStatement(sql);   ) {
            prSt.setInt(1,year);
            prSt.setInt(2,month);
            prSt.setInt(3,day);
            prSt.executeUpdate();
            rewriteBalanceSumOfProfits();
        } catch (SQLException e) {
            System.out.println("error in deleteRow(), "+e.getMessage());
        }
}





public void findRow(int year, int month, int day) {
    String url = "jdbc:sqlite:financialDatabase.db";
    String sql = "SELECT * FROM finance WHERE year = ? AND month = ? AND day = ?";
    try (var conn = DriverManager.getConnection(url);
         var prSt = conn.prepareStatement(sql); ) {
        prSt.setInt(1, year);
        prSt.setInt(2, month);
        prSt.setInt(3, day);
        var rs =  prSt.executeQuery();
        while (rs.next()) {
            System.out.printf("%nYear - %d  Month - %d  Date - %d  expenses - %d  income - %d  profit - %d%n",
                    rs.getInt("year"),
                    rs.getInt("month"),
                    rs.getInt("day"),
                    rs.getInt("expenses"),
                    rs.getInt("income"),
                    rs.getInt("profit"));
        }
        rs.close();
    } catch ( SQLException e) {
        System.out.println("error in findRow(), " + e.getMessage());
    }

}





public void createBackup (String path) {
Path pathToBackup = Path.of(path);
try {
Files.createDirectories(pathToBackup.getParent());
    if (!Files.exists(pathToBackup)) {
        Files.createFile(pathToBackup);
        try {
            Files.copy(Path.of("financialDatabase.db"), pathToBackup, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("error in copy backup, "+e.getMessage());
        }
    }

}catch (IOException e) {
    System.out.println("error in backup (), " + e.getMessage());
}
}
}
