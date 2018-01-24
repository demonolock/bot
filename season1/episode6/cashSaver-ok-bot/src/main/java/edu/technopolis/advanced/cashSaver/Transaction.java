package edu.technopolis.advanced.cashSaver;

import java.io.Serializable;
import java.util.Calendar;

public class Transaction implements Serializable{

    private String goal;
    private Calendar date;
    private Integer amount;

    public Transaction() {
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getGoal(){
        return goal;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }


    public void setAll(String goal, Integer amount, Calendar date) {
        this.goal = goal;
        this.date = date;
        this.amount = amount;
    }


    @Override
    public String toString(){

        int year = this.date.get(this.date.YEAR);
        int month = this.date.get(this.date.MONTH)+1;
        int day = this.date.get(this.date.DAY_OF_MONTH);
        int hour = this.date.get(this.date.HOUR);
        int min = this.date.get(this.date.MINUTE);
        int sec = this.date.get(this.date.SECOND);

        return "дата: "+day+"."+month+"."+year+" - "+hour+":"+min+":"+sec+"  цель: "+this.goal+"  сумма: "+this.amount;
    }
}
