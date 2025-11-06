package org.example;

public class MonthProfit {
    int year;
    int month;
    int profit;
    public MonthProfit (int year,int month,int profit) {
        this.year = year;
        this.month = month;
        this.profit = profit;
    }
    @Override
    public String toString() {
     return "year - " + year + "   month - " + month + "   profit - " + profit;
    }


    // ЭТОТ КАЛ КОТОРЫЙ НИЖЕ ПЕРЕПИСАТЬ И РАЗОБРАТСЯ ЧТО ОН ДЕЛАЕТ
    @Override
        public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MonthProfit)) return false;
        MonthProfit monthProfit = (MonthProfit) object;
        return month == monthProfit.month & year == monthProfit.year;



    }
    @Override
    public int hashCode() {
        return Integer.hashCode(month);
    }

}
