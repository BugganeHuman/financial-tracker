package org.example;



import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class Tracker {



    public void createTable () {
        String url = "jdbc:sqlite:financialDatabase.db";
        String sql = "CREATE TABLE IF NOT EXISTS finance (" +
                "year INTEGER NOT NULL," +
                "month INTEGER NOT NULL," +
                "day INTEGER NOT NULL," +
                "expenses INTEGER," +
                "income INTEGER" +
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
                    String sqlUpdate = "UPDATE finance SET expenses = ?, income = ? WHERE year = ? AND month = ? AND day = ? ";
                    try (var prSt = conn.prepareStatement(sqlUpdate);) {
                        addInBalance(expenses, income);
                        prSt.setInt(1, expenses);
                        prSt.setInt(2, income);
                        prSt.setInt(3, year);
                        prSt.setInt(4, month);
                        prSt.setInt(5, day);
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
        String sql = "INSERT INTO finance (year, month, day, expenses, income) VALUES (?,?,?,?,?)";
        addInBalance(expenses,income);
        try(var connection = DriverManager.getConnection(url); var prStmt = connection.prepareStatement(sql)) {
            prStmt.setInt(1, year);
            prStmt.setInt(2, month);
            prStmt.setInt(3, day);
            prStmt.setInt(4, expenses);
            prStmt.setInt(5, income);
            prStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error in tracker3 " + e.getMessage());
        } // надо сделать в начале поиск SELECT * FROM finance WHERE year = ?, month = ?, date = ?
    }







    public void reportForAPeriod (int startYear, int startMonth, int startDay, // здесь баг
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