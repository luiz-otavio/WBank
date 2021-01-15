package com.rededark.wbank.event.operation;

import com.rededark.wbank.account.Account;
import com.rededark.wbank.account.operation.Operation;
import com.rededark.wbank.event.DarkEvent;

public class OperationCompleteEvent extends DarkEvent {

    private final Operation operation;
    private final Account user, receiver;

    public OperationCompleteEvent(Operation operation, Account account, Account user) {
        this.operation = operation;
        this.receiver = account;
        this.user = user;
    }

    public Account getReceiver() {
        return receiver;
    }

    public Operation getOperation() {
        return operation;
    }

    public Account getUser() {
        return user;
    }
}
