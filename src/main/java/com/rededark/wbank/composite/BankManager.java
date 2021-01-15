package com.rededark.wbank.composite;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class BankManager {

    public static final AtomicBoolean LOCKDOWN = new AtomicBoolean(false);

    private static final BankManager BANK_MANAGER = new BankManager();

    public static BankManager getInstance() {
        return BANK_MANAGER;
    }

    private BankManager() {};

    private final Table<Class<?>, Object, Object> table = HashBasedTable.create();

    public <T> void put(Class<T> clazz, Object unique, Object reference) {
        table.put(clazz, unique, reference);
    }

    public <T> T remove(Class<T> clazz, Object unique) {
        return (T) table.remove(clazz, unique);
    }

    public <T> T searchBy(Class<T> clazz, Object unique) {
        return (T) table.get(clazz, unique);
    }

    public <T> Collection<T> fill(Class<T> clazz) {
        return (Collection<T>) table.row(clazz).values();
    }

    public <T> int getRows(Class<T> clazz) {
        return table.row(clazz).size() + 1;
    }
}
