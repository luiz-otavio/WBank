package com.rededark.wbank.event.account;

import com.rededark.wbank.account.Account;
import com.rededark.wbank.account.operation.type.OperationType;
import com.rededark.wbank.event.DarkEvent;

public class AccountUpdateLimitEvent extends DarkEvent {

    private final Account account;
    private final OperationType type;
    private int limit;

    public AccountUpdateLimitEvent(Account account, OperationType operationType, int limit) {
        this.account = account;
        this.type = operationType;
        this.limit = limit;
    }

    public Account getAccount() {
        return account;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public OperationType getType() {
        return type;
    }
}
