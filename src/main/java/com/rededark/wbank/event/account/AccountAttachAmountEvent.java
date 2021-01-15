package com.rededark.wbank.event.account;

import com.rededark.wbank.account.Account;
import com.rededark.wbank.account.operation.Operation;
import com.rededark.wbank.event.DarkEvent;

public class AccountAttachAmountEvent extends DarkEvent {

    private final Account account;
    private final Operation operation;

    private float amount;

    public AccountAttachAmountEvent(Account account, Operation operation, float amount) {
        this.account = account;
        this.operation = operation;
        this.amount = amount;
    }

    public Operation getOperation() {
        return operation;
    }

    public float getAmount() {
        return amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

}
