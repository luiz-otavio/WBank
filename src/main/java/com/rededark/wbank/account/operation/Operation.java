package com.rededark.wbank.account.operation;

import com.rededark.wbank.account.Account;
import com.rededark.wbank.account.operation.type.OperationType;
import com.rededark.wbank.composite.BankManager;
import com.rededark.wbank.event.operation.OperationCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.UUID;

public class Operation {

    private static final BankManager BANK_MANAGER = BankManager.getInstance();

    private final OfflinePlayer receiver, user;
    private final Instant instant;
    private final OperationType operationType;

    private final float amount;
    private final long id;
    private final boolean place;

    public static Builder of() {
        return new Builder();
    }

    private Operation(OfflinePlayer receiver, OfflinePlayer user, Instant instant, OperationType type, float amount, long id, boolean place) {
        this.receiver = receiver;
        this.user = user;
        this.instant = instant;
        this.operationType = type;
        this.amount = amount;
        this.place = place;
        this.id = id;
    }

    public static class Builder {

        private static final BankManager BANK_MANAGER = BankManager.getInstance();

        private UUID receiver, user;
        private OperationType operationType;

        private float amount = 0;
        private Instant instant = Instant.now();

        private long id = -1;
        private boolean place = false;

        public Builder receiver(UUID account) {
            this.receiver = account; return this;
        }

        public Builder user(UUID account) {
            this.user = account; return this;
        }

        public Builder type(OperationType type) {
            this.operationType = type; return this;
        }

        public Builder amount(float amount) {
            this.amount = amount; return this;
        }

        public Builder instant(Instant instant) {
            this.instant = instant; return this;
        }

        public Builder id(long id) {
            this.id = id; return this;
        }

        public Builder isPlaced(boolean place) {
            this.place = place; return this;
        }

        public Operation to() {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(receiver),
                    other = user == null ? null : Bukkit.getOfflinePlayer(user);

            return new Operation(target, other, instant, operationType, amount, id == -1 ? BANK_MANAGER.getRows(Operation.class) : id, place);
        }
    }

    public float getAmount() {
        return amount;
    }

    public Instant getInstant() {
        return instant;
    }

    public OfflinePlayer getReceiver() {
        return receiver;
    }

    public OfflinePlayer getUser() {
        return user;
    }

    public OperationType getType() {
        return operationType;
    }

    public long getId() {
        return id;
    }

    public boolean isAncestor(UUID uuid) {
        return receiver.getUniqueId().compareTo(uuid) == 0 || (user != null && user.getUniqueId().compareTo(uuid) == 0);
    }

    public boolean isPlace() {
        return place;
    }

    public void queue() {
        final Account account = BANK_MANAGER.searchBy(Account.class, receiver.getUniqueId());

        final Account target = user == null ? null : BANK_MANAGER.searchBy(Account.class, user.getUniqueId());

        new OperationCompleteEvent(this, account, target).call();
    }
}
