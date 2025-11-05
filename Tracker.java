package org.example;



import java.nio.file.Path;
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
                    System.out.println("fine");
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
        } // надо сделать в начале поиск SELECT * FROM finance WHERE year = ?, month = ?, date = ?
    }







    public void reportForAPeriod (int startYear, int startMonth, int startDay,
                                         int finishYear, int finishMonth, int finishDay) {
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
            //allExpenses += rs.getInt("expenses");
            //allIncome += rs.getInt("income");
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
            System.out.println(Integer.parseInt(balance));

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
                    System.out.printf("Year - %d  Month - %d  Date - %d  expenses - %d  income - %d  profit - %d%n",
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

                //

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


                        // надо как то сделать что бы значение year-profitForYear добавлялись в массив,
                        // там сортировались по profitForYear и принтились
                        // мб делать так: все профиты годов складывать в массив и сортировать их,
                        // потом искать год в котором сумма профитов ровна профиту к которому ищем
                        // и принтить их вместе, и идти дальше (это должен быть цикл)

                        rsInYears.close();
                    } catch (SQLException e) {
                        System.out.println("error in sortingTable (years) in for years, " + e.getMessage());
                    }


                }

            } catch (SQLException e) {
                System.out.println("error in sortingTable(year), " + e.getMessage());
            }
            // здесь писать
            Collections.sort(yearsAndProfitsArray, (q, w) -> Integer.compare(q.profit, w.profit));
            if (sortingOn.equals("unprofitable")) {
                for (YearProfit elem : yearsAndProfitsArray) {
                    System.out.println(elem);
                }


            } else if (sortingOn.equals("profitable")) {
                Collections.reverse(yearsAndProfitsArray);
                for (YearProfit elem : yearsAndProfitsArray) {
                    System.out.println(elem);


                }

            }


        }

        else if (period.equals("month")) {
            // для начала надо что бы просто выводились месяцы (и их год) и их profit
            // мне надо что был массив с обьектами MonthProfit где строки year, month, profit
            // потом когда массив наполнен надо сортировать массив по profit (по возрст или убыв в зависимости от выбора)
            // и принтился отсартированый массив
            Set<String> setOfMonth = new HashSet<>() ;
            sql = "SELECT year, month FROM finance";
            try(var conn = DriverManager.getConnection(url);

                var prSt = conn.prepareStatement(sql);
                var rs = prSt.executeQuery();) {

                while (rs.next()) {
                    System.out.println(" year - "+rs.getInt("year")+" month - "+rs.getInt("month"));

                }


            } catch ( SQLException e) {
                System.out.println("error in sortingTable (month), "+e.getMessage());


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
            System.out.printf("Year - %d  Month - %d  Date - %d  expenses - %d  income - %d  profit - %d%n",
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
            System.out.printf("Year - %d  Month - %d  Date - %d  expenses - %d  income - %d  profit - %d%n",
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



}
// мне надо методы: 1) создания таблицы,2) добавления данных в таблицу с учетом дубликатов (потом как улучшение можно добавить
// удаление и прибавление средств с баланса)
// 3) просмотр отчета за переод времени
// (юзер вводит началоьную дату и конечную, и надо вывести сколько накопил и сколько потратил,
// потом как улучшение надо добавить сортировку по самым прибыльным/убыточным дням),
// 4) сортировка таблицы по самым прибыльным/убыточным  дням/месяцам/годам,
//5) метод редактирования/удаления/просмотра (юзер вводит дату и может с ней зделать что ему надо -
// удалить, отредактировать или просто посмотреть и закрыть)
// 6) метод что бы юзер в начале пользования программой, ввел свой текущий риальный баланс, и что бы от него отталкиватся
// 6.1) метод показыть баланс
// 7) функция бэкапа - файл .db переносится на указанный путь
