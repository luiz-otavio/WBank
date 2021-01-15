package com.rededark.wbank.account;

import com.rededark.wbank.booster.Booster;
import com.rededark.wbank.composite.BankManager;
import com.rededark.wbank.account.operation.Operation;
import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Account {

    private static final BankManager BANK_MANAGER = BankManager.getInstance();
    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(3);

    private final UUID uuid;

    private float amount;
    private int current, limit;

    public Account(UUID uuid, float amount, int current, int limit) {
        this.uuid = uuid;
        this.amount = amount;
        this.current = current;
        this.limit = limit;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getLimit() {
        return limit;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getAmount() {
        return amount;
    }

    public Operation[] getOperations() {
        return CompletableFuture.supplyAsync(() -> {
            Operation[] operations = new Operation[0];

            for (Operation operation : BANK_MANAGER.fill(Operation.class)) {
                if(operation.isAncestor(uuid)) operations = ArrayUtils.add(operations, operation);
            }; return operations;
        }, SERVICE).join();
    }

    public Booster[] getBoosters() {
        return CompletableFuture.supplyAsync(() -> {
            Booster[] boosters = new Booster[0];

            final Player current = getPlayer();

            for (Booster booster : BANK_MANAGER.fill(Booster.class)) {
                if(booster.hasPermission(current)) boosters = ArrayUtils.add(boosters, booster);
            }; return boosters;
        }, SERVICE).join();
    }

    public boolean isFull() {
        return current >= limit;
    }

}
