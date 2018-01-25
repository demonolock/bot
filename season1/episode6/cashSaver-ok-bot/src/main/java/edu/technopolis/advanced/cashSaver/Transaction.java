package edu.technopolis.advanced.cashSaver;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Transaction implements Serializable{

    private String goal;
    private SimpleDateFormat date;
    private Integer amount;

    Transaction() {
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    String getGoal(){
        return goal;
    }

    public SimpleDateFormat getDate() {
        return date;
    }

    public void setDate(SimpleDateFormat date) {
        this.date = date;
    }

    Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }


    void setAll(String goal, Integer amount, SimpleDateFormat date) {
        this.goal = goal;
        this.date = date;
        this.amount = amount;
    }


    @Override
    public String toString(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy G 'at' HH:mm:ss z");
        return "дата: "+dateFormat+"  цель: "+this.goal+"  сумма: "+this.amount;
    }
}
