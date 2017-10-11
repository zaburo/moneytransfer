package com.francoismoureau.moneytransfer.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Transfer {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final int id;

    private Account source;

    private Account destination;

    private BigDecimal amount;

    private Currency currency;

    private Date date;

    private String comment;

    private TransferStatus status;

    public Transfer(Account source, Account destination, BigDecimal amount, Currency currency, Date date, String comment) {
        this.id = COUNTER.getAndIncrement();
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.comment = comment;
        this.status = TransferStatus.CREATED;
    }

    public Transfer() {
        this.id = COUNTER.getAndIncrement();
        this.status = TransferStatus.CREATED;
    }

    public static AtomicInteger getCOUNTER() {
        return COUNTER;
    }

    public int getId() {
        return id;
    }

    public Account getSource() {
        return source;
    }

    public void setSource(Account source) {
        this.source = source;
    }

    public Account getDestination() {
        return destination;
    }

    public void setDestination(Account destination) {
        this.destination = destination;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        return id == transfer.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", source=" + source +
                ", destination=" + destination +
                ", amount=" + amount +
                ", currency=" + currency +
                ", date=" + date +
                ", comment='" + comment + '\'' +
                ", status=" + status +
                '}';
    }

    public boolean execute() {
        if (status != TransferStatus.EXECUTED && amount.compareTo(BigDecimal.ZERO) > 0 && source.getBalance().compareTo(amount) > 0 && source.getCurrency().equals(destination.getCurrency()) && source.getCurrency().equals(currency) && destination.getCurrency().equals(currency)) {
            source.withdraw(amount);
            destination.deposit(amount);
            status = TransferStatus.EXECUTED;
            return true;
        } else {
            status = TransferStatus.FAILED;
            return false;
        }
    }

    public enum TransferStatus {
        CREATED,
        EXECUTED,
        FAILED
    }
}
