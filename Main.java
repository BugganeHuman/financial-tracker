package org.example;



import java.sql.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class Main {
    public static void main (String[] args) {




    Tracker tracker= new Tracker();
    tracker.createTable();
    //tracker.add(2001, 5, 10, 450, 1050);
    //tracker.reportForAPeriod(0, 0, 0, 10000, 100000, 100000);
    //tracker.createBalance (3000);
    tracker.sortingTable("profitable","month");
    //tracker.findRow(2000, 5, 4);


}
}
