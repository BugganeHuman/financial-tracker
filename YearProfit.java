package org.example;

public class YearProfit {
    int year;
    int profit;

    public YearProfit(int year, int profit) {
        this.year = year;
        this.profit = profit;
    }

    
    @Override
    public String toString() {
        return "year " + year + " - " + "profit " + profit;
    }
    
}

