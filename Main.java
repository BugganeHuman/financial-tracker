package org.example;



import java.sql.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class Main {
public static void getConnect () {
    var url = "jdbc:sqlite:testDatabase.db";
    try (var connection = DriverManager.getConnection(url)) {
        if (connection != null) {
            var meta = connection.getMetaData();
            System.out.println("connect " + meta.getDriverName());
        }
    } catch (SQLException e) {
        System.err.println (e.getErrorCode());
    }
}



public static void creatTable() {
    var url = "jdbc:sqlite:testDatabase.db";
    var sql = "CREATE TABLE IF NOT EXISTS animals (" +
            "id INTEGER PRIMARY KEY, " +
            "typeOfAnimal TEXT NOT NULL," +
            "name TEXT " +
            ");";

    try(var conn = DriverManager.getConnection(url);
        var statement = conn.createStatement();
            ) {
        statement.execute(sql);
        if (conn != null) {
            var meta = conn.getMetaData();
            System.out.println ("Database is created, meta: " + meta.getDriverName());
        }
    } catch (SQLException e) {
                System.err.println ("error" +e.getErrorCode()+ e.getMessage());
    }

}


public static void insertInDB (String type, String name) {
    var url = "jdbc:sqlite:testDatabase.db";
    //String type = first;
    //String name = second;
    var sql = "INSERT INTO animals (typeOfAnimal, name) VALUES (?,?)";
    try (var conn = DriverManager.getConnection(url); var prSt = conn.prepareStatement(sql);) {
        prSt.setString(1, type);
        prSt.setString(2, name);
        prSt.executeUpdate();
    } catch (SQLException e) {
        System.out.println("error" + e.getMessage());
    }
}

public static void read() {
    String url = "jdbc:sqlite:testDatabase.db";
    String sql = "SELECT * FROM animals";
    try(var conn = DriverManager.getConnection(url); var prSt = conn.prepareStatement(sql); var rs = prSt.executeQuery();) {
        while (rs.next()) {
            System.out.printf ("%d %s %s%n",
                    rs.getInt("id"),
                    rs.getString("typeOfAnimal"),
                    rs.getString("name"));
        }


    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }

}



public static void update() {
    String url = "jdbc:sqlite:testDatabase.db";
    String sql = "UPDATE animals SET name = 'bitch' WHERE typeOfAnimal = 'squirrel'";
    try(var conn = DriverManager.getConnection(url); var prSt = conn.prepareStatement(sql)) {
        prSt.executeUpdate();


    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }

}


public static void delete () {
String url = "jdbc:sqlite:testDatabase.db";
String sql = "DELETE FROM animals WHERE typeOfAnimal = 'human'";
try(var conn = DriverManager.getConnection(url);var prSt = conn.prepareStatement(sql)) {
    prSt.executeUpdate();
} catch (SQLException e) {
    System.out.println(e.getMessage());
}
}

public static void main (String[] args) {
    delete();

    //update();
    //insertInDB("human", "Dick");
    Tracker tracker= new Tracker();
    tracker.createTable();
    //tracker.add(2000, 4, 5, 230, 1450);
    //tracker.reportForAPeriod(0, 0, 0, 10000, 100000, 100000);
    //tracker.createBalance (3000);
    tracker.sortingTable("profitable");

}
}
